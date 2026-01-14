(() => {
  // src/static-components/CompareSubscription/compare-subscription.ts
  var CompareSubscription = class {
    container;
    plansContainer;
    plansBody;
    ctaPlansContainer;
    prevBtn;
    nextBtn;
    planCount;
    currentPlanIndex;
    isScrolling;
    scrollTimeout;
    constructor(config) {
      this.container = config.container;
      this.plansContainer = this.container.querySelector(".compare-subscription__plans-container");
      this.plansBody = this.container.querySelector(".compare-subscription__plans-body");
      this.ctaPlansContainer = this.container.querySelector(".compare-subscription__cta-plans-container");
      this.prevBtn = this.container.querySelector(".compare-subscription__nav-btn--prev");
      this.nextBtn = this.container.querySelector(".compare-subscription__nav-btn--next");
      this.planCount = this.container.querySelectorAll(".compare-subscription__plan-col").length;
      this.currentPlanIndex = 0;
      this.isScrolling = false;
      this.scrollTimeout = null;
      this.init();
    }
    init() {
      this.setupSyncScroll();
      this.setupNavButtons();
      this.updateNavButtons();
    }
    /**
     * Setup synchronized scrolling between all horizontal scroll containers
     */
    setupSyncScroll() {
      const scrollContainers = [
        this.plansContainer,
        this.plansBody,
        this.ctaPlansContainer
      ].filter(Boolean);
      scrollContainers.forEach((container) => {
        container.addEventListener("scroll", () => {
          if (this.isScrolling) return;
          this.isScrolling = true;
          const scrollLeft = container.scrollLeft;
          scrollContainers.forEach((otherContainer) => {
            if (otherContainer !== container) {
              otherContainer.scrollLeft = scrollLeft;
            }
          });
          this.updateCurrentIndexFromScroll(scrollLeft, container);
          if (this.scrollTimeout) {
            clearTimeout(this.scrollTimeout);
          }
          this.scrollTimeout = setTimeout(() => {
            this.isScrolling = false;
          }, 50);
        });
      });
    }
    /**
     * Setup click handlers for navigation buttons
     */
    setupNavButtons() {
      if (this.prevBtn) {
        this.prevBtn.addEventListener("click", () => {
          this.scrollToPlan(this.currentPlanIndex - 1);
        });
      }
      if (this.nextBtn) {
        this.nextBtn.addEventListener("click", () => {
          this.scrollToPlan(this.currentPlanIndex + 1);
        });
      }
    }
    /**
     * Scroll to a specific plan by index
     */
    scrollToPlan(index) {
      if (index < 0 || index >= this.planCount) return;
      this.currentPlanIndex = index;
      const scrollContainers = [
        this.plansContainer,
        this.plansBody,
        this.ctaPlansContainer
      ].filter(Boolean);
      if (scrollContainers.length === 0) return;
      const planCols = this.container.querySelectorAll(".compare-subscription__plan-col");
      if (planCols.length === 0) return;
      const planCol = planCols[0];
      const planWidth = planCol.offsetWidth;
      const scrollPosition = index * planWidth;
      this.isScrolling = true;
      scrollContainers.forEach((container) => {
        container.scrollTo({
          left: scrollPosition,
          behavior: "smooth"
        });
      });
      this.updateNavButtons();
      setTimeout(() => {
        this.isScrolling = false;
      }, 300);
    }
    /**
     * Update current plan index based on scroll position
     */
    updateCurrentIndexFromScroll(scrollLeft, container) {
      const planCols = this.container.querySelectorAll(".compare-subscription__plan-col");
      if (planCols.length === 0) return;
      const planCol = planCols[0];
      const planWidth = planCol.offsetWidth;
      const newIndex = Math.round(scrollLeft / planWidth);
      if (newIndex !== this.currentPlanIndex && newIndex >= 0 && newIndex < this.planCount) {
        this.currentPlanIndex = newIndex;
        this.updateNavButtons();
      }
    }
    /**
     * Update the disabled state of navigation buttons
     */
    updateNavButtons() {
      if (this.prevBtn) {
        this.prevBtn.disabled = this.currentPlanIndex === 0;
      }
      if (this.nextBtn) {
        this.nextBtn.disabled = this.currentPlanIndex >= this.planCount - 1;
      }
    }
    /**
     * Public method to scroll to a specific plan
     */
    goToPlan(index) {
      this.scrollToPlan(index);
    }
    /**
     * Public method to go to previous plan
     */
    prevPlan() {
      this.scrollToPlan(this.currentPlanIndex - 1);
    }
    /**
     * Public method to go to next plan
     */
    nextPlan() {
      this.scrollToPlan(this.currentPlanIndex + 1);
    }
    /**
     * Public method to get current plan index
     */
    getCurrentPlanIndex() {
      return this.currentPlanIndex;
    }
    /**
     * Public method to get total plan count
     */
    getPlanCount() {
      return this.planCount;
    }
  };
  function initCompareSubscription() {
    const containers = document.querySelectorAll('[data-component="compare-subscription"]');
    containers.forEach((container) => {
      new CompareSubscription({
        container
      });
    });
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initCompareSubscription);
  } else {
    initCompareSubscription();
  }
})();
