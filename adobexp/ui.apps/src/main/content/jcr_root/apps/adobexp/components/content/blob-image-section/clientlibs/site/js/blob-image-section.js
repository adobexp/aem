/**
 * Blob Image Section Component JavaScript
 * Handles video auto-play on intersection and start time functionality
 */
(function() {
    'use strict';

    /**
     * BlobImageSectionController - manages video playback based on viewport visibility
     */
    var BlobImageSectionController = function() {
        this.observer = null;
        this.videos = [];
        this.videoStartTimes = new Map();
        this.init();
    };

    /**
     * Initialize the controller
     */
    BlobImageSectionController.prototype.init = function() {
        var self = this;
        
        // Find all videos within blob-image-section components
        this.videos = Array.from(
            document.querySelectorAll('.blob-image-section__blob-primary video')
        );

        if (this.videos.length === 0) return;

        // Configure each video
        this.videos.forEach(function(video) {
            video.muted = true;
            video.controls = false;
            video.playsInline = true;
            video.loop = true;

            // Parse start time from data attribute
            var startTimeAttr = video.getAttribute('data-start-time');
            if (startTimeAttr) {
                var startTime = parseFloat(startTimeAttr);
                if (!isNaN(startTime) && startTime >= 0) {
                    self.videoStartTimes.set(video, startTime);
                }
            }
        });

        this.createObserver();
    };

    /**
     * Create intersection observer for video visibility
     */
    BlobImageSectionController.prototype.createObserver = function() {
        var self = this;
        
        var options = {
            root: null, // viewport
            rootMargin: '0px',
            threshold: 0.3 // 30% visibility triggers play/pause
        };

        this.observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                var video = entry.target;
                if (entry.isIntersecting) {
                    self.playVideo(video);
                } else {
                    self.pauseVideo(video);
                }
            });
        }, options);

        // Observe all videos
        this.videos.forEach(function(video) {
            self.observer.observe(video);
        });
    };

    /**
     * Play video with optional start time
     */
    BlobImageSectionController.prototype.playVideo = function(video) {
        var startTime = this.videoStartTimes.get(video);
        
        // Set start time if configured and video hasn't progressed past it
        if (startTime !== undefined && video.currentTime < startTime) {
            video.currentTime = startTime;
        }

        var playPromise = video.play();
        if (playPromise !== undefined) {
            playPromise.catch(function(error) {
                console.debug('BlobImageSection: Video autoplay prevented', error);
            });
        }
    };

    /**
     * Pause video
     */
    BlobImageSectionController.prototype.pauseVideo = function(video) {
        if (!video.paused) {
            video.pause();
        }
    };

    /**
     * Cleanup observer and resources
     */
    BlobImageSectionController.prototype.destroy = function() {
        if (this.observer) {
            this.observer.disconnect();
            this.observer = null;
        }
        this.videos = [];
        this.videoStartTimes.clear();
    };

    /**
     * Initialize blob image section when DOM is ready
     */
    function initBlobImageSection() {
        new BlobImageSectionController();
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initBlobImageSection);
    } else {
        initBlobImageSection();
    }

    // Expose controller for potential external use
    window.BlobImageSectionController = BlobImageSectionController;

})();

