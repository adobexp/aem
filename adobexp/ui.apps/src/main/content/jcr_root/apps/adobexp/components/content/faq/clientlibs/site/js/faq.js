/**
 * FAQ Component
 * Handles accordion-style expand/collapse functionality for FAQ items.
 * Only one item can be expanded at a time.
 */
(function() {
    'use strict';

    /**
     * Initializes the FAQ accordion functionality for a single FAQ section
     * @param {HTMLElement} faqSection - The FAQ section element
     */
    function initFaqSection(faqSection) {
        if (!faqSection) return;

        var faqItems = faqSection.querySelectorAll('.faq__item');
        var faqQuestions = faqSection.querySelectorAll('.faq__question');

        faqQuestions.forEach(function(question, index) {
            question.addEventListener('click', function() {
                var currentItem = faqItems[index];
                var isCurrentlyExpanded = currentItem.getAttribute('data-expanded') === 'true';

                // Close all items first
                faqItems.forEach(function(item) {
                    item.setAttribute('data-expanded', 'false');
                    var btn = item.querySelector('.faq__question');
                    if (btn) {
                        btn.setAttribute('aria-expanded', 'false');
                    }
                });

                // If the clicked item wasn't expanded, expand it
                if (!isCurrentlyExpanded) {
                    currentItem.setAttribute('data-expanded', 'true');
                    question.setAttribute('aria-expanded', 'true');
                }
            });
        });
    }

    /**
     * Initializes all FAQ components on the page
     */
    function initAllFaqComponents() {
        var faqSections = document.querySelectorAll('[data-component="faq"]');
        faqSections.forEach(function(faqSection) {
            initFaqSection(faqSection);
        });
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAllFaqComponents);
    } else {
        initAllFaqComponents();
    }

    // Also expose for dynamic content (e.g., after AJAX calls or AEM authoring mode)
    window.initFaqComponent = initFaqSection;
})();
