/**
 * Page Header Dialog Editor JavaScript
 * Minimal scaffold for dialog parity with other components.
 */
(function(document, $, Coral) {
    'use strict';

    function initDialog(dialog) {
        if (!dialog.find('.cmp-page-header__editor').length) {
            return;
        }
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
        if (dialog.length && dialog.find('.cmp-page-header__editor').length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 200);
        }
    });

})(document, Granite.$, Coral);
