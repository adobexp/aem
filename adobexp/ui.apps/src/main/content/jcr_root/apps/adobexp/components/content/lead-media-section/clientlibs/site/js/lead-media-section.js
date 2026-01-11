/**
 * LeadMediaSection Component
 * Handles video autoplay based on viewport visibility
 * 
 * Videos play automatically when visible in viewport and pause when scrolled out.
 * All video controls are hidden at all times.
 * 
 * Supports data-start-time attribute on video elements to set
 * the starting point (in seconds) when video becomes visible.
 * Example: <video data-start-time="2.5" ...> starts at 2.5 seconds
 */

(function() {
  'use strict';

  var LeadMediaSectionController = /** @class */ (function() {
    function LeadMediaSectionController() {
      this.observer = null;
      this.videos = [];
      this.videoStartTimes = new Map();
      this.init();
    }

    LeadMediaSectionController.prototype.init = function() {
      var _this = this;
      
      // Find all videos within lead-media-section components
      this.videos = Array.from(
        document.querySelectorAll('.lead-media-section__media-frame video')
      );

      if (this.videos.length === 0) return;

      // Configure all videos: muted, no controls, plays inline, loops
      this.videos.forEach(function(video) {
        // Ensure video is muted (required for autoplay in most browsers)
        video.muted = true;
        
        // Hide all controls
        video.controls = false;
        video.removeAttribute('controls');
        
        // Enable inline playback on mobile
        video.playsInline = true;
        video.setAttribute('playsinline', '');
        video.setAttribute('webkit-playsinline', '');
        
        // Enable looping
        video.loop = true;

        // Disable picture-in-picture
        video.disablePictureInPicture = true;

        // Prevent context menu (right-click)
        video.addEventListener('contextmenu', function(e) {
          e.preventDefault();
        });

        // Get start time from data attribute (in seconds)
        var startTimeAttr = video.getAttribute('data-start-time');
        if (startTimeAttr) {
          var startTime = parseFloat(startTimeAttr);
          if (!isNaN(startTime) && startTime >= 0) {
            _this.videoStartTimes.set(video, startTime);
          }
        }
      });

      // Create intersection observer for viewport detection
      this.createObserver();
    };

    LeadMediaSectionController.prototype.createObserver = function() {
      var _this = this;
      
      var options = {
        root: null, // viewport
        rootMargin: '0px',
        threshold: 0.3 // 30% visibility triggers play/pause
      };

      this.observer = new IntersectionObserver(function(entries) {
        entries.forEach(function(entry) {
          var video = entry.target;

          if (entry.isIntersecting) {
            // Video is visible - play it
            _this.playVideo(video);
          } else {
            // Video is not visible - pause it
            _this.pauseVideo(video);
          }
        });
      }, options);

      // Observe all videos
      this.videos.forEach(function(video) {
        _this.observer.observe(video);
      });
    };

    LeadMediaSectionController.prototype.playVideo = function(video) {
      // Set start time if configured and video is at the beginning
      var startTime = this.videoStartTimes.get(video);
      if (startTime !== undefined && video.currentTime < startTime) {
        video.currentTime = startTime;
      }

      // Use play() with promise handling to avoid console errors
      var playPromise = video.play();

      if (playPromise !== undefined) {
        playPromise.catch(function(error) {
          // Auto-play was prevented, this is expected in some browsers
          console.debug('LeadMediaSection: Video autoplay prevented', error);
        });
      }
    };

    LeadMediaSectionController.prototype.pauseVideo = function(video) {
      if (!video.paused) {
        video.pause();
      }
    };

    LeadMediaSectionController.prototype.destroy = function() {
      if (this.observer) {
        this.observer.disconnect();
        this.observer = null;
      }
      this.videos = [];
      this.videoStartTimes.clear();
    };

    return LeadMediaSectionController;
  }());

  // Initialize when DOM is ready
  function initLeadMediaSection() {
    new LeadMediaSectionController();
  }

  // Support both DOMContentLoaded and immediate execution if DOM is already ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initLeadMediaSection);
  } else {
    initLeadMediaSection();
  }

  // Export for potential external use
  window.LeadMediaSectionController = LeadMediaSectionController;

})();
