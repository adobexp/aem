/**
 * Video Component JavaScript
 * Handles video playback controls, teaser overlay animation, fullscreen mode, and visibility-based autoplay
 */
(function() {
    'use strict';

    var INITIALIZED_ATTR = 'data-video-initialized';

    /**
     * Initialize a single video component
     */
    var initVideoComponent = function(root) {
        // Prevent double initialization
        if (root.hasAttribute(INITIALIZED_ATTR)) return;
        root.setAttribute(INITIALIZED_ATTR, 'true');

        var video = root.querySelector('video');
        if (!video) return;

        // Read data attributes
        var title = (root.getAttribute('data-video-title') || '').trim();
        var description = (root.getAttribute('data-video-description') || '').trim();
        var href = (root.getAttribute('data-video-href') || '').trim();
        var linkTarget = (root.getAttribute('data-video-target') || '').trim();
        var muteAttr = (root.getAttribute('data-show-mute-toggle') || '').trim().toLowerCase();
        var showMuteToggle = !['false', '0', 'no', 'off'].includes(muteAttr);
        var playAttr = (root.getAttribute('data-show-play-toggle') || '').trim().toLowerCase();
        var showPlayToggle = !['false', '0', 'no', 'off'].includes(playAttr);
        var fullscreenAttr = (root.getAttribute('data-show-fullscreen-toggle') || '').trim().toLowerCase();
        var showFullscreenToggle = !['false', '0', 'no', 'off'].includes(fullscreenAttr);

        // SVG Icons
        var speakerOnSvg = '<svg aria-hidden="true" viewBox="0 0 24 24" width="18" height="18" fill="none">' +
            '<path d="M3 10v4c0 1.1.9 2 2 2h3l5 4V4L8 8H5c-1.1 0-2 .9-2 2Z" fill="currentColor"/>' +
            '<path d="M16.5 7.5c1.5 1.5 1.5 7.5 0 9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>' +
            '<path d="M19.5 4.5c3 3 3 12 0 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>' +
            '</svg>';

        var speakerOffSvg = '<svg aria-hidden="true" viewBox="0 0 24 24" width="18" height="18" fill="none">' +
            '<path d="M3 10v4c0 1.1.9 2 2 2h3l5 4V4L8 8H5c-1.1 0-2 .9-2 2Z" fill="currentColor"/>' +
            '<path d="M18 9l-6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>' +
            '<path d="M12 9l6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>' +
            '</svg>';

        var playSvg = '<svg aria-hidden="true" viewBox="0 0 24 24" width="18" height="18" fill="none">' +
            '<path d="M9 7.5v9l8-4.5-8-4.5Z" fill="currentColor"/>' +
            '</svg>';

        var pauseSvg = '<svg aria-hidden="true" viewBox="0 0 24 24" width="18" height="18" fill="none">' +
            '<path d="M7 6h3v12H7V6Z" fill="currentColor"/>' +
            '<path d="M14 6h3v12h-3V6Z" fill="currentColor"/>' +
            '</svg>';

        var fullscreenEnterSvg = '<svg aria-hidden="true" viewBox="0 0 24 24" width="18" height="18" fill="none">' +
            '<path d="M3 3h6v2H5v4H3V3zm12 0h6v6h-2V5h-4V3zM3 15h2v4h4v2H3v-6zm16 0h2v6h-6v-2h4v-4z" fill="currentColor"/>' +
            '</svg>';

        var fullscreenExitSvg = '<svg aria-hidden="true" viewBox="0 0 24 24" width="18" height="18" fill="none">' +
            '<path d="M9 3v6H3v-2h4V3h2zm6 0h2v4h4v2h-6V3zM3 15h6v6H7v-4H3v-2zm12 0h6v2h-4v4h-2v-6z" fill="currentColor"/>' +
            '</svg>';

        /**
         * Create or get teaser overlay element
         */
        var ensureTeaserOverlay = function() {
            if (!title && !description) return null;

            var existing = root.querySelector('.video__teaser-overlay');
            if (existing) return existing;

            var overlay = document.createElement(href ? 'a' : 'div');
            overlay.className = 'video__teaser-overlay';

            if (href && overlay.tagName === 'A') {
                overlay.classList.add('video__teaser-overlay--link');
                overlay.href = href;
                if (linkTarget) overlay.target = linkTarget;
                if (overlay.target === '_blank') overlay.rel = 'noreferrer noopener';
                overlay.setAttribute('aria-label', title ? 'Open: ' + title : 'Open');
            }

            if (title) {
                var h3 = document.createElement('h3');
                h3.className = 'video__teaser-title';
                h3.textContent = title;
                overlay.appendChild(h3);
            }

            if (description) {
                var p = document.createElement('p');
                p.className = 'video__teaser-description';
                p.textContent = description;
                overlay.appendChild(p);
            }

            video.insertAdjacentElement('beforebegin', overlay);
            return overlay;
        };

        /**
         * Update collapsed height CSS variable for teaser animation
         */
        var scheduleTeaserCollapsedHeightUpdate = function(overlayEl) {
            if (!overlayEl) return;
            var titleEl = overlayEl.querySelector('.video__teaser-title');
            if (!titleEl) return;

            requestAnimationFrame(function() {
                requestAnimationFrame(function() {
                    var titleHeight = Math.ceil(titleEl.getBoundingClientRect().height);
                    var collapsedHeight = Math.max(60, titleHeight + 20);
                    overlayEl.style.setProperty('--video-collapsed-height', collapsedHeight + 'px');
                });
            });
        };

        /**
         * Update expanded height CSS variable for teaser animation
         */
        var scheduleTeaserExpandedHeightUpdate = function(overlayEl) {
            if (!overlayEl) return;

            requestAnimationFrame(function() {
                requestAnimationFrame(function() {
                    var prevPadding = overlayEl.style.padding;
                    overlayEl.style.padding = '24px';
                    var expandedHeight = Math.ceil(overlayEl.scrollHeight);
                    overlayEl.style.padding = prevPadding;

                    var collapsedStr = overlayEl.style.getPropertyValue('--video-collapsed-height') || '';
                    var collapsed = parseInt(collapsedStr, 10);
                    var safeExpanded = !isNaN(collapsed) && isFinite(collapsed) ? Math.max(expandedHeight, collapsed) : expandedHeight;
                    overlayEl.style.setProperty('--video-expanded-height', safeExpanded + 'px');
                });
            });
        };

        // Initialize teaser overlay
        var teaserOverlayEl = ensureTeaserOverlay();
        scheduleTeaserCollapsedHeightUpdate(teaserOverlayEl);
        scheduleTeaserExpandedHeightUpdate(teaserOverlayEl);

        // Observe resize changes for teaser
        if (teaserOverlayEl && 'ResizeObserver' in window) {
            var ro = new ResizeObserver(function() {
                scheduleTeaserCollapsedHeightUpdate(teaserOverlayEl);
                scheduleTeaserExpandedHeightUpdate(teaserOverlayEl);
            });
            ro.observe(teaserOverlayEl);
        }

        // Video configuration
        video.controls = false;
        video.muted = true;
        video.defaultMuted = true;
        video.playsInline = true;
        video.setAttribute('controlsList', 'nodownload noplaybackrate noremoteplayback');
        video.setAttribute('disablePictureInPicture', 'true');

        var userPaused = false;
        var visibilityPaused = false;

        // Handle mute toggle button
        var existingMuteToggleBtn = root.querySelector('[data-video-mute-toggle]');
        if (!showMuteToggle && existingMuteToggleBtn) existingMuteToggleBtn.remove();
        var muteToggleBtn = showMuteToggle ? existingMuteToggleBtn : null;

        // Handle play toggle button
        var existingPlayToggleBtn = root.querySelector('[data-video-play-toggle]');
        if (!showPlayToggle && existingPlayToggleBtn) existingPlayToggleBtn.remove();
        var playToggleBtn = showPlayToggle ? existingPlayToggleBtn : null;

        // Handle fullscreen toggle button
        var existingFullscreenToggleBtn = root.querySelector('[data-video-fullscreen-toggle]');
        if (!showFullscreenToggle && existingFullscreenToggleBtn) existingFullscreenToggleBtn.remove();
        var fullscreenToggleBtn = showFullscreenToggle ? existingFullscreenToggleBtn : null;

        // Fullscreen control bar elements
        var controlBar = null;
        var controlBarPlayBtn = null;
        var controlBarMuteBtn = null;
        var controlBarExitFullscreenBtn = null;
        var progressContainer = null;
        var progressBar = null;
        var progressSlider = null;
        var currentTimeDisplay = null;
        var durationDisplay = null;
        var hideControlBarTimeout = null;

        /**
         * Format seconds to MM:SS string
         */
        var formatTime = function(seconds) {
            var mins = Math.floor(seconds / 60);
            var secs = Math.floor(seconds % 60);
            return mins + ':' + (secs < 10 ? '0' : '') + secs;
        };

        /**
         * Create the fullscreen control bar
         */
        var createFullscreenControlBar = function() {
            if (controlBar) return;

            controlBar = document.createElement('div');
            controlBar.className = 'video__fullscreen-control-bar';

            // Play/Pause button
            controlBarPlayBtn = document.createElement('button');
            controlBarPlayBtn.className = 'video__control-btn video__control-play';
            controlBarPlayBtn.type = 'button';
            controlBarPlayBtn.setAttribute('aria-label', 'Pause video');
            controlBarPlayBtn.innerHTML = pauseSvg;

            // Mute/Unmute button
            controlBarMuteBtn = document.createElement('button');
            controlBarMuteBtn.className = 'video__control-btn video__control-mute';
            controlBarMuteBtn.type = 'button';
            controlBarMuteBtn.setAttribute('aria-label', 'Unmute video');
            controlBarMuteBtn.innerHTML = speakerOffSvg;

            // Progress container
            progressContainer = document.createElement('div');
            progressContainer.className = 'video__progress-container';

            currentTimeDisplay = document.createElement('span');
            currentTimeDisplay.className = 'video__time video__time--current';
            currentTimeDisplay.textContent = '0:00';

            var progressWrapper = document.createElement('div');
            progressWrapper.className = 'video__progress-wrapper';

            progressBar = document.createElement('div');
            progressBar.className = 'video__progress-bar';

            progressSlider = document.createElement('input');
            progressSlider.type = 'range';
            progressSlider.className = 'video__progress-slider';
            progressSlider.min = '0';
            progressSlider.max = '100';
            progressSlider.value = '0';
            progressSlider.setAttribute('aria-label', 'Video progress');

            progressWrapper.appendChild(progressBar);
            progressWrapper.appendChild(progressSlider);

            durationDisplay = document.createElement('span');
            durationDisplay.className = 'video__time video__time--duration';
            durationDisplay.textContent = '0:00';

            progressContainer.appendChild(currentTimeDisplay);
            progressContainer.appendChild(progressWrapper);
            progressContainer.appendChild(durationDisplay);

            // Exit fullscreen button
            controlBarExitFullscreenBtn = document.createElement('button');
            controlBarExitFullscreenBtn.className = 'video__control-btn video__control-exit-fullscreen';
            controlBarExitFullscreenBtn.type = 'button';
            controlBarExitFullscreenBtn.setAttribute('aria-label', 'Exit fullscreen');
            controlBarExitFullscreenBtn.innerHTML = fullscreenExitSvg;

            controlBar.appendChild(controlBarPlayBtn);
            controlBar.appendChild(controlBarMuteBtn);
            controlBar.appendChild(progressContainer);
            controlBar.appendChild(controlBarExitFullscreenBtn);

            root.appendChild(controlBar);

            // Control bar event listeners
            controlBarPlayBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                if (video.paused) {
                    userPaused = false;
                    visibilityPaused = false;
                    playSafe();
                } else {
                    userPaused = true;
                    video.pause();
                }
                syncAllPlayUi();
            });

            controlBarMuteBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                var nextMuted = !video.muted;
                video.muted = nextMuted;
                if (!nextMuted && video.volume === 0) video.volume = 1;
                syncAllMuteUi();
            });

            controlBarExitFullscreenBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                exitFullscreen();
            });

            progressSlider.addEventListener('input', function(e) {
                var seekTime = (parseFloat(e.target.value) / 100) * video.duration;
                video.currentTime = seekTime;
                updateProgressBar();
            });

            progressSlider.addEventListener('change', function() {
                // Resume playing after seeking if wasn't paused by user
                if (!userPaused && video.paused) {
                    playSafe();
                }
            });
        };

        /**
         * Update the progress bar display
         */
        var updateProgressBar = function() {
            if (!progressBar || !progressSlider || !currentTimeDisplay || !durationDisplay) return;

            var currentTime = video.currentTime;
            var duration = video.duration || 0;
            var percentage = duration > 0 ? (currentTime / duration) * 100 : 0;

            progressBar.style.width = percentage + '%';
            progressSlider.value = percentage.toString();
            currentTimeDisplay.textContent = formatTime(currentTime);
            durationDisplay.textContent = formatTime(duration);
        };

        /**
         * Show the control bar with auto-hide timeout
         */
        var showControlBar = function() {
            if (!controlBar) return;
            controlBar.classList.add('video__fullscreen-control-bar--visible');

            if (hideControlBarTimeout) {
                clearTimeout(hideControlBarTimeout);
            }

            hideControlBarTimeout = window.setTimeout(function() {
                if (controlBar && !video.paused) {
                    controlBar.classList.remove('video__fullscreen-control-bar--visible');
                }
            }, 3000);
        };

        /**
         * Sync control bar play button UI
         */
        var syncControlBarPlayUi = function() {
            if (!controlBarPlayBtn) return;
            var isPaused = video.paused;
            controlBarPlayBtn.setAttribute('aria-label', isPaused ? 'Play video' : 'Pause video');
            controlBarPlayBtn.innerHTML = isPaused ? playSvg : pauseSvg;
        };

        /**
         * Sync control bar mute button UI
         */
        var syncControlBarMuteUi = function() {
            if (!controlBarMuteBtn) return;
            var isMuted = video.muted || video.volume === 0;
            controlBarMuteBtn.setAttribute('aria-label', isMuted ? 'Unmute video' : 'Mute video');
            controlBarMuteBtn.innerHTML = isMuted ? speakerOffSvg : speakerOnSvg;
        };

        /**
         * Sync all play UI elements
         */
        var syncAllPlayUi = function() {
            syncPlayUi();
            syncControlBarPlayUi();
        };

        /**
         * Sync all mute UI elements
         */
        var syncAllMuteUi = function() {
            syncMuteUi();
            syncControlBarMuteUi();
        };

        // Fullscreen state
        var isInFullscreen = false;

        /**
         * Check if currently in fullscreen mode
         */
        var checkIsFullscreen = function() {
            return !!(
                document.fullscreenElement ||
                document.webkitFullscreenElement ||
                document.mozFullScreenElement ||
                document.msFullscreenElement
            );
        };

        /**
         * Enter fullscreen mode
         */
        var enterFullscreen = function() {
            try {
                createFullscreenControlBar();

                if (root.requestFullscreen) {
                    root.requestFullscreen();
                } else if (root.webkitRequestFullscreen) {
                    root.webkitRequestFullscreen();
                } else if (root.mozRequestFullScreen) {
                    root.mozRequestFullScreen();
                } else if (root.msRequestFullscreen) {
                    root.msRequestFullscreen();
                }
            } catch (err) {
                // Fullscreen request failed
            }
        };

        /**
         * Exit fullscreen mode
         */
        var exitFullscreen = function() {
            try {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.webkitExitFullscreen) {
                    document.webkitExitFullscreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            } catch (err) {
                // Exit fullscreen failed
            }
        };

        /**
         * Handle fullscreen change events
         */
        var handleFullscreenChange = function() {
            isInFullscreen = checkIsFullscreen();
            root.classList.toggle('video--fullscreen', isInFullscreen);

            if (fullscreenToggleBtn) {
                fullscreenToggleBtn.setAttribute('aria-label', isInFullscreen ? 'Exit fullscreen' : 'Enter fullscreen');
                fullscreenToggleBtn.innerHTML = isInFullscreen ? fullscreenExitSvg : fullscreenEnterSvg;
            }

            if (isInFullscreen) {
                showControlBar();
                syncControlBarPlayUi();
                syncControlBarMuteUi();
                updateProgressBar();
            } else {
                if (controlBar) {
                    controlBar.classList.remove('video__fullscreen-control-bar--visible');
                }
                if (hideControlBarTimeout) {
                    clearTimeout(hideControlBarTimeout);
                    hideControlBarTimeout = null;
                }
            }
        };

        // Fullscreen event listeners
        document.addEventListener('fullscreenchange', handleFullscreenChange);
        document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
        document.addEventListener('mozfullscreenchange', handleFullscreenChange);
        document.addEventListener('MSFullscreenChange', handleFullscreenChange);

        // Show control bar on mouse movement in fullscreen
        root.addEventListener('mousemove', function() {
            if (isInFullscreen) {
                showControlBar();
            }
        });

        root.addEventListener('touchstart', function() {
            if (isInFullscreen) {
                showControlBar();
            }
        });

        // Update progress bar on time update
        video.addEventListener('timeupdate', function() {
            if (isInFullscreen) {
                updateProgressBar();
            }
        });

        video.addEventListener('loadedmetadata', function() {
            if (durationDisplay) {
                durationDisplay.textContent = formatTime(video.duration);
            }
        });

        // Fullscreen toggle button click handler
        if (fullscreenToggleBtn) {
            fullscreenToggleBtn.innerHTML = fullscreenEnterSvg;
            fullscreenToggleBtn.setAttribute('aria-label', 'Enter fullscreen');

            fullscreenToggleBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                if (isInFullscreen) {
                    exitFullscreen();
                } else {
                    enterFullscreen();
                }
            });
        }

        // Prevent context menu on video
        video.addEventListener('contextmenu', function(e) {
            e.preventDefault();
        });

        // Prevent click from triggering default video behavior
        video.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
        });

        /**
         * Safe play function with error handling
         */
        var playSafe = function() {
            try {
                var playPromise = video.play();
                if (playPromise !== undefined) {
                    playPromise.catch(function() {
                        // Autoplay was prevented, silently fail
                    });
                }
            } catch (e) {
                // Play failed, silently fail
            }
        };

        /**
         * Sync mute button UI with video state
         */
        var syncMuteUi = function() {
            if (!muteToggleBtn) return;
            var isMuted = video.muted || video.volume === 0;
            muteToggleBtn.setAttribute('aria-label', isMuted ? 'Unmute video' : 'Mute video');
            muteToggleBtn.innerHTML = isMuted ? speakerOffSvg : speakerOnSvg;
        };

        /**
         * Sync play button UI with video state
         */
        var syncPlayUi = function() {
            if (!playToggleBtn) return;
            var isPaused = video.paused;
            playToggleBtn.setAttribute('aria-label', isPaused ? 'Play video' : 'Pause video');
            playToggleBtn.innerHTML = isPaused ? playSvg : pauseSvg;
        };

        // Mute toggle click handler
        if (muteToggleBtn) {
            muteToggleBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                var nextMuted = !video.muted;
                video.muted = nextMuted;
                if (!nextMuted && video.volume === 0) video.volume = 1;
                syncAllMuteUi();
            });
        }

        // Play toggle click handler
        if (playToggleBtn) {
            playToggleBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                if (video.paused) {
                    userPaused = false;
                    visibilityPaused = false;
                    playSafe();
                } else {
                    userPaused = true;
                    video.pause();
                }
                syncAllPlayUi();
            });
        }

        // Video pause handler - auto-resume if not user initiated
        video.addEventListener('pause', function() {
            syncAllPlayUi();
            if (isInFullscreen) {
                showControlBar();
            }
            if (userPaused || visibilityPaused) return;
            window.setTimeout(function() {
                if (video.paused) playSafe();
            }, 150);
        });

        // Video play handler
        video.addEventListener('play', function() {
            userPaused = false;
            visibilityPaused = false;
            syncAllPlayUi();
        });

        // Intersection Observer for visibility-based playback
        if ('IntersectionObserver' in window) {
            var observer = new IntersectionObserver(
                function(entries) {
                    var entry = entries[0];
                    if (!entry) return;

                    if (entry.isIntersecting && entry.intersectionRatio >= 0.25) {
                        if (!userPaused) {
                            visibilityPaused = false;
                            playSafe();
                        }
                    } else {
                        if (!video.paused) {
                            visibilityPaused = true;
                            video.pause();
                        }
                    }
                },
                { threshold: [0, 0.25, 0.5, 1] }
            );
            observer.observe(root);
        } else {
            playSafe();
        }

        // Initialize UI state
        if (muteToggleBtn) {
            video.addEventListener('volumechange', syncAllMuteUi);
            syncMuteUi();
        }

        if (playToggleBtn) {
            syncPlayUi();
        }

        // Initial sync for fullscreen control bar if toggle is shown
        if (fullscreenToggleBtn) {
            video.addEventListener('volumechange', syncControlBarMuteUi);
        }
    };

    /**
     * Initialize all video components on the page
     */
    var initAllVideoComponents = function() {
        var roots = document.querySelectorAll('[data-component="video"]');
        roots.forEach(initVideoComponent);
    };

    // Initialize all existing video components
    initAllVideoComponents();

    // Watch for dynamically added video components
    if ('MutationObserver' in window) {
        var mutationObserver = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                mutation.addedNodes.forEach(function(node) {
                    if (node instanceof HTMLElement) {
                        if (node.matches('[data-component="video"]')) {
                            initVideoComponent(node);
                        }
                        node.querySelectorAll('[data-component="video"]').forEach(initVideoComponent);
                    }
                });
            });
        });
        mutationObserver.observe(document.body, {
            childList: true,
            subtree: true
        });
    }
})();
