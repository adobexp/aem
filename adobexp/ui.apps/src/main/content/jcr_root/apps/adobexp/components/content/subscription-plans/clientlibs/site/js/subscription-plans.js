(() => {
  // src/static-components/SubscriptionPlans/subscription-plans.ts
  var SubscriptionPlans = class {
    container;
    toggleBtns;
    cards;
    grid;
    navBtns;
    saveBadges;
    currentBilling;
    currentCardIndex = 0;
    constructor(config) {
      this.container = config.container;
      this.currentBilling = config.defaultBilling || "monthly";
      this.toggleBtns = this.container.querySelectorAll(".subscription-plans__toggle-btn");
      this.cards = this.container.querySelectorAll(".subscription-plans__card");
      this.grid = this.container.querySelector(".subscription-plans__grid");
      this.navBtns = this.container.querySelectorAll(".subscription-plans__nav-btn");
      this.saveBadges = this.container.querySelectorAll(".subscription-plans__toggle-badge");
      this.init();
    }
    init() {
      this.setupToggle();
      this.setupNavigation();
      this.checkDiscountExpiration();
      this.updatePricing();
      this.updateSaveBadgeVisibility();
    }
    /**
     * Setup billing toggle functionality
     */
    setupToggle() {
      this.toggleBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
          const billing = btn.dataset.billing;
          if (billing && billing !== this.currentBilling) {
            this.currentBilling = billing;
            this.updateToggleState();
            this.updatePricing();
            this.updateSaveBadgeVisibility();
          }
        });
      });
    }
    /**
     * Update save badge visibility based on billing selection
     * Badge is only visible when yearly billing is selected
     */
    updateSaveBadgeVisibility() {
      const isYearly = this.currentBilling === "yearly";
      this.saveBadges.forEach((badge) => {
        badge.classList.toggle("visible", isYearly);
      });
    }
    /**
     * Setup mobile navigation arrows
     */
    setupNavigation() {
      this.navBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
          const direction = btn.dataset.nav;
          if (direction === "prev") {
            this.navigateToPlan(this.currentCardIndex - 1);
          } else if (direction === "next") {
            this.navigateToPlan(this.currentCardIndex + 1);
          }
        });
      });
      if (this.grid) {
        this.grid.addEventListener("scroll", () => {
          this.updateCurrentIndexFromScroll();
        });
      }
      this.updateNavButtonStates();
    }
    /**
     * Navigate to a specific plan card
     */
    navigateToPlan(index) {
      if (index < 0 || index >= this.cards.length || !this.grid) return;
      const targetCard = this.cards[index];
      if (targetCard) {
        const gridRect = this.grid.getBoundingClientRect();
        const cardRect = targetCard.getBoundingClientRect();
        const scrollLeft = targetCard.offsetLeft - gridRect.width / 2 + cardRect.width / 2;
        this.grid.scrollTo({
          left: scrollLeft,
          behavior: "smooth"
        });
        this.currentCardIndex = index;
        this.updateNavButtonStates();
      }
    }
    /**
     * Update current card index based on scroll position
     */
    updateCurrentIndexFromScroll() {
      if (!this.grid) return;
      const gridRect = this.grid.getBoundingClientRect();
      const gridCenter = gridRect.left + gridRect.width / 2;
      let closestIndex = 0;
      let closestDistance = Infinity;
      this.cards.forEach((card, index) => {
        const cardRect = card.getBoundingClientRect();
        const cardCenter = cardRect.left + cardRect.width / 2;
        const distance = Math.abs(gridCenter - cardCenter);
        if (distance < closestDistance) {
          closestDistance = distance;
          closestIndex = index;
        }
      });
      if (closestIndex !== this.currentCardIndex) {
        this.currentCardIndex = closestIndex;
        this.updateNavButtonStates();
      }
    }
    /**
     * Update navigation button disabled states
     */
    updateNavButtonStates() {
      this.navBtns.forEach((btn) => {
        const direction = btn.dataset.nav;
        if (direction === "prev") {
          btn.disabled = this.currentCardIndex === 0;
        } else if (direction === "next") {
          btn.disabled = this.currentCardIndex === this.cards.length - 1;
        }
      });
    }
    /**
     * Update toggle button active states
     */
    updateToggleState() {
      this.toggleBtns.forEach((btn) => {
        const isActive = btn.dataset.billing === this.currentBilling;
        btn.classList.toggle("active", isActive);
      });
    }
    /**
     * Update pricing display based on current billing selection
     */
    updatePricing() {
      this.cards.forEach((card) => {
        const pricingContainer = card.querySelector(".subscription-plans__pricing");
        if (pricingContainer) {
          pricingContainer.setAttribute("data-billing", this.currentBilling);
        }
        const monthlyNote = card.querySelector('.subscription-plans__billing-note[data-billing="monthly"]');
        const yearlyNote = card.querySelector('.subscription-plans__billing-note[data-billing="yearly"]');
        if (monthlyNote) {
          monthlyNote.style.display = this.currentBilling === "monthly" ? "block" : "none";
        }
        if (yearlyNote) {
          yearlyNote.style.display = this.currentBilling === "yearly" ? "block" : "none";
        }
      });
    }
    /**
     * Check if discount has expired based on data-discount-until attribute
     * Format: YYYY-MM-DD or ISO date string
     */
    checkDiscountExpiration() {
      const now = /* @__PURE__ */ new Date();
      this.cards.forEach((card) => {
        const discountUntil = card.dataset.discountUntil;
        if (discountUntil) {
          const expirationDate = new Date(discountUntil);
          const isExpired = now > expirationDate;
          const originalPrices = card.querySelectorAll(".subscription-plans__original-price");
          originalPrices.forEach((el) => {
            el.classList.toggle("hidden", isExpired);
          });
          const discountIndicator = card.querySelector(".subscription-plans__discount-active");
          if (discountIndicator) {
            discountIndicator.style.display = isExpired ? "none" : "inline-flex";
          }
          card.dataset.discountExpired = isExpired ? "true" : "false";
        }
      });
    }
    /**
     * Public method to manually refresh discount status
     */
    refreshDiscounts() {
      this.checkDiscountExpiration();
    }
    /**
     * Public method to set billing programmatically
     */
    setBilling(billing) {
      if (billing !== this.currentBilling) {
        this.currentBilling = billing;
        this.updateToggleState();
        this.updatePricing();
      }
    }
    /**
     * Get current billing selection
     */
    getBilling() {
      return this.currentBilling;
    }
  };
  function initSubscriptionPlans() {
    const containers = document.querySelectorAll('[data-component="subscription-plans"]');
    containers.forEach((container) => {
      const defaultBilling = container.dataset.defaultBilling || "monthly";
      new SubscriptionPlans({
        container,
        defaultBilling
      });
    });
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initSubscriptionPlans);
  } else {
    initSubscriptionPlans();
  }
})();
