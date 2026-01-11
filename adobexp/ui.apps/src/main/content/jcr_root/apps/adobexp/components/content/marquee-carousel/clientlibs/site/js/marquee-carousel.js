/**
 * MarqueeCarousel Component
 * Initializes the infinite scrolling marquee carousel
 * 
 * Speed can be controlled via:
 * 1. data-marquee-speed attribute on .marquee-carousel__track-container (in seconds)
 * 2. --marquee-carousel-duration CSS variable in theme files
 * 3. data-marquee-speed attribute on the component container (fallback)
 */

(function() {
  'use strict';

  var MarqueeCarousel = /** @class */ (function() {
    function MarqueeCarousel(container, options) {
      if (options === void 0) { options = {}; }
      this.container = container;
      this.trackContainer = container.querySelector('.marquee-carousel__track-container');
      this.track = container.querySelector('.marquee-carousel__track');
      
      // Priority: trackContainer data attr > component data attr > options > default (use CSS variable)
      var trackContainerSpeed = this.trackContainer ? this.trackContainer.dataset.marqueeSpeed : null;
      var componentSpeed = container.dataset.marqueeSpeed;
      
      var speed;
      if (trackContainerSpeed) {
        speed = parseInt(trackContainerSpeed, 10);
      } else if (componentSpeed) {
        speed = parseInt(componentSpeed, 10);
      } else if (options.speed !== undefined) {
        speed = options.speed;
      }
      // If no speed specified, leave undefined to use CSS variable from theme
      
      this.options = {
        speed: speed || 0, // 0 means use CSS variable
        pauseOnHover: options.pauseOnHover !== false
      };

      this.init();
    }

    MarqueeCarousel.prototype.init = function() {
      if (!this.track) {
        console.warn('MarqueeCarousel: Track element not found');
        return;
      }

      // Duplicate the track content for seamless looping
      this.duplicateContent();

      // Set animation duration only if explicitly specified (not using CSS variable)
      if (this.options.speed > 0) {
        this.setAnimationDuration();
      }

      // Setup hover pause if enabled
      if (this.options.pauseOnHover) {
        this.setupHoverPause();
      }
    };

    MarqueeCarousel.prototype.duplicateContent = function() {
      if (!this.track) return;

      // Clone all children and append them for seamless loop
      var children = Array.from(this.track.children);
      var _this = this;
      children.forEach(function(child) {
        var clone = child.cloneNode(true);
        _this.track.appendChild(clone);
      });
    };

    MarqueeCarousel.prototype.setAnimationDuration = function() {
      if (!this.track || this.options.speed <= 0) return;

      // Set CSS custom property for animation duration on the track
      this.track.style.setProperty('--marquee-carousel-duration', this.options.speed + 's');
    };

    MarqueeCarousel.prototype.setupHoverPause = function() {
      if (!this.track) return;

      var track = this.track;
      track.addEventListener('mouseenter', function() {
        track.style.animationPlayState = 'paused';
      });

      track.addEventListener('mouseleave', function() {
        track.style.animationPlayState = 'running';
      });
    };

    // Public method to update speed dynamically
    MarqueeCarousel.prototype.setSpeed = function(speed) {
      this.options.speed = speed;
      this.setAnimationDuration();
    };

    return MarqueeCarousel;
  }());

  // Auto-initialize all marquee carousels on page load
  function initMarqueeCarousels() {
    var carousels = document.querySelectorAll('[data-component="marquee-carousel"]');
    
    carousels.forEach(function(carousel) {
      var pauseOnHover = carousel.dataset.marqueePause !== 'false';
      new MarqueeCarousel(carousel, { pauseOnHover: pauseOnHover });
    });
  }

  // Initialize on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initMarqueeCarousels);
  } else {
    initMarqueeCarousels();
  }

  // Export for potential external use
  window.MarqueeCarousel = MarqueeCarousel;

})();
