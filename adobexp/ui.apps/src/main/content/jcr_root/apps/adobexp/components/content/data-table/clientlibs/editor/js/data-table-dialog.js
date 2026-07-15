/**
 * Data Table Dialog Editor JavaScript
 * Ensures the pipe-delimited row cells hint is visible when the dialog loads.
 */
(function(document, $, Coral) {
    'use strict';

    function initDialog(dialog) {
        if (!dialog.find('.cmp-data-table__editor').length) {
            return;
        }

        var hint = dialog.find('.cmp-data-table__pipe-hint');
        if (hint.length) {
            hint.attr('aria-live', 'polite');
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
        if (dialog.length && dialog.find('.cmp-data-table__editor').length) {
            setTimeout(function() {
                initDialog(dialog);
            }, 200);
        }
    });

})(document, Granite.$, Coral);
