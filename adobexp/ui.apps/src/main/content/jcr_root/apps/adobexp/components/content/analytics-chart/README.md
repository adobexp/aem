# Analytics Chart

AEM Component for displaying dependency-free SVG report panels (area, line, bar, horizontal bar,
stacked bar, donut, gauge and sparkline) that animate when scrolled into view.

## Configuration

This component can be configured through the following tabs:

### Content
- **Title**: Section title above the report panels
- **Subtitle**: Supporting text below the title
- **Accessible Label**: Screen reader label for the whole section (default: 'Analytics reports')

### Panels
- **Chart Panels**: List of report panels, one chart per panel
  - **Panel Title**: Heading of the panel (e.g. 'Unique visitors per day')
  - **Badge**: Short type chip shown next to the panel title (e.g. 'Trend')
  - **Description**: Supporting copy below the heading, supports rich text
  - **Chart Type**: Area, Line, Bar, Horizontal Bar, Stacked Bar, Donut, Gauge or Sparkline (default: Area)
  - **Labels**: Category labels. One label per line, or comma separated. Example: `Mon, Tue, Wed`
  - **Series**: One series per line, pipe delimited: `Name|#colour|v1,v2,v3`. Both the name and the
    colour are optional, so `|#10b981|1,2,3` and `1,2,3` also work.
    Example: `Unique users|#10b981|12,18,24,19`

## Colours

A chart is coloured either by series or by category, and **Colour By** (default *Automatic*)
decides which:

| Chart | Automatic behaviour |
| --- | --- |
| Donut, stacked bar, horizontal bar | By category — every value is a separate slice |
| Bar with one series | By category — the bars are categories, not a trend |
| Bar with several series, area, line | By series — the series have to be told apart |
| Gauge, sparkline | Single value, series colour |

Charts coloured by series take their colour from the **Series** field. Charts coloured by
category take one colour per category from the site palette, cycling it when there are more
categories than colours; the series colour is ignored because a single colour would make the
slices indistinguishable. The palette is set per site in the **AdobeXP - Analytics Chart Theme
Configuration** context-aware configuration (*Analytics Chart Category Colours*, dark and
light), and reaches the browser as `--analytics-chart-cat-1` … `--analytics-chart-cat-8`.

To override the palette on one panel, list one colour per category in the colour slot,
separated by `/`:

```
Sessions|#f4c15e/#5b9dff/#4ecdc4|58,34,8
```

Set **Colour By** to *Series* to keep a single-series bar chart in one colour.

Because the palette is referenced as `var(--analytics-chart-cat-N)` rather than a resolved
value, charts recolour themselves when the visitor toggles between dark and light without
being redrawn.
  - **Unit**: Suffix appended to plain numbers (e.g. 'ms')
  - **Value Format**: Number, Compact (1.2K), Bytes (TB / GB) or Percent (default: Number)
  - **Decimal Places**: Decimal places used when formatting values
  - **Axis Maximum**: Fixes the top of the value axis; gauges use it as 100% (default: 100)
  - **Total**: Stacked bar only, the capacity the segments are measured against
  - **Total Label**: Stacked bar only, caption below the bar
  - **Center Label**: Donut and gauge only, caption under the centred value
  - **Center Value**: Donut only, headline value shown in the middle of the ring (e.g. '58%')
  - **Curve**: Area and line only, Smooth or Linear (default: Smooth)
  - **Show Grid Lines**: Draws the horizontal grid lines behind plotted charts (default: on)
  - **Show Legend**: Adds a legend when more than one series is plotted
  - **Show Axis Labels**: Draws the value and category tick labels (default: on)
  - **Chart Accessible Label**: Screen reader description of the chart, falls back to the panel title

### Appearance
- **Section Style**: None, With Background or Muted Background
- **Grid Columns**: Number of panel columns on large screens (default: 2 Columns)

## Usage

This component can be added to AEM pages through the component console.
Configure the fields according to the tab structure above to customize the component's appearance and behavior.

The Sling model serialises each panel into the `data-chart-labels`, `data-chart-series` and
`data-chart-config` attributes. The client library reads those payloads and draws the SVG into the
empty `.analytics-chart__canvas` element once the panel scrolls into view.
