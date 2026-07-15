(() => {
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };

  // src/static-components/Hero/hero.ts
  var require_hero = __commonJS({
    "src/static-components/Hero/hero.ts"() {
      (() => {
        const heroes = document.querySelectorAll('[data-component="hero"]');
        if (heroes.length === 0) return;
        const prefersReducedMotion = window.matchMedia?.("(prefers-reduced-motion: reduce)")?.matches ?? false;
        heroes.forEach((hero) => {
          if (prefersReducedMotion) {
            hero.classList.add("is-visible");
            return;
          }
          requestAnimationFrame(() => {
            hero.classList.add("is-visible");
          });
        });
      })();
    }
  });
  require_hero();
})();
