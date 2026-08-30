(() => {
  // src/static-components/LeadCarousel/lead-carousel.ts
  var DEFAULT_INTERVAL_MS = 7e3;
  var LeadCarousel = class {
    root;
    slides;
    dotsHost;
    playPauseBtn;
    intervalMs;
    index = 0;
    playing;
    timer = null;
    reducedMotion;
    constructor(root) {
      this.root = root;
      this.slides = Array.from(root.querySelectorAll("[data-lead-carousel-slide]"));
      this.dotsHost = root.querySelector("[data-lead-carousel-dots]");
      this.playPauseBtn = root.querySelector("[data-lead-carousel-playpause]");
      this.reducedMotion = typeof window !== "undefined" && Boolean(window.matchMedia?.("(prefers-reduced-motion: reduce)").matches);
      const authoredInterval = Number(root.dataset.interval);
      this.intervalMs = Number.isFinite(authoredInterval) && authoredInterval >= 2e3 ? authoredInterval : DEFAULT_INTERVAL_MS;
      const autoplay = root.dataset.autoplay !== "false";
      this.playing = autoplay && !this.reducedMotion && this.slides.length > 1;
      this.init();
    }
    init() {
      if (this.slides.length === 0) return;
      this.root.style.setProperty("--lead-carousel-interval", `${this.intervalMs}ms`);
      this.buildDots();
      this.goTo(this.activeIndex(), { restart: false });
      this.bind();
      this.syncPlayingUi();
      if (this.playing) {
        this.startTimer();
      }
    }
    activeIndex() {
      const current = this.slides.findIndex((slide) => slide.classList.contains("is-active"));
      return current >= 0 ? current : 0;
    }
    buildDots() {
      if (!this.dotsHost) return;
      this.dotsHost.replaceChildren();
      this.slides.forEach((slide, i) => {
        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "lead-carousel__dot";
        btn.setAttribute("role", "tab");
        btn.setAttribute("aria-label", `Go to slide ${i + 1}`);
        const fill = document.createElement("span");
        fill.className = "lead-carousel__dot-fill";
        fill.setAttribute("aria-hidden", "true");
        btn.appendChild(fill);
        btn.addEventListener("click", () => {
          this.goTo(i, { user: true });
        });
        this.dotsHost.appendChild(btn);
      });
    }
    bind() {
      this.playPauseBtn?.addEventListener("click", () => {
        this.setPlaying(!this.playing);
      });
      this.root.addEventListener("keydown", (event) => {
        if (event.key === "ArrowRight") {
          event.preventDefault();
          this.goTo(this.index + 1, { user: true });
        } else if (event.key === "ArrowLeft") {
          event.preventDefault();
          this.goTo(this.index - 1, { user: true });
        }
      });
      document.addEventListener("visibilitychange", () => {
        if (document.hidden) {
          this.clearTimer();
        } else if (this.playing) {
          this.startTimer();
          this.restartDotFill();
        }
      });
    }
    setPlaying(next) {
      this.playing = next && this.slides.length > 1 && !this.reducedMotion;
      this.syncPlayingUi();
      if (this.playing) {
        this.startTimer();
        this.restartDotFill();
      } else {
        this.clearTimer();
      }
    }
    syncPlayingUi() {
      this.root.dataset.playing = this.playing ? "true" : "false";
      if (!this.playPauseBtn) return;
      this.playPauseBtn.setAttribute("aria-pressed", this.playing ? "true" : "false");
      this.playPauseBtn.setAttribute("aria-label", this.playing ? "Pause slideshow" : "Play slideshow");
    }
    goTo(nextIndex, opts = {}) {
      const count = this.slides.length;
      if (!count) return;
      const index = (nextIndex % count + count) % count;
      this.index = index;
      this.slides.forEach((slide, i) => {
        const active = i === index;
        slide.classList.toggle("is-active", active);
        slide.setAttribute("aria-hidden", active ? "false" : "true");
      });
      const dots = this.dotsHost?.querySelectorAll(".lead-carousel__dot");
      dots?.forEach((dot, i) => {
        const active = i === index;
        dot.classList.toggle("is-active", active);
        if (active) {
          dot.setAttribute("aria-current", "true");
        } else {
          dot.removeAttribute("aria-current");
        }
      });
      if (opts.user) {
        this.restartDotFill();
        if (this.playing) this.startTimer();
      } else if (opts.restart !== false) {
        this.restartDotFill();
      }
    }
    restartDotFill() {
      const active = this.dotsHost?.querySelector(".lead-carousel__dot.is-active .lead-carousel__dot-fill");
      if (!active) return;
      active.style.animation = "none";
      void active.offsetWidth;
      active.style.animation = "";
    }
    startTimer() {
      this.clearTimer();
      if (!this.playing) return;
      this.timer = window.setTimeout(() => {
        this.goTo(this.index + 1);
        this.startTimer();
      }, this.intervalMs);
    }
    clearTimer() {
      if (this.timer !== null) {
        window.clearTimeout(this.timer);
        this.timer = null;
      }
    }
  };
  function initLeadCarousels() {
    document.querySelectorAll('[data-component="lead-carousel"]').forEach((root) => {
      new LeadCarousel(root);
    });
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initLeadCarousels);
  } else {
    initLeadCarousels();
  }
})();
