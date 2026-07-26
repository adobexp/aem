/**
 * Analytics Chart Component - AEM Client-Side JavaScript
 *
 * Renders dependency-free SVG charts from data attributes and animates them
 * when the panel scrolls into view. Every panel is self-describing:
 *
 *   data-chart-type    area | line | bar | hbar | stacked-bar | donut | gauge | sparkline
 *   data-chart-labels  JSON string[]  - category / x-axis labels
 *   data-chart-series  JSON Series[]  - [{ name, color, values: number[] }]
 *   data-chart-config  JSON Config    - { unit, yMax, total, valueFormat, showGrid, ... }
 */
(function() {
    'use strict';

    var SVG_NS = 'http://www.w3.org/2000/svg';

    var FALLBACK_PALETTE = [
        '#10b981',
        '#f97316',
        '#ec4899',
        '#14b8a6',
        '#f59e0b',
        '#6366f1',
        '#ef4444',
        '#84cc16'
    ];

    var PLOT = { width: 640, height: 260, left: 44, right: 12, top: 16, bottom: 28 };

    var REDUCED_MOTION = (function() {
        if (!window.matchMedia) {
            return false;
        }
        var query = window.matchMedia('(prefers-reduced-motion: reduce)');
        return !!(query && query.matches);
    })();

    /* ------------------------------------------------------------------ utils */

    /**
     * Create an SVG element with the given attributes
     * @param {string} name - SVG tag name
     * @param {Object} attrs - Attribute map, values are stringified
     * @returns {SVGElement} - The created element
     */
    function el(name, attrs) {
        var node = document.createElementNS(SVG_NS, name);
        if (attrs) {
            Object.keys(attrs).forEach(function(key) {
                node.setAttribute(key, String(attrs[key]));
            });
        }
        return node;
    }

    /**
     * Parse a JSON attribute, falling back when it is missing or malformed
     * @param {string|null} raw - Raw attribute value
     * @param {*} fallback - Value returned when parsing fails
     * @returns {*} - Parsed value or the fallback
     */
    function parseJson(raw, fallback) {
        if (!raw) {
            return fallback;
        }
        try {
            var parsed = JSON.parse(raw);
            return parsed === null ? fallback : parsed;
        } catch (e) {
            return fallback;
        }
    }

    /**
     * Coerce the parsed series payload into well-formed series objects
     * @param {Array} raw - Parsed value of data-chart-series
     * @returns {Array} - Series with a guaranteed numeric values array
     */
    function normaliseSeries(raw) {
        var series = [];
        if (!raw || typeof raw.length !== 'number') {
            return series;
        }
        for (var i = 0; i < raw.length; i += 1) {
            var entry = raw[i];
            if (!entry) {
                continue;
            }
            var values = [];
            var rawValues = entry.values;
            if (rawValues && typeof rawValues.length === 'number') {
                for (var j = 0; j < rawValues.length; j += 1) {
                    var value = typeof rawValues[j] === 'number' ? rawValues[j] : parseFloat(rawValues[j]);
                    if (!isNaN(value) && isFinite(value)) {
                        values.push(value);
                    }
                }
            }
            series.push({ name: entry.name, color: entry.color, values: values });
        }
        return series;
    }

    /**
     * Flatten every value across all series
     * @param {Array} series - Normalised series
     * @returns {Array} - Flat list of numbers
     */
    function allValues(series) {
        var values = [];
        series.forEach(function(entry) {
            entry.values.forEach(function(value) {
                values.push(value);
            });
        });
        return values;
    }

    /**
     * Largest value in the list, never below the given floor
     * @param {Array} values - List of numbers
     * @param {number} floor - Lower bound of the result
     * @returns {number} - The maximum
     */
    function maxOf(values, floor) {
        var max = floor;
        values.forEach(function(value) {
            if (value > max) {
                max = value;
            }
        });
        return max;
    }

    /**
     * Resolve the value axis maximum from the config or the data
     * @param {Object} config - Chart config
     * @param {number} dataMax - Largest plotted value
     * @param {boolean} nice - Whether to round the derived maximum up
     * @returns {number} - A strictly positive axis maximum
     */
    function resolveMax(config, dataMax, nice) {
        var max = typeof config.yMax === 'number' ? config.yMax : (nice ? niceMax(dataMax) : dataMax);
        if (!max || max <= 0 || !isFinite(max)) {
            max = 1;
        }
        return max;
    }

    /**
     * Round up to a readable axis maximum (1/2/2.5/5 x power of ten)
     * @param {number} value - Largest plotted value
     * @returns {number} - Rounded axis maximum
     */
    function niceMax(value) {
        if (value <= 0) {
            return 1;
        }
        var exponent = Math.floor(Math.log(value) / Math.LN10);
        var magnitude = Math.pow(10, exponent);
        var normalised = value / magnitude;
        var step = 10;
        if (normalised <= 1) {
            step = 1;
        } else if (normalised <= 2) {
            step = 2;
        } else if (normalised <= 2.5) {
            step = 2.5;
        } else if (normalised <= 5) {
            step = 5;
        }
        return step * magnitude;
    }

    /**
     * Format a terabyte figure as TB or GB
     * @param {number} terabytes - Value in terabytes
     * @returns {string} - Formatted size
     */
    function formatBytes(terabytes) {
        if (terabytes >= 1) {
            return terabytes.toFixed(terabytes >= 10 ? 1 : 2) + ' TB';
        }
        return (terabytes * 1024).toFixed(0) + ' GB';
    }

    /**
     * Format a number in compact notation (1.2K, 3.4M, 5.6B)
     * @param {number} value - Value to format
     * @returns {string} - Compact representation
     */
    function formatCompact(value) {
        var abs = Math.abs(value);
        if (abs >= 1e9) {
            return (value / 1e9).toFixed(1) + 'B';
        }
        if (abs >= 1e6) {
            return (value / 1e6).toFixed(1) + 'M';
        }
        if (abs >= 1e3) {
            return (value / 1e3).toFixed(abs >= 1e4 ? 0 : 1) + 'K';
        }
        return String(Math.round(value));
    }

    /**
     * Format a value according to the panel config
     * @param {number} value - Value to format
     * @param {Object} config - Chart config
     * @returns {string} - Formatted value
     */
    function formatValue(value, config) {
        var hasDecimals = typeof config.decimals === 'number';
        var decimals = hasDecimals ? config.decimals : 0;

        if (config.valueFormat === 'bytes') {
            return formatBytes(value);
        }
        if (config.valueFormat === 'percent') {
            return value.toFixed(decimals) + '%';
        }
        if (config.valueFormat === 'compact') {
            return formatCompact(value);
        }

        var formatted = value.toLocaleString('en-US', {
            minimumFractionDigits: decimals,
            maximumFractionDigits: hasDecimals ? decimals : 1
        });
        return formatted + (config.unit ? config.unit : '');
    }

    /**
     * Resolve the colour of a series, falling back to the shared palette
     * @param {Object} series - Series object
     * @param {number} index - Position used to pick a fallback colour
     * @returns {string} - CSS colour
     */
    function colorAt(series, index) {
        if (series && series.color) {
            return series.color;
        }
        return FALLBACK_PALETTE[index % FALLBACK_PALETTE.length];
    }

    /**
     * Build a path through the given points. Smooth mode uses a monotone-ish
     * cubic so trend lines stay readable without overshooting into negatives.
     * @param {Array} points - List of [x, y] pairs
     * @param {boolean} smooth - Whether to curve between points
     * @returns {string} - SVG path data
     */
    function buildLinePath(points, smooth) {
        if (points.length === 0) {
            return '';
        }
        if (points.length === 1 || !smooth) {
            return points.map(function(point, index) {
                return (index === 0 ? 'M' : 'L') + point[0].toFixed(2) + ',' + point[1].toFixed(2);
            }).join(' ');
        }

        var path = 'M' + points[0][0].toFixed(2) + ',' + points[0][1].toFixed(2);
        for (var i = 0; i < points.length - 1; i += 1) {
            var x0 = points[i][0];
            var y0 = points[i][1];
            var x1 = points[i + 1][0];
            var y1 = points[i + 1][1];
            var midX = (x0 + x1) / 2;
            path += ' C' + midX.toFixed(2) + ',' + y0.toFixed(2)
                + ' ' + midX.toFixed(2) + ',' + y1.toFixed(2)
                + ' ' + x1.toFixed(2) + ',' + y1.toFixed(2);
        }
        return path;
    }

    /**
     * Describe a circular arc as SVG path data
     * @param {number} cx - Centre x
     * @param {number} cy - Centre y
     * @param {number} radius - Arc radius
     * @param {number} startAngle - Start angle in degrees, 0 is twelve o'clock
     * @param {number} endAngle - End angle in degrees
     * @returns {string} - SVG path data
     */
    function describeArc(cx, cy, radius, startAngle, endAngle) {
        function toPoint(angle) {
            var radians = ((angle - 90) * Math.PI) / 180;
            return [cx + radius * Math.cos(radians), cy + radius * Math.sin(radians)];
        }
        var start = toPoint(startAngle);
        var end = toPoint(endAngle);
        var largeArc = endAngle - startAngle > 180 ? 1 : 0;
        return 'M' + start[0].toFixed(2) + ',' + start[1].toFixed(2)
            + ' A' + radius + ',' + radius + ' 0 ' + largeArc + ' 1 '
            + end[0].toFixed(2) + ',' + end[1].toFixed(2);
    }

    /**
     * Animate a stroke draw-on by transitioning stroke-dashoffset to zero
     * @param {SVGElement} path - Stroked element to animate
     * @param {number} delay - Delay in milliseconds
     * @param {number} duration - Duration in milliseconds
     */
    function drawOn(path, delay, duration) {
        if (!path || REDUCED_MOTION || typeof path.getTotalLength !== 'function') {
            return;
        }

        var length = 0;
        try {
            length = path.getTotalLength();
        } catch (e) {
            length = 0;
        }
        if (!length) {
            return;
        }

        path.style.strokeDasharray = String(length);
        path.style.strokeDashoffset = String(length);
        path.style.transition = 'stroke-dashoffset ' + duration + 'ms cubic-bezier(0.22, 1, 0.36, 1) ' + delay + 'ms';
        requestAnimationFrame(function() {
            path.style.strokeDashoffset = '0';
        });
    }

    /**
     * Fade an element in once it is attached
     * @param {Element} node - Element to fade
     * @param {number} delay - Delay in milliseconds
     */
    function fadeIn(node, delay) {
        if (!node || REDUCED_MOTION) {
            return;
        }
        node.style.opacity = '0';
        node.style.transition = 'opacity 600ms ease ' + delay + 'ms';
        requestAnimationFrame(function() {
            node.style.opacity = '1';
        });
    }

    /**
     * Count a numeric text node up to its final value
     * @param {HTMLElement} node - Element whose text content is animated
     * @param {number} to - Final value
     * @param {Object} config - Chart config used for formatting
     * @param {number} duration - Duration in milliseconds
     */
    function countUpValue(node, to, config, duration) {
        if (REDUCED_MOTION) {
            node.textContent = formatValue(to, config);
            return;
        }
        var start = performance.now();
        function tick(now) {
            var progress = Math.min((now - start) / duration, 1);
            var eased = 1 - Math.pow(1 - progress, 3);
            node.textContent = formatValue(to * eased, config);
            if (progress < 1) {
                requestAnimationFrame(tick);
            }
        }
        requestAnimationFrame(tick);
    }

    /* --------------------------------------------------------------- renderers */

    /**
     * Create the shared plot canvas
     * @returns {Object} - The svg element plus its usable inner dimensions
     */
    function createPlotSvg() {
        var svg = el('svg', {
            viewBox: '0 0 ' + PLOT.width + ' ' + PLOT.height,
            preserveAspectRatio: 'none',
            'class': 'analytics-chart__svg',
            role: 'presentation'
        });
        return {
            svg: svg,
            innerWidth: PLOT.width - PLOT.left - PLOT.right,
            innerHeight: PLOT.height - PLOT.top - PLOT.bottom
        };
    }

    /**
     * Draw the horizontal grid lines and the axis tick labels
     * @param {SVGElement} svg - Target svg
     * @param {number} maxValue - Value axis maximum
     * @param {Array} labels - Category labels
     * @param {Object} config - Chart config
     */
    function drawGridAndAxis(svg, maxValue, labels, config) {
        var innerWidth = PLOT.width - PLOT.left - PLOT.right;
        var innerHeight = PLOT.height - PLOT.top - PLOT.bottom;
        var tickCount = typeof config.ticks === 'number' && config.ticks > 0 ? config.ticks : 4;

        for (var i = 0; i <= tickCount; i += 1) {
            var ratio = i / tickCount;
            var y = PLOT.top + innerHeight * (1 - ratio);

            if (config.showGrid !== false) {
                svg.appendChild(el('line', {
                    x1: PLOT.left,
                    y1: y,
                    x2: PLOT.left + innerWidth,
                    y2: y,
                    'class': 'analytics-chart__gridline'
                }));
            }

            if (config.showAxis !== false) {
                var tickLabel = el('text', {
                    x: PLOT.left - 8,
                    y: y + 3.5,
                    'text-anchor': 'end',
                    'class': 'analytics-chart__tick'
                });
                tickLabel.textContent = formatCompact(maxValue * ratio);
                svg.appendChild(tickLabel);
            }
        }

        if (config.showAxis === false || labels.length === 0) {
            return;
        }

        // Show only first / middle / last x labels so they never collide.
        var indices;
        if (labels.length <= 3) {
            indices = labels.map(function(label, index) {
                return index;
            });
        } else {
            indices = [0, Math.floor((labels.length - 1) / 2), labels.length - 1];
        }

        var step = labels.length > 1 ? innerWidth / (labels.length - 1) : 0;
        indices.forEach(function(index) {
            var anchor = 'middle';
            if (index === 0) {
                anchor = 'start';
            } else if (index === labels.length - 1) {
                anchor = 'end';
            }
            var text = el('text', {
                x: PLOT.left + step * index,
                y: PLOT.height - 8,
                'text-anchor': anchor,
                'class': 'analytics-chart__tick'
            });
            text.textContent = labels[index];
            svg.appendChild(text);
        });
    }

    /**
     * Render an area or line chart
     * @param {Object} context - Render context
     * @param {boolean} filled - Whether to fill the area below the line
     */
    function renderTrend(context, filled) {
        var series = context.series;
        var config = context.config;
        var plot = createPlotSvg();
        var svg = plot.svg;
        var innerWidth = plot.innerWidth;
        var innerHeight = plot.innerHeight;

        var pointCount = 1;
        series.forEach(function(entry) {
            if (entry.values.length > pointCount) {
                pointCount = entry.values.length;
            }
        });

        var maxValue = resolveMax(config, maxOf(allValues(series), 0), true);
        var step = pointCount > 1 ? innerWidth / (pointCount - 1) : 0;

        drawGridAndAxis(svg, maxValue, context.labels, config);

        series.forEach(function(entry, seriesIndex) {
            var color = colorAt(entry, seriesIndex);
            var points = entry.values.map(function(value, index) {
                return [
                    PLOT.left + step * index,
                    PLOT.top + innerHeight * (1 - Math.min(value / maxValue, 1))
                ];
            });
            var linePath = buildLinePath(points, config.curve !== 'linear');

            if (filled && linePath) {
                var gradientId = 'ac-grad-' + Math.random().toString(36).slice(2, 9);
                var defs = el('defs');
                var gradient = el('linearGradient', { id: gradientId, x1: '0', y1: '0', x2: '0', y2: '1' });
                gradient.appendChild(el('stop', { offset: '0%', 'stop-color': color, 'stop-opacity': '0.45' }));
                gradient.appendChild(el('stop', { offset: '100%', 'stop-color': color, 'stop-opacity': '0.02' }));
                defs.appendChild(gradient);
                svg.appendChild(defs);

                var baseline = PLOT.top + innerHeight;
                var area = el('path', {
                    d: linePath
                        + ' L' + (PLOT.left + step * (pointCount - 1)).toFixed(2) + ',' + baseline
                        + ' L' + PLOT.left + ',' + baseline + ' Z',
                    fill: 'url(#' + gradientId + ')',
                    'class': 'analytics-chart__area'
                });
                svg.appendChild(area);
                fadeIn(area, 250);
            }

            var line = el('path', {
                d: linePath,
                fill: 'none',
                stroke: color,
                'stroke-width': '2',
                'stroke-linecap': 'round',
                'stroke-linejoin': 'round',
                'class': 'analytics-chart__line'
            });
            svg.appendChild(line);
            drawOn(line, seriesIndex * 140, 1400);
        });

        context.host.appendChild(svg);
    }

    /**
     * Render a grouped vertical bar chart
     * @param {Object} context - Render context
     */
    function renderBar(context) {
        var series = context.series;
        var config = context.config;
        var plot = createPlotSvg();
        var svg = plot.svg;
        var innerWidth = plot.innerWidth;
        var innerHeight = plot.innerHeight;

        var primary = series.length > 0 ? series[0] : { values: [] };
        var maxValue = resolveMax(config, maxOf(allValues(series), 0), true);

        drawGridAndAxis(svg, maxValue, context.labels, config);

        var groupCount = Math.max(primary.values.length, 1);
        var slot = innerWidth / groupCount;
        var barWidth = Math.max((slot * 0.62) / Math.max(series.length, 1), 2);
        var baseline = PLOT.top + innerHeight;

        series.forEach(function(entry, seriesIndex) {
            var color = colorAt(entry, seriesIndex);
            entry.values.forEach(function(value, index) {
                var height = innerHeight * Math.min(value / maxValue, 1);
                var x = PLOT.left + slot * index + slot * 0.19 + barWidth * seriesIndex;
                var rect = el('rect', {
                    x: x,
                    y: baseline - height,
                    width: barWidth,
                    height: Math.max(height, 0),
                    rx: Math.min(barWidth / 2, 3),
                    fill: color,
                    'class': 'analytics-chart__bar'
                });
                svg.appendChild(rect);

                if (!REDUCED_MOTION) {
                    rect.style.transformOrigin = 'center ' + baseline + 'px';
                    rect.style.transform = 'scaleY(0)';
                    rect.style.transition = 'transform 700ms cubic-bezier(0.22, 1, 0.36, 1) '
                        + (index * 35 + seriesIndex * 90) + 'ms';
                    requestAnimationFrame(function() {
                        rect.style.transform = 'scaleY(1)';
                    });
                }
            });
        });

        context.host.appendChild(svg);
    }

    /**
     * Render a ranked list of horizontal bars
     * @param {Object} context - Render context
     */
    function renderHorizontalBar(context) {
        var labels = context.labels;
        var config = context.config;
        var primary = context.series.length > 0 ? context.series[0] : { values: [] };
        var values = primary.values;
        var maxValue = resolveMax(config, maxOf(values, 1), false);

        var list = document.createElement('ul');
        list.className = 'analytics-chart__hbars';

        values.forEach(function(value, index) {
            var item = document.createElement('li');
            item.className = 'analytics-chart__hbar';

            var label = document.createElement('span');
            label.className = 'analytics-chart__hbar-label';
            label.textContent = labels[index] ? labels[index] : 'Item ' + (index + 1);

            var track = document.createElement('span');
            track.className = 'analytics-chart__hbar-track';

            var fill = document.createElement('span');
            fill.className = 'analytics-chart__hbar-fill';
            fill.style.background = colorAt(primary, index);
            var ratio = Math.min(value / maxValue, 1);
            track.appendChild(fill);

            var amount = document.createElement('span');
            amount.className = 'analytics-chart__hbar-value';
            amount.textContent = formatValue(value, config);

            item.appendChild(label);
            item.appendChild(track);
            item.appendChild(amount);
            list.appendChild(item);

            if (REDUCED_MOTION) {
                fill.style.width = (ratio * 100) + '%';
            } else {
                fill.style.width = '0%';
                fill.style.transition = 'width 900ms cubic-bezier(0.22, 1, 0.36, 1) ' + (index * 90) + 'ms';
                requestAnimationFrame(function() {
                    fill.style.width = (ratio * 100) + '%';
                });
            }
        });

        context.host.appendChild(list);
    }

    /**
     * Render a single stacked bar with a caption and a legend
     * @param {Object} context - Render context
     */
    function renderStackedBar(context) {
        var labels = context.labels;
        var config = context.config;
        var primary = context.series.length > 0 ? context.series[0] : { values: [] };
        var values = primary.values;

        var sum = 0;
        values.forEach(function(value) {
            sum += value;
        });
        var total = typeof config.total === 'number' ? config.total : sum;

        var wrap = document.createElement('div');
        wrap.className = 'analytics-chart__stack-wrap';

        var bar = document.createElement('div');
        bar.className = 'analytics-chart__stack';

        values.forEach(function(value, index) {
            var ratio = total > 0 ? value / total : 0;
            var segment = document.createElement('span');
            segment.className = 'analytics-chart__stack-seg';
            segment.style.background = colorAt(primary, index);
            segment.title = (labels[index] ? labels[index] : '') + ': ' + formatValue(value, config);

            var percent = document.createElement('em');
            percent.textContent = ratio >= 0.08 ? (ratio * 100).toFixed(1) + '%' : '';
            segment.appendChild(percent);
            bar.appendChild(segment);

            if (REDUCED_MOTION) {
                segment.style.flexBasis = (ratio * 100) + '%';
            } else {
                segment.style.flexBasis = '0%';
                segment.style.transition = 'flex-basis 900ms cubic-bezier(0.22, 1, 0.36, 1) ' + (index * 110) + 'ms';
                requestAnimationFrame(function() {
                    segment.style.flexBasis = (ratio * 100) + '%';
                });
            }
        });

        var caption = document.createElement('p');
        caption.className = 'analytics-chart__stack-caption';
        caption.textContent = config.totalLabel
            ? config.totalLabel
            : formatValue(sum, config) + ' of ' + formatValue(total, config);

        wrap.appendChild(bar);
        wrap.appendChild(caption);
        wrap.appendChild(buildValueLegend(labels, values, primary, config, false));
        context.host.appendChild(wrap);
    }

    /**
     * Render a donut chart with an optional centred readout
     * @param {Object} context - Render context
     */
    function renderDonut(context) {
        var labels = context.labels;
        var config = context.config;
        var primary = context.series.length > 0 ? context.series[0] : { values: [] };
        var values = primary.values;

        var total = 0;
        values.forEach(function(value) {
            total += value;
        });
        if (total <= 0) {
            total = 1;
        }

        var size = 220;
        var radius = 82;
        var svg = el('svg', {
            viewBox: '0 0 ' + size + ' ' + size,
            'class': 'analytics-chart__svg analytics-chart__svg--donut',
            role: 'presentation'
        });

        svg.appendChild(el('circle', {
            cx: size / 2,
            cy: size / 2,
            r: radius,
            fill: 'none',
            'stroke-width': '26',
            'class': 'analytics-chart__donut-track'
        }));

        var angle = 0;
        values.forEach(function(value, index) {
            var sweep = (value / total) * 360;
            if (sweep <= 0) {
                return;
            }
            // Leave a hairline gap between segments for legibility.
            var arc = el('path', {
                d: describeArc(size / 2, size / 2, radius, angle + 1, angle + Math.max(sweep - 1, 0.5)),
                fill: 'none',
                stroke: colorAt(primary, index),
                'stroke-width': '26',
                'stroke-linecap': 'butt'
            });
            svg.appendChild(arc);
            drawOn(arc, index * 130, 1000);
            angle += sweep;
        });

        var wrap = document.createElement('div');
        wrap.className = 'analytics-chart__donut-wrap';

        var figure = document.createElement('div');
        figure.className = 'analytics-chart__donut-figure';
        figure.appendChild(svg);

        if (config.centerValue || config.centerLabel) {
            var center = document.createElement('div');
            center.className = 'analytics-chart__donut-center';
            if (config.centerValue) {
                var centerValue = document.createElement('strong');
                centerValue.textContent = config.centerValue;
                center.appendChild(centerValue);
            }
            if (config.centerLabel) {
                var centerLabel = document.createElement('span');
                centerLabel.textContent = config.centerLabel;
                center.appendChild(centerLabel);
            }
            figure.appendChild(center);
            fadeIn(center, 500);
        }

        wrap.appendChild(figure);
        wrap.appendChild(buildValueLegend(labels, values, primary, config, true));
        context.host.appendChild(wrap);
    }

    /**
     * Build a legend listing each value against its category label
     * @param {Array} labels - Category labels
     * @param {Array} values - Plotted values
     * @param {Object} series - Series the colours are taken from
     * @param {Object} config - Chart config
     * @param {boolean} stacked - Whether to use the stacked legend layout
     * @returns {HTMLElement} - The legend list
     */
    function buildValueLegend(labels, values, series, config, stacked) {
        var legend = document.createElement('ul');
        legend.className = stacked
            ? 'analytics-chart__legend analytics-chart__legend--stacked'
            : 'analytics-chart__legend';

        values.forEach(function(value, index) {
            var item = document.createElement('li');
            item.className = 'analytics-chart__legend-item';

            var dot = document.createElement('span');
            dot.className = 'analytics-chart__legend-dot';
            dot.style.background = colorAt(series, index);

            var text = document.createElement('span');
            text.textContent = (labels[index] ? labels[index] : '') + ' · ' + formatValue(value, config);

            item.appendChild(dot);
            item.appendChild(text);
            legend.appendChild(item);
        });

        return legend;
    }

    /**
     * Render a half-circle gauge with an animated readout
     * @param {Object} context - Render context
     */
    function renderGauge(context) {
        var config = context.config;
        var primary = context.series.length > 0 ? context.series[0] : { values: [] };
        var value = primary.values.length > 0 ? primary.values[0] : 0;
        var max = typeof config.yMax === 'number' && config.yMax > 0 ? config.yMax : 100;
        var ratio = Math.max(Math.min(value / max, 1), 0);
        var size = 220;
        var radius = 84;

        var svg = el('svg', {
            viewBox: '0 0 ' + size + ' ' + (size * 0.66),
            'class': 'analytics-chart__svg analytics-chart__svg--gauge',
            role: 'presentation'
        });

        // Half-circle track from -90deg to +90deg.
        svg.appendChild(el('path', {
            d: describeArc(size / 2, size * 0.62, radius, -90, 90),
            fill: 'none',
            'stroke-width': '20',
            'stroke-linecap': 'round',
            'class': 'analytics-chart__donut-track'
        }));

        var arc = el('path', {
            d: describeArc(size / 2, size * 0.62, radius, -90, -90 + 180 * ratio),
            fill: 'none',
            stroke: colorAt(primary, 0),
            'stroke-width': '20',
            'stroke-linecap': 'round'
        });
        svg.appendChild(arc);
        drawOn(arc, 120, 1200);

        var wrap = document.createElement('div');
        wrap.className = 'analytics-chart__gauge-wrap';
        wrap.appendChild(svg);

        var readout = document.createElement('p');
        readout.className = 'analytics-chart__gauge-value';
        wrap.appendChild(readout);
        countUpValue(readout, value, config, 1400);

        if (config.centerLabel) {
            var label = document.createElement('p');
            label.className = 'analytics-chart__gauge-label';
            label.textContent = config.centerLabel;
            wrap.appendChild(label);
        }

        context.host.appendChild(wrap);
    }

    /**
     * Render a compact sparkline
     * @param {Object} context - Render context
     */
    function renderSparkline(context) {
        var config = context.config;
        var primary = context.series.length > 0 ? context.series[0] : { values: [] };
        var values = primary.values;
        var max = resolveMax(config, maxOf(values, 1), false);
        var width = 240;
        var height = 56;
        var step = values.length > 1 ? width / (values.length - 1) : 0;

        var svg = el('svg', {
            viewBox: '0 0 ' + width + ' ' + height,
            'class': 'analytics-chart__svg analytics-chart__svg--spark',
            preserveAspectRatio: 'none',
            role: 'presentation'
        });

        var points = values.map(function(value, index) {
            return [
                step * index,
                height - (height - 6) * Math.min(value / max, 1) - 3
            ];
        });

        var line = el('path', {
            d: buildLinePath(points, true),
            fill: 'none',
            stroke: colorAt(primary, 0),
            'stroke-width': '2',
            'stroke-linecap': 'round'
        });
        svg.appendChild(line);
        drawOn(line, 0, 1100);

        context.host.appendChild(svg);
    }

    var RENDERERS = {
        area: function(context) {
            renderTrend(context, true);
        },
        line: function(context) {
            renderTrend(context, false);
        },
        bar: renderBar,
        hbar: renderHorizontalBar,
        'stacked-bar': renderStackedBar,
        donut: renderDonut,
        gauge: renderGauge,
        sparkline: renderSparkline
    };

    /* ------------------------------------------------------------------- setup */

    /**
     * Render the chart described by a single panel
     * @param {HTMLElement} panel - The .analytics-chart__panel element
     */
    function renderPanel(panel) {
        if (!panel) {
            return;
        }

        var host = panel.querySelector('.analytics-chart__canvas');
        if (!host || host.getAttribute('data-rendered') === 'true') {
            return;
        }

        var type = panel.getAttribute('data-chart-type') || 'area';
        var renderer = RENDERERS[type];
        if (!renderer) {
            return;
        }

        var labels = parseJson(panel.getAttribute('data-chart-labels'), []);
        var series = normaliseSeries(parseJson(panel.getAttribute('data-chart-series'), []));
        var config = parseJson(panel.getAttribute('data-chart-config'), {});
        if (!labels || typeof labels.length !== 'number') {
            labels = [];
        }
        if (!config || typeof config !== 'object') {
            config = {};
        }

        host.innerHTML = '';
        renderer({ host: host, labels: labels, series: series, config: config });
        host.setAttribute('data-rendered', 'true');

        if (config.showLegend && series.length > 1 && !host.querySelector('.analytics-chart__legend')) {
            var legend = document.createElement('ul');
            legend.className = 'analytics-chart__legend';
            series.forEach(function(entry, index) {
                var item = document.createElement('li');
                item.className = 'analytics-chart__legend-item';

                var dot = document.createElement('span');
                dot.className = 'analytics-chart__legend-dot';
                dot.style.background = colorAt(entry, index);

                var text = document.createElement('span');
                text.textContent = entry.name ? entry.name : 'Series ' + (index + 1);

                item.appendChild(dot);
                item.appendChild(text);
                legend.appendChild(item);
            });
            host.appendChild(legend);
        }

        panel.classList.add('is-visible');
    }

    /**
     * Initialize a single analytics chart section
     * @param {HTMLElement} section - The analytics chart section element
     */
    function initAnalyticsChartComponent(section) {
        if (!section) {
            return;
        }

        var panels = section.querySelectorAll('.analytics-chart__panel');
        if (panels.length === 0) {
            return;
        }

        if (REDUCED_MOTION || !('IntersectionObserver' in window)) {
            panels.forEach(function(panel) {
                renderPanel(panel);
            });
            return;
        }

        var observer = new IntersectionObserver(
            function(entries) {
                entries.forEach(function(entry) {
                    if (!entry.isIntersecting) {
                        return;
                    }
                    renderPanel(entry.target);
                    observer.unobserve(entry.target);
                });
            },
            {
                threshold: 0.2, // Trigger when 20% of the panel is visible
                rootMargin: '0px 0px -60px 0px' // Slight offset from bottom
            }
        );

        panels.forEach(function(panel, index) {
            panel.style.setProperty('--i', String(index));
            observer.observe(panel);
        });
    }

    /**
     * Initialize all analytics chart components on the page
     */
    function initAllAnalyticsChartComponents() {
        var sections = document.querySelectorAll('[data-component="analytics-chart"]');
        sections.forEach(function(section) {
            initAnalyticsChartComponent(section);
        });
    }

    // Initialize on DOMContentLoaded or immediately if already loaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllAnalyticsChartComponents);
    } else {
        initAllAnalyticsChartComponents();
    }

    // Re-initialize on AEM author mode content refresh (for edit mode support)
    if (typeof Granite !== 'undefined' && Granite.author && Granite.author.MessageChannel) {
        new Granite.author.MessageChannel('cqauthor', window).subscribeRequestMessage('cmp.panelcontainer', function() {
            initAllAnalyticsChartComponents();
        });
    }
})();
