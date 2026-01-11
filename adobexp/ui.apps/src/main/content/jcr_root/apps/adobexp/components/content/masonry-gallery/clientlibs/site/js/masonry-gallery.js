/**
 * Masonry Gallery Component JavaScript
 * Handles video auto-play on hover (desktop) or intersection (mobile)
 * and manages video start times
 */
(function() {
    'use strict';

    var INITIALIZED_ATTR = 'data-masonry-gallery-initialized';

    /**
     * Initialize a single masonry gallery instance
     */
    var initMasonryGallery = function(root) {
        if (root.hasAttribute(INITIALIZED_ATTR)) return;
        root.setAttribute(INITIALIZED_ATTR, 'true');

        var prefersReduced = typeof window !== 'undefined' && 
            window.matchMedia && 
            window.matchMedia('(prefers-reduced-motion: reduce)').matches;

        var videoItems = [];
        var items = root.querySelectorAll('.masonry-gallery__item');

        items.forEach(function(item) {
            var video = item.querySelector('.masonry-gallery__video');
            if (!video) return;

            var wrapper = item.querySelector('.masonry-gallery__video-wrapper');
            if (!wrapper) return;

            var startTimeAttr = item.getAttribute('data-video-start') || '0';
            var startTime = parseFloat(startTimeAttr) || 0;
            var thumbnail = item.querySelector('.masonry-gallery__thumbnail');

            // Configure video element
            video.controls = false;
            video.muted = true;
            video.defaultMuted = true;
            video.volume = 0;
            video.playsInline = true;
            video.loop = true;
            video.preload = 'metadata';
            video.setAttribute('controlsList', 'nodownload noplaybackrate noremoteplayback nofullscreen');
            video.setAttribute('disablePictureInPicture', 'true');
            video.setAttribute('disableRemotePlayback', 'true');
            video.removeAttribute('controls');

            // Prevent unmuting
            video.addEventListener('volumechange', function() {
                if (!video.muted || video.volume > 0) {
                    video.muted = true;
                    video.volume = 0;
                }
            });

            // Prevent context menu
            video.addEventListener('contextmenu', function(e) {
                e.preventDefault();
            });

            // Prevent click through
            video.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
            });

            // Handle metadata loaded
            video.addEventListener('loadedmetadata', function() {
                video.muted = true;
                video.volume = 0;
                if (startTime > 0 && startTime < video.duration) {
                    video.currentTime = startTime;
                }
                if (!thumbnail && !prefersReduced) {
                    generateThumbnail(video, startTime, wrapper);
                }
            });

            // Handle already loaded videos
            if (video.readyState >= 1) {
                video.muted = true;
                video.volume = 0;
                if (startTime > 0 && startTime < video.duration) {
                    video.currentTime = startTime;
                }
                if (!thumbnail && !prefersReduced) {
                    generateThumbnail(video, startTime, wrapper);
                }
            }

            videoItems.push({
                video: video,
                startTime: startTime,
                thumbnail: thumbnail,
                wrapper: wrapper
            });

            // Add hover handlers for desktop
            if (!prefersReduced) {
                item.addEventListener('mouseenter', function() {
                    playVideo(item, video, startTime);
                });
                item.addEventListener('mouseleave', function() {
                    pauseVideo(item, video, startTime);
                });
                item.addEventListener('touchstart', function() {
                    if (item.classList.contains('is-playing')) {
                        pauseVideo(item, video, startTime);
                    } else {
                        playVideo(item, video, startTime);
                    }
                }, { passive: true });
            }
        });

        // Set up intersection observer for mobile autoplay
        if (!prefersReduced && 'IntersectionObserver' in window) {
            var observerOptions = {
                root: null,
                rootMargin: '0px',
                threshold: [0, 0.5, 1]
            };

            var videoObserver = new IntersectionObserver(function(entries) {
                entries.forEach(function(entry) {
                    var item = entry.target;
                    var video = item.querySelector('.masonry-gallery__video');
                    if (!video) return;

                    var config = videoItems.find(function(v) {
                        return v.video === video;
                    });
                    var startTime = config ? config.startTime : 0;

                    if (entry.isIntersecting && entry.intersectionRatio >= 0.5) {
                        if (isTouchDevice() && !item.classList.contains('is-playing')) {
                            playVideo(item, video, startTime);
                        }
                    } else if (!entry.isIntersecting || entry.intersectionRatio < 0.3) {
                        if (item.classList.contains('is-playing')) {
                            pauseVideo(item, video, startTime);
                        }
                    }
                });
            }, observerOptions);

            videoItems.forEach(function(config) {
                var item = config.wrapper.closest('.masonry-gallery__item');
                if (item) {
                    videoObserver.observe(item);
                }
            });
        }
    };

    /**
     * Generate a thumbnail from the video at the start time
     */
    var generateThumbnail = function(video, startTime, wrapper) {
        var canvas = document.createElement('canvas');
        var ctx = canvas.getContext('2d');
        if (!ctx) return;

        var captureFrame = function() {
            canvas.width = video.videoWidth || 320;
            canvas.height = video.videoHeight || 240;
            try {
                ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
                var dataUrl = canvas.toDataURL('image/jpeg', 0.8);
                var thumbnail = document.createElement('img');
                thumbnail.className = 'masonry-gallery__thumbnail';
                thumbnail.src = dataUrl;
                thumbnail.alt = '';
                thumbnail.setAttribute('aria-hidden', 'true');
                wrapper.insertBefore(thumbnail, video);
            } catch (e) {
                // Silently fail if CORS blocks the canvas
            }
        };

        if (video.readyState >= 2) {
            video.currentTime = startTime;
            video.addEventListener('seeked', captureFrame, { once: true });
        } else {
            video.addEventListener('loadeddata', function() {
                video.currentTime = startTime;
                video.addEventListener('seeked', captureFrame, { once: true });
            }, { once: true });
        }
    };

    /**
     * Play video from start time
     */
    var playVideo = function(item, video, startTime) {
        item.classList.add('is-playing');
        video.muted = true;
        video.volume = 0;
        if (video.currentTime < startTime || video.ended) {
            video.currentTime = startTime;
        }
        video.play().catch(function() {
            item.classList.remove('is-playing');
        });
    };

    /**
     * Pause video and reset to start time
     */
    var pauseVideo = function(item, video, startTime) {
        item.classList.remove('is-playing');
        video.pause();
        video.currentTime = startTime;
    };

    /**
     * Check if device is touch-enabled
     */
    var isTouchDevice = function() {
        return 'ontouchstart' in window || 
            navigator.maxTouchPoints > 0 || 
            navigator.msMaxTouchPoints > 0;
    };

    /**
     * Initialize all galleries on the page
     */
    var initAllGalleries = function() {
        var galleries = document.querySelectorAll('[data-component="masonry-gallery"]');
        galleries.forEach(initMasonryGallery);
    };

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllGalleries);
    } else {
        initAllGalleries();
    }

    // Watch for dynamically added galleries
    if ('MutationObserver' in window) {
        var mutationObserver = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                mutation.addedNodes.forEach(function(node) {
                    if (node instanceof HTMLElement) {
                        if (node.matches('[data-component="masonry-gallery"]')) {
                            initMasonryGallery(node);
                        }
                        node.querySelectorAll('[data-component="masonry-gallery"]').forEach(initMasonryGallery);
                    }
                });
            });
        });
        mutationObserver.observe(document.body, {
            childList: true,
            subtree: true
        });
    }

    // Expose for external use
    window.MasonryGalleryController = {
        init: initAllGalleries,
        initGallery: initMasonryGallery
    };

})();
