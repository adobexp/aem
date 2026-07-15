(() => {
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };

  // src/static-components/Prose/prose.ts
  var require_prose = __commonJS({
    "src/static-components/Prose/prose.ts"() {
      (() => {
        const proseBlocks = document.querySelectorAll('[data-component="prose"]');
        if (proseBlocks.length === 0) return;
        const prefersReducedMotion = window.matchMedia?.("(prefers-reduced-motion: reduce)")?.matches ?? false;
        proseBlocks.forEach((prose) => {
          const blocks = prose.querySelectorAll(".prose__block");
          if (blocks.length === 0) return;
          if (prefersReducedMotion) {
            blocks.forEach((block) => block.classList.add("is-visible"));
            return;
          }
          const observer = new IntersectionObserver(
            (entries) => {
              entries.forEach((entry) => {
                if (!entry.isIntersecting) return;
                const block = entry.target;
                const index = parseInt(block.style.getPropertyValue("--i") || "0", 10);
                block.style.transitionDelay = `${index * 0.06}s`;
                block.classList.add("is-visible");
                observer.unobserve(block);
              });
            },
            { threshold: 0.1, rootMargin: "0px 0px -30px 0px" }
          );
          blocks.forEach((block, i) => {
            block.style.setProperty("--i", String(i));
            observer.observe(block);
          });
        });
      })();
    }
  });
  require_prose();
})();
