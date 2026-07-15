/**
 * Hero Dialog Editor JavaScript
 * Hides secondary CTA link/target fields when secondary CTA text is empty.
 */
(function(document, $, Coral) {
    'use strict';

    var HIDDEN_CLASS = 'cmp-hero--hidden';
    var style = document.createElement('style');
    style.textContent = '.' + HIDDEN_CLASS + ' { display: none !important; }';
    document.head.appendChild(style);

    function getFieldContainer(dialog, fieldName) {
        var field = dialog.find('[name="' + fieldName + '"]');
        if (!field.length) {
            return $();
        }
        var wrapper = field.closest('.coral-Form-fieldwrapper');
        if (wrapper.length) {
            return wrapper;
        }
        wrapper = field.closest('.coral3-Form-fieldwrapper');
        if (wrapper.length) {
            return wrapper;
        }
        return field.parent();
    }

    function toggleSecondaryCta(dialog) {
        var textField = dialog.find('[name="./secondaryCtaText"]');
        var hasText = textField.length && String(textField.val() || '').trim().length > 0;
        var linkContainer = getFieldContainer(dialog, './secondaryCtaLink');
        var externalContainer = getFieldContainer(dialog, './secondaryCtaExternal');

        if (hasText) {
            linkContainer.removeClass(HIDDEN_CLASS);
            externalContainer.removeClass(HIDDEN_CLASS);
        } else {
            linkContainer.addClass(HIDDEN_CLASS);
            externalContainer.addClass(HIDDEN_CLASS);
        }
    }

    function initDialog(dialog) {
        if (!dialog.find('.cmp-hero__editor').length) {
            return;
        }

        toggleSecondaryCta(dialog);

        dialog.on('change input', '[name="./secondaryCtaText"]', function() {
            toggleSecondaryCta(dialog);
        });
    }

    $(document).on('dialog-loaded', function(event) {
        var dialog = event.dialog;
        if (dialog && dialog.length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 150);
        }
    });

    $(document).on('foundation-contentloaded', function(event) {
        var container = $(event.target);
        var dialog = container.closest('.cq-dialog');
        if (dialog.length && dialog.find('.cmp-hero__editor').length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 200);
        }
    });

})(document, Granite.$, Coral);
