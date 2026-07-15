(() => {
  var __getOwnPropNames = Object.getOwnPropertyNames;
  var __commonJS = (cb, mod) => function __require() {
    return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
  };

  // src/static-components/CallToAction/call-to-action.ts
  var require_call_to_action = __commonJS({
    "src/static-components/CallToAction/call-to-action.ts"() {
      (() => {
        const sections = document.querySelectorAll('[data-component="call-to-action"]');
        if (sections.length === 0) return;
        const prefersReducedMotion = window.matchMedia?.("(prefers-reduced-motion: reduce)")?.matches ?? false;
        sections.forEach((section) => {
          if (prefersReducedMotion) {
            section.classList.add("is-visible");
            return;
          }
          const observer = new IntersectionObserver(
            (entries) => {
              entries.forEach((entry) => {
                if (entry.isIntersecting) {
                  section.classList.add("is-visible");
                  observer.unobserve(section);
                }
              });
            },
            { threshold: 0.2 }
          );
          observer.observe(section);
        });
      })();
    }
  });
  require_call_to_action();
})();
