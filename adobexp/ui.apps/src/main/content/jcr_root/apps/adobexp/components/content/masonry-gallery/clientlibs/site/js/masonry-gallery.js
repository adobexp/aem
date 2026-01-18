(() => {
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };

  // src/static-components/MasonryGallery/masonry-gallery.ts
  var require_masonry_gallery = __commonJS({
    "src/static-components/MasonryGallery/masonry-gallery.ts"() {
      (() => {
        const INITIALIZED_ATTR = "data-masonry-gallery-initialized";
        const ICONS = {
          close: `<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6L6 18"/><path d="M6 6l12 12"/></svg>`,
          chevronLeft: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 18l-6-6 6-6"/></svg>`,
          chevronRight: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 18l6-6-6-6"/></svg>`,
          play: `<svg viewBox="0 0 24 24" width="18" height="18" fill="none"><path d="M9 7.5v9l8-4.5-8-4.5Z" fill="currentColor"/></svg>`,
          pause: `<svg viewBox="0 0 24 24" width="18" height="18" fill="none"><path d="M7 6h3v12H7V6Z" fill="currentColor"/><path d="M14 6h3v12h-3V6Z" fill="currentColor"/></svg>`,
          speakerOn: `<svg viewBox="0 0 24 24" width="18" height="18" fill="none"><path d="M3 10v4c0 1.1.9 2 2 2h3l5 4V4L8 8H5c-1.1 0-2 .9-2 2Z" fill="currentColor"/><path d="M16.5 7.5c1.5 1.5 1.5 7.5 0 9" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M19.5 4.5c3 3 3 12 0 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>`,
          speakerOff: `<svg viewBox="0 0 24 24" width="18" height="18" fill="none"><path d="M3 10v4c0 1.1.9 2 2 2h3l5 4V4L8 8H5c-1.1 0-2 .9-2 2Z" fill="currentColor"/><path d="M18 9l-6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M12 9l6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>`
        };
        let lightboxInstance = null;
        const initMasonryGallery = (root) => {
          if (root.hasAttribute(INITIALIZED_ATTR)) return;
          root.setAttribute(INITIALIZED_ATTR, "true");
          const prefersReduced = typeof window !== "undefined" && window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
          const videoItems = [];
          const galleryItems = [];
          const items = root.querySelectorAll(".masonry-gallery__item");
          items.forEach((item, index) => {
            const video = item.querySelector(".masonry-gallery__video");
            const image = item.querySelector(".masonry-gallery__image");
            const titleEl = item.querySelector(".masonry-gallery__title");
            const title = titleEl?.textContent?.trim() || "";
            const startTimeAttr = item.getAttribute("data-video-start") || "0";
            const startTime = parseFloat(startTimeAttr) || 0;
            if (video) {
              const source = video.querySelector("source");
              const videoSrc = source?.src || video.src || "";
              galleryItems.push({
                element: item,
                type: "video",
                src: videoSrc,
                title,
                videoStartTime: startTime
              });
              const wrapper = item.querySelector(".masonry-gallery__video-wrapper");
              if (!wrapper) return;
              let thumbnail = item.querySelector(".masonry-gallery__thumbnail");
              video.controls = false;
              video.muted = true;
              video.defaultMuted = true;
              video.volume = 0;
              video.playsInline = true;
              video.loop = true;
              video.preload = "metadata";
              video.setAttribute("controlsList", "nodownload noplaybackrate noremoteplayback nofullscreen");
              video.setAttribute("disablePictureInPicture", "true");
              video.setAttribute("disableRemotePlayback", "true");
              video.removeAttribute("controls");
              video.addEventListener("volumechange", () => {
                if (!video.muted || video.volume > 0) {
                  video.muted = true;
                  video.volume = 0;
                }
              });
              video.addEventListener("contextmenu", (e) => e.preventDefault());
              video.addEventListener("click", (e) => {
                e.preventDefault();
              });
              video.addEventListener("loadedmetadata", () => {
                video.muted = true;
                video.volume = 0;
                if (startTime > 0 && startTime < video.duration) {
                  video.currentTime = startTime;
                }
                if (!thumbnail && !prefersReduced) {
                  generateThumbnail(video, startTime, wrapper);
                }
              });
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
              const autoplay = item.hasAttribute("data-video-autoplay");
              videoItems.push({
                video,
                startTime,
                thumbnail,
                wrapper,
                item,
                autoplay
              });
              if (!prefersReduced) {
                item.addEventListener("mouseenter", () => {
                  playVideo(item, video, startTime);
                });
                item.addEventListener("mouseleave", () => {
                  pauseVideo(item, video, startTime);
                });
              }
            } else if (image) {
              galleryItems.push({
                element: item,
                type: "image",
                src: image.src,
                title
              });
            }
            item.addEventListener("click", (e) => {
              e.preventDefault();
              e.stopPropagation();
              const itemIndex = galleryItems.findIndex((gi) => gi.element === item);
              if (itemIndex !== -1) {
                openLightbox(galleryItems, itemIndex);
              }
            });
            item.setAttribute("data-gallery-index", String(galleryItems.length - 1));
          });
          if (!prefersReduced && "IntersectionObserver" in window) {
            const observerOptions = {
              root: null,
              rootMargin: "0px",
              threshold: [0, 0.5, 1]
            };
            const videoObserver = new IntersectionObserver((entries) => {
              entries.forEach((entry) => {
                const item = entry.target;
                const video = item.querySelector(".masonry-gallery__video");
                if (!video) return;
                const config = videoItems.find((v) => v.video === video);
                const startTime = config?.startTime || 0;
                const hasAutoplay = config?.autoplay || false;
                if (entry.isIntersecting && entry.intersectionRatio >= 0.5) {
                  if ((isTouchDevice() || hasAutoplay) && !item.classList.contains("is-playing")) {
                    playVideo(item, video, startTime);
                  }
                } else if (!entry.isIntersecting || entry.intersectionRatio < 0.3) {
                  if (item.classList.contains("is-playing")) {
                    pauseVideo(item, video, startTime);
                  }
                }
              });
            }, observerOptions);
            videoItems.forEach(({ wrapper }) => {
              const item = wrapper.closest(".masonry-gallery__item");
              if (item) {
                videoObserver.observe(item);
              }
            });
          }
        };
        const generateThumbnail = (video, startTime, wrapper) => {
          const canvas = document.createElement("canvas");
          const ctx = canvas.getContext("2d");
          if (!ctx) return;
          const captureFrame = () => {
            canvas.width = video.videoWidth || 320;
            canvas.height = video.videoHeight || 240;
            try {
              ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
              const dataUrl = canvas.toDataURL("image/jpeg", 0.8);
              const thumbnail = document.createElement("img");
              thumbnail.className = "masonry-gallery__thumbnail";
              thumbnail.src = dataUrl;
              thumbnail.alt = "";
              thumbnail.setAttribute("aria-hidden", "true");
              wrapper.insertBefore(thumbnail, video);
            } catch {
            }
          };
          if (video.readyState >= 2) {
            video.currentTime = startTime;
            video.addEventListener("seeked", captureFrame, { once: true });
          } else {
            video.addEventListener("loadeddata", () => {
              video.currentTime = startTime;
              video.addEventListener("seeked", captureFrame, { once: true });
            }, { once: true });
          }
        };
        const playVideo = (item, video, startTime) => {
          item.classList.add("is-playing");
          video.muted = true;
          video.volume = 0;
          if (video.currentTime < startTime || video.ended) {
            video.currentTime = startTime;
          }
          video.play().catch(() => {
            item.classList.remove("is-playing");
          });
        };
        const pauseVideo = (item, video, startTime) => {
          item.classList.remove("is-playing");
          video.pause();
          video.currentTime = startTime;
        };
        const isTouchDevice = () => {
          return "ontouchstart" in window || navigator.maxTouchPoints > 0 || // @ts-expect-error - msMaxTouchPoints is IE-specific
          navigator.msMaxTouchPoints > 0;
        };
        const createLightbox = () => {
          const overlay = document.createElement("div");
          overlay.className = "masonry-gallery-lightbox";
          overlay.setAttribute("role", "dialog");
          overlay.setAttribute("aria-modal", "true");
          overlay.setAttribute("aria-label", "Gallery lightbox");
          const container = document.createElement("div");
          container.className = "masonry-gallery-lightbox__container";
          const closeBtn = document.createElement("button");
          closeBtn.className = "masonry-gallery-lightbox__close btn-theme-dark btn-icon-only";
          closeBtn.setAttribute("aria-label", "Close lightbox");
          closeBtn.innerHTML = ICONS.close;
          const prevBtn = document.createElement("button");
          prevBtn.className = "masonry-gallery-lightbox__nav masonry-gallery-lightbox__nav--prev btn-theme-dark btn-icon-only";
          prevBtn.setAttribute("aria-label", "Previous item");
          prevBtn.innerHTML = ICONS.chevronLeft;
          const nextBtn = document.createElement("button");
          nextBtn.className = "masonry-gallery-lightbox__nav masonry-gallery-lightbox__nav--next btn-theme-dark btn-icon-only";
          nextBtn.setAttribute("aria-label", "Next item");
          nextBtn.innerHTML = ICONS.chevronRight;
          const mediaContainer = document.createElement("div");
          mediaContainer.className = "masonry-gallery-lightbox__media";
          const titleEl = document.createElement("div");
          titleEl.className = "masonry-gallery-lightbox__title";
          const videoControls = document.createElement("div");
          videoControls.className = "masonry-gallery-lightbox__video-controls";
          const playToggleBtn = document.createElement("button");
          playToggleBtn.className = "masonry-gallery-lightbox__control-btn btn-theme-dark btn-icon-only";
          playToggleBtn.setAttribute("aria-label", "Pause video");
          playToggleBtn.innerHTML = ICONS.pause;
          const muteToggleBtn = document.createElement("button");
          muteToggleBtn.className = "masonry-gallery-lightbox__control-btn btn-theme-dark btn-icon-only";
          muteToggleBtn.setAttribute("aria-label", "Unmute video");
          muteToggleBtn.innerHTML = ICONS.speakerOff;
          videoControls.appendChild(playToggleBtn);
          videoControls.appendChild(muteToggleBtn);
          container.appendChild(closeBtn);
          container.appendChild(prevBtn);
          container.appendChild(nextBtn);
          container.appendChild(mediaContainer);
          container.appendChild(titleEl);
          container.appendChild(videoControls);
          overlay.appendChild(container);
          document.body.appendChild(overlay);
          const keyHandler = (e) => {
            if (!lightboxInstance) return;
            switch (e.key) {
              case "Escape":
                closeLightbox();
                break;
              case "ArrowLeft":
                navigateLightbox(-1);
                break;
              case "ArrowRight":
                navigateLightbox(1);
                break;
            }
          };
          closeBtn.addEventListener("click", closeLightbox);
          prevBtn.addEventListener("click", () => navigateLightbox(-1));
          nextBtn.addEventListener("click", () => navigateLightbox(1));
          const SWIPE_THRESHOLD = 50;
          const SWIPE_MAX_TIME = 300;
          const SWIPE_VERTICAL_TOLERANCE = 100;
          let swipeState = null;
          const handleTouchStart = (e) => {
            if (e.touches.length === 1) {
              const touch = e.touches[0];
              swipeState = {
                startX: touch.clientX,
                startY: touch.clientY,
                startTime: Date.now()
              };
            }
          };
          const handleTouchMove = (e) => {
            if (swipeState && e.touches.length === 1) {
              const touch = e.touches[0];
              const deltaX = touch.clientX - swipeState.startX;
              const deltaY = Math.abs(touch.clientY - swipeState.startY);
              if (Math.abs(deltaX) > 10 && deltaY < SWIPE_VERTICAL_TOLERANCE) {
                e.preventDefault();
              }
            }
          };
          const handleTouchEnd = (e) => {
            if (!swipeState || e.changedTouches.length !== 1) {
              swipeState = null;
              return;
            }
            const touch = e.changedTouches[0];
            const deltaX = touch.clientX - swipeState.startX;
            const deltaY = Math.abs(touch.clientY - swipeState.startY);
            const deltaTime = Date.now() - swipeState.startTime;
            if (Math.abs(deltaX) >= SWIPE_THRESHOLD && deltaY < SWIPE_VERTICAL_TOLERANCE && deltaTime <= SWIPE_MAX_TIME) {
              if (deltaX > 0) {
                navigateLightbox(-1);
              } else {
                navigateLightbox(1);
              }
            }
            swipeState = null;
          };
          mediaContainer.addEventListener("touchstart", handleTouchStart, { passive: true });
          mediaContainer.addEventListener("touchmove", handleTouchMove, { passive: false });
          mediaContainer.addEventListener("touchend", handleTouchEnd, { passive: true });
          playToggleBtn.addEventListener("click", (e) => {
            e.stopPropagation();
            if (!lightboxInstance?.currentVideo) return;
            const video = lightboxInstance.currentVideo;
            if (video.paused) {
              video.play().catch(() => {
              });
              playToggleBtn.innerHTML = ICONS.pause;
              playToggleBtn.setAttribute("aria-label", "Pause video");
            } else {
              video.pause();
              playToggleBtn.innerHTML = ICONS.play;
              playToggleBtn.setAttribute("aria-label", "Play video");
            }
          });
          muteToggleBtn.addEventListener("click", (e) => {
            e.stopPropagation();
            if (!lightboxInstance?.currentVideo) return;
            const video = lightboxInstance.currentVideo;
            video.muted = !video.muted;
            if (!video.muted && video.volume === 0) {
              video.volume = 1;
            }
            syncMuteButton(muteToggleBtn, video.muted);
          });
          return {
            overlay,
            container,
            mediaContainer,
            titleEl,
            closeBtn,
            prevBtn,
            nextBtn,
            videoControls,
            playToggleBtn,
            muteToggleBtn,
            currentIndex: 0,
            items: [],
            currentVideo: null,
            keyHandler,
            swipeState: null
          };
        };
        const syncMuteButton = (btn, isMuted) => {
          btn.innerHTML = isMuted ? ICONS.speakerOff : ICONS.speakerOn;
          btn.setAttribute("aria-label", isMuted ? "Unmute video" : "Mute video");
        };
        const syncPlayButton = (btn, isPaused) => {
          btn.innerHTML = isPaused ? ICONS.play : ICONS.pause;
          btn.setAttribute("aria-label", isPaused ? "Play video" : "Pause video");
        };
        const openLightbox = (items, index) => {
          if (!lightboxInstance) {
            lightboxInstance = createLightbox();
          }
          if (!lightboxInstance) return;
          lightboxInstance.items = items;
          lightboxInstance.currentIndex = index;
          document.addEventListener("keydown", lightboxInstance.keyHandler);
          lightboxInstance.overlay.classList.add("is-open");
          document.body.style.overflow = "hidden";
          renderLightboxItem();
        };
        const closeLightbox = () => {
          if (!lightboxInstance) return;
          if (lightboxInstance.currentVideo) {
            lightboxInstance.currentVideo.pause();
            lightboxInstance.currentVideo = null;
          }
          document.removeEventListener("keydown", lightboxInstance.keyHandler);
          lightboxInstance.overlay.classList.remove("is-open");
          document.body.style.overflow = "";
          lightboxInstance.mediaContainer.innerHTML = "";
          lightboxInstance.titleEl.textContent = "";
        };
        const navigateLightbox = (direction) => {
          if (!lightboxInstance) return;
          const { items, currentIndex } = lightboxInstance;
          let newIndex = currentIndex + direction;
          if (newIndex < 0) {
            newIndex = items.length - 1;
          } else if (newIndex >= items.length) {
            newIndex = 0;
          }
          lightboxInstance.currentIndex = newIndex;
          renderLightboxItem();
        };
        const renderLightboxItem = () => {
          if (!lightboxInstance) return;
          const { items, currentIndex, mediaContainer, titleEl, videoControls, playToggleBtn, muteToggleBtn } = lightboxInstance;
          const item = items[currentIndex];
          if (!item) return;
          if (lightboxInstance.currentVideo) {
            lightboxInstance.currentVideo.pause();
            lightboxInstance.currentVideo = null;
          }
          mediaContainer.innerHTML = "";
          if (item.type === "image") {
            const img = document.createElement("img");
            img.className = "masonry-gallery-lightbox__image";
            img.src = item.src;
            img.alt = item.title || "Gallery image";
            mediaContainer.appendChild(img);
            videoControls.classList.remove("is-visible");
          } else if (item.type === "video") {
            const video = document.createElement("video");
            video.className = "masonry-gallery-lightbox__video";
            video.src = item.src;
            video.autoplay = true;
            video.muted = true;
            video.loop = true;
            video.playsInline = true;
            video.controls = false;
            video.setAttribute("controlsList", "nodownload noplaybackrate noremoteplayback");
            video.setAttribute("disablePictureInPicture", "true");
            if (item.videoStartTime && item.videoStartTime > 0) {
              video.currentTime = item.videoStartTime;
            }
            mediaContainer.appendChild(video);
            lightboxInstance.currentVideo = video;
            videoControls.classList.add("is-visible");
            syncMuteButton(muteToggleBtn, video.muted);
            video.addEventListener("play", () => {
              syncPlayButton(playToggleBtn, false);
            });
            video.addEventListener("pause", () => {
              syncPlayButton(playToggleBtn, true);
            });
            video.addEventListener("volumechange", () => {
              syncMuteButton(muteToggleBtn, video.muted);
            });
            video.play().catch(() => {
              syncPlayButton(playToggleBtn, true);
            });
          }
          titleEl.textContent = item.title || "";
          lightboxInstance.prevBtn.style.display = items.length > 1 ? "" : "none";
          lightboxInstance.nextBtn.style.display = items.length > 1 ? "" : "none";
        };
        const initAllGalleries = () => {
          const galleries = document.querySelectorAll(
            '[data-component="masonry-gallery"]'
          );
          galleries.forEach(initMasonryGallery);
        };
        initAllGalleries();
        if ("MutationObserver" in window) {
          const mutationObserver = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
              mutation.addedNodes.forEach((node) => {
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
      })();
    }
  });
  require_masonry_gallery();
})();
