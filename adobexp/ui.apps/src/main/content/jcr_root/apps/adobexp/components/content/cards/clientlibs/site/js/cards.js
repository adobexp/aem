(() => {
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };

  // src/static-components/Cards/cards.ts
  var require_cards = __commonJS({
    "src/static-components/Cards/cards.ts"() {
      (() => {
        const sections = document.querySelectorAll('[data-component="cards"]');
        if (sections.length === 0) return;
        const prefersReducedMotion = window.matchMedia?.("(prefers-reduced-motion: reduce)")?.matches ?? false;
        sections.forEach((section) => {
          const items = section.querySelectorAll(".cards__item");
          if (items.length === 0) return;
          if (prefersReducedMotion) {
            items.forEach((item) => item.classList.add("is-visible"));
            return;
          }
          const observer = new IntersectionObserver(
            (entries) => {
              entries.forEach((entry) => {
                if (!entry.isIntersecting) return;
                const item = entry.target;
                const index = parseInt(item.style.getPropertyValue("--i") || "0", 10);
                item.style.transitionDelay = `${index * 0.08}s`;
                item.classList.add("is-visible");
                observer.unobserve(item);
              });
            },
            { threshold: 0.15, rootMargin: "0px 0px -40px 0px" }
          );
          items.forEach((item, i) => {
            item.style.setProperty("--i", String(i));
            observer.observe(item);
          });
        });
      })();
    }
  });
  require_cards();
})();
