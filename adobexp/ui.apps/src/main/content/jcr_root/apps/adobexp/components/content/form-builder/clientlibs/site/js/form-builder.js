/**
 * Form Builder Component JavaScript
 * Handles form validation, submission, and interactive field components
 */
(function() {
  'use strict';

  // Dialog Manager Class - Handles success/error dialogs
  class DialogManager {
    constructor() {
      this.dialogOverlay = null;
    }

    show(title, message, serverResponse, isSuccess, onClose) {
      this.remove();
      const overlay = document.createElement('div');
      overlay.className = 'form-builder__dialog-overlay';
      overlay.innerHTML = `
        <div class="form-builder__dialog ${isSuccess ? 'form-builder__dialog--success' : 'form-builder__dialog--error'}">
          <div class="form-builder__dialog-icon">
            ${isSuccess ? this.getSuccessIcon() : this.getErrorIcon()}
          </div>
          <h3 class="form-builder__dialog-title">${title}</h3>
          <p class="form-builder__dialog-message">${message}</p>
          ${serverResponse ? `<div class="form-builder__dialog-response">${serverResponse}</div>` : ''}
          <button type="button" class="form-builder__dialog-close">Close</button>
        </div>
      `;
      document.body.appendChild(overlay);
      this.dialogOverlay = overlay;
      
      requestAnimationFrame(() => {
        overlay.classList.add('form-builder__dialog-overlay--visible');
      });
      
      const closeBtn = overlay.querySelector('.form-builder__dialog-close');
      closeBtn?.addEventListener('click', () => {
        this.remove();
        onClose();
      });
      
      overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
          this.remove();
          onClose();
        }
      });
    }

    remove() {
      if (this.dialogOverlay) {
        this.dialogOverlay.classList.remove('form-builder__dialog-overlay--visible');
        setTimeout(() => {
          this.dialogOverlay?.remove();
          this.dialogOverlay = null;
        }, 300);
      }
    }

    getSuccessIcon() {
      return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <path d="M9 12l2 2 4-4"></path>
      </svg>`;
    }

    getErrorIcon() {
      return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="15" y1="9" x2="9" y2="15"></line>
        <line x1="9" y1="9" x2="15" y2="15"></line>
      </svg>`;
    }
  }

  // Multi-Select Dropdown Class
  class MultiSelectDropdown {
    constructor(container) {
      this.container = container;
      this.trigger = container.querySelector('.form-builder__dropdown-trigger');
      this.dropdown = container.querySelector('.form-builder__dropdown-menu');
      this.searchInput = container.querySelector('.form-builder__dropdown-search');
      this.optionsList = container.querySelector('.form-builder__dropdown-options');
      this.hiddenInput = container.querySelector('.form-builder__dropdown-value');
      this.selectedValues = new Set();
      this.options = [];
      this.parseOptions();
      this.init();
    }

    parseOptions() {
      const optionElements = this.optionsList.querySelectorAll('.form-builder__dropdown-option');
      optionElements.forEach((opt) => {
        const value = opt.getAttribute('data-value') || '';
        const label = opt.textContent?.trim() || '';
        this.options.push({ value, label });
        if (opt.hasAttribute('data-selected')) {
          this.selectedValues.add(value);
        }
      });
      this.updateDisplay();
    }

    init() {
      this.trigger.addEventListener('click', (e) => {
        e.stopPropagation();
        this.toggle();
      });
      
      this.searchInput?.addEventListener('input', () => {
        this.filterOptions();
      });
      
      this.optionsList.addEventListener('click', (e) => {
        const target = e.target;
        const option = target.closest('.form-builder__dropdown-option');
        if (option) {
          const value = option.getAttribute('data-value') || '';
          this.toggleOption(value, option);
        }
      });
      
      document.addEventListener('click', (e) => {
        if (!this.container.contains(e.target)) {
          this.close();
        }
      });
      
      this.container.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
          this.close();
        }
      });
    }

    toggle() {
      const isOpen = this.container.classList.contains('form-builder__dropdown--open');
      if (isOpen) {
        this.close();
      } else {
        this.open();
      }
    }

    open() {
      this.container.classList.add('form-builder__dropdown--open');
      this.searchInput?.focus();
    }

    close() {
      this.container.classList.remove('form-builder__dropdown--open');
      if (this.searchInput) {
        this.searchInput.value = '';
        this.filterOptions();
      }
    }

    filterOptions() {
      const searchTerm = this.searchInput?.value.toLowerCase() || '';
      const options = this.optionsList.querySelectorAll('.form-builder__dropdown-option');
      options.forEach((opt) => {
        const label = opt.textContent?.toLowerCase() || '';
        const matches = label.includes(searchTerm);
        opt.style.display = matches ? '' : 'none';
      });
    }

    toggleOption(value, optionElement) {
      if (this.selectedValues.has(value)) {
        this.selectedValues.delete(value);
        optionElement.classList.remove('form-builder__dropdown-option--selected');
      } else {
        this.selectedValues.add(value);
        optionElement.classList.add('form-builder__dropdown-option--selected');
      }
      this.updateDisplay();
    }

    updateDisplay() {
      const selectedLabels = [];
      this.options.forEach((opt) => {
        if (this.selectedValues.has(opt.value)) {
          selectedLabels.push(opt.label);
        }
      });
      
      const displayText = this.trigger.querySelector('.form-builder__dropdown-text');
      if (displayText) {
        displayText.textContent = selectedLabels.length > 0 
          ? selectedLabels.join(', ') 
          : this.container.getAttribute('data-placeholder') || 'Select options...';
      }
      
      if (this.hiddenInput) {
        this.hiddenInput.value = Array.from(this.selectedValues).join(',');
        this.hiddenInput.dispatchEvent(new Event('change', { bubbles: true }));
      }
      
      // Update has-value class
      if (selectedLabels.length > 0) {
        this.container.classList.add('form-builder__dropdown--has-value');
      } else {
        this.container.classList.remove('form-builder__dropdown--has-value');
      }
    }

    getValues() {
      return Array.from(this.selectedValues);
    }

    reset() {
      this.selectedValues.clear();
      this.optionsList.querySelectorAll('.form-builder__dropdown-option--selected')
        .forEach((opt) => opt.classList.remove('form-builder__dropdown-option--selected'));
      this.updateDisplay();
    }
  }

  // Date Picker Class
  class DatePicker {
    constructor(container) {
      this.container = container;
      this.input = container.querySelector('.form-builder__date-input');
      this.calendar = container.querySelector('.form-builder__calendar');
      this.currentDate = new Date();
      this.selectedDate = null;
      this.init();
    }

    init() {
      this.input.addEventListener('click', () => {
        this.toggle();
      });
      
      document.addEventListener('click', (e) => {
        if (!this.container.contains(e.target)) {
          this.close();
        }
      });
      
      this.renderCalendar();
    }

    toggle() {
      const isOpen = this.container.classList.contains('form-builder__date--open');
      if (isOpen) {
        this.close();
      } else {
        this.open();
      }
    }

    open() {
      this.container.classList.add('form-builder__date--open');
    }

    close() {
      this.container.classList.remove('form-builder__date--open');
    }

    renderCalendar() {
      const year = this.currentDate.getFullYear();
      const month = this.currentDate.getMonth();
      const monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'];
      
      const firstDay = new Date(year, month, 1).getDay();
      const daysInMonth = new Date(year, month + 1, 0).getDate();
      const daysInPrevMonth = new Date(year, month, 0).getDate();
      
      let html = `
        <div class="form-builder__calendar-header">
          <button type="button" class="form-builder__calendar-nav" data-nav="prev">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M15 18l-6-6 6-6"></path>
            </svg>
          </button>
          <span class="form-builder__calendar-title">${monthNames[month]} ${year}</span>
          <button type="button" class="form-builder__calendar-nav" data-nav="next">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"></path>
            </svg>
          </button>
        </div>
        <div class="form-builder__calendar-weekdays">
          <span>Su</span><span>Mo</span><span>Tu</span><span>We</span><span>Th</span><span>Fr</span><span>Sa</span>
        </div>
        <div class="form-builder__calendar-days">
      `;
      
      // Previous month days
      for (let i = firstDay - 1; i >= 0; i--) {
        html += `<button type="button" class="form-builder__calendar-day form-builder__calendar-day--other" disabled>${daysInPrevMonth - i}</button>`;
      }
      
      // Current month days
      const today = new Date();
      for (let day = 1; day <= daysInMonth; day++) {
        const isToday = today.getDate() === day && today.getMonth() === month && today.getFullYear() === year;
        const isSelected = this.selectedDate && 
          this.selectedDate.getDate() === day && 
          this.selectedDate.getMonth() === month && 
          this.selectedDate.getFullYear() === year;
        
        const classes = ['form-builder__calendar-day'];
        if (isToday) classes.push('form-builder__calendar-day--today');
        if (isSelected) classes.push('form-builder__calendar-day--selected');
        
        html += `<button type="button" class="${classes.join(' ')}" data-day="${day}">${day}</button>`;
      }
      
      // Next month days
      const totalCells = firstDay + daysInMonth;
      const remainingCells = 7 - (totalCells % 7);
      if (remainingCells < 7) {
        for (let i = 1; i <= remainingCells; i++) {
          html += `<button type="button" class="form-builder__calendar-day form-builder__calendar-day--other" disabled>${i}</button>`;
        }
      }
      
      html += '</div>';
      this.calendar.innerHTML = html;
      
      // Attach event listeners
      this.calendar.querySelector('[data-nav="prev"]')?.addEventListener('click', (e) => {
        e.stopPropagation();
        this.currentDate.setMonth(this.currentDate.getMonth() - 1);
        this.renderCalendar();
      });
      
      this.calendar.querySelector('[data-nav="next"]')?.addEventListener('click', (e) => {
        e.stopPropagation();
        this.currentDate.setMonth(this.currentDate.getMonth() + 1);
        this.renderCalendar();
      });
      
      this.calendar.querySelectorAll('.form-builder__calendar-day[data-day]').forEach((day) => {
        day.addEventListener('click', (e) => {
          e.stopPropagation();
          const dayNum = parseInt(day.getAttribute('data-day') || '1');
          this.selectDate(dayNum);
        });
      });
    }

    selectDate(day) {
      this.selectedDate = new Date(
        this.currentDate.getFullYear(),
        this.currentDate.getMonth(),
        day
      );
      const formattedDate = this.formatDate(this.selectedDate);
      this.input.value = formattedDate;
      this.input.dispatchEvent(new Event('change', { bubbles: true }));
      this.renderCalendar();
      this.close();
    }

    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }

    reset() {
      this.selectedDate = null;
      this.currentDate = new Date();
      this.input.value = '';
      this.renderCalendar();
    }
  }

  // Time Picker Class
  class TimePicker {
    constructor(container) {
      this.container = container;
      this.input = container.querySelector('.form-builder__time-input');
      this.picker = container.querySelector('.form-builder__time-picker');
      this.hoursColumn = container.querySelector('.form-builder__time-hours');
      this.minutesColumn = container.querySelector('.form-builder__time-minutes');
      this.selectedHour = 12;
      this.selectedMinute = 0;
      this.init();
    }

    init() {
      this.renderColumns();
      this.input.addEventListener('click', () => {
        this.toggle();
      });
      
      document.addEventListener('click', (e) => {
        if (!this.container.contains(e.target)) {
          this.close();
        }
      });
    }

    renderColumns() {
      let hoursHtml = '';
      for (let h = 0; h < 24; h++) {
        const hourStr = String(h).padStart(2, '0');
        const isSelected = h === this.selectedHour;
        hoursHtml += `<button type="button" class="form-builder__time-option ${isSelected ? 'form-builder__time-option--selected' : ''}" data-hour="${h}">${hourStr}</button>`;
      }
      this.hoursColumn.innerHTML = hoursHtml;
      
      let minutesHtml = '';
      for (let m = 0; m < 60; m += 5) {
        const minStr = String(m).padStart(2, '0');
        const isSelected = m === this.selectedMinute;
        minutesHtml += `<button type="button" class="form-builder__time-option ${isSelected ? 'form-builder__time-option--selected' : ''}" data-minute="${m}">${minStr}</button>`;
      }
      this.minutesColumn.innerHTML = minutesHtml;
      
      // Attach event listeners for hours
      this.hoursColumn.querySelectorAll('[data-hour]').forEach((btn) => {
        btn.addEventListener('click', (e) => {
          e.stopPropagation();
          this.selectedHour = parseInt(btn.getAttribute('data-hour') || '0');
          this.updateDisplay();
          this.renderColumns();
        });
      });
      
      // Attach event listeners for minutes
      this.minutesColumn.querySelectorAll('[data-minute]').forEach((btn) => {
        btn.addEventListener('click', (e) => {
          e.stopPropagation();
          this.selectedMinute = parseInt(btn.getAttribute('data-minute') || '0');
          this.updateDisplay();
          this.renderColumns();
        });
      });
      
      this.scrollToSelected();
    }

    scrollToSelected() {
      const selectedHour = this.hoursColumn.querySelector('.form-builder__time-option--selected');
      const selectedMin = this.minutesColumn.querySelector('.form-builder__time-option--selected');
      if (selectedHour) {
        selectedHour.scrollIntoView({ block: 'center', behavior: 'smooth' });
      }
      if (selectedMin) {
        selectedMin.scrollIntoView({ block: 'center', behavior: 'smooth' });
      }
    }

    toggle() {
      const isOpen = this.container.classList.contains('form-builder__time--open');
      if (isOpen) {
        this.close();
      } else {
        this.open();
      }
    }

    open() {
      this.container.classList.add('form-builder__time--open');
      setTimeout(() => this.scrollToSelected(), 100);
    }

    close() {
      this.container.classList.remove('form-builder__time--open');
    }

    updateDisplay() {
      const hourStr = String(this.selectedHour).padStart(2, '0');
      const minStr = String(this.selectedMinute).padStart(2, '0');
      this.input.value = `${hourStr}:${minStr}`;
      this.input.dispatchEvent(new Event('change', { bubbles: true }));
    }

    reset() {
      this.selectedHour = 12;
      this.selectedMinute = 0;
      this.input.value = '';
      this.renderColumns();
    }
  }

  // Form Validator Class
  class FormValidator {
    validateField(field) {
      const isRequired = field.hasAttribute('data-required') || 
        field.closest('.form-builder__field')?.hasAttribute('data-required');
      const pattern = field.getAttribute('data-pattern');
      const errorMessage = field.getAttribute('data-error-message') || 
        field.closest('.form-builder__field')?.getAttribute('data-error-message') || 
        'This field is invalid';
      
      let value = '';
      const fieldType = field.getAttribute('type') || field.tagName.toLowerCase();
      
      if (fieldType === 'checkbox') {
        value = field.checked ? 'checked' : '';
      } else if (field.classList.contains('form-builder__dropdown-value')) {
        value = field.value;
      } else {
        value = field.value?.trim() || '';
      }
      
      // Check required
      if (isRequired && !value) {
        return {
          isValid: false,
          errorMessage: field.getAttribute('data-required-message') || 
            field.closest('.form-builder__field')?.getAttribute('data-required-message') || 
            'This field is required'
        };
      }
      
      // Check pattern
      if (pattern && value) {
        try {
          const regex = new RegExp(pattern);
          if (!regex.test(value)) {
            return { isValid: false, errorMessage };
          }
        } catch (e) {
          console.warn('Invalid regex pattern:', pattern);
        }
      }
      
      return { isValid: true, errorMessage: '' };
    }

    showError(fieldContainer, message) {
      fieldContainer.classList.add('form-builder__field--error');
      let errorEl = fieldContainer.querySelector('.form-builder__field-error');
      if (!errorEl) {
        errorEl = document.createElement('div');
        errorEl.className = 'form-builder__field-error';
        fieldContainer.appendChild(errorEl);
      }
      errorEl.textContent = message;
    }

    clearError(fieldContainer) {
      fieldContainer.classList.remove('form-builder__field--error');
      const errorEl = fieldContainer.querySelector('.form-builder__field-error');
      if (errorEl) {
        errorEl.textContent = '';
      }
    }
  }

  // Main Form Builder Class
  class FormBuilder {
    constructor(form) {
      this.form = form;
      this.config = this.getFormConfig();
      this.validator = new FormValidator();
      this.dialogManager = new DialogManager();
      this.multiSelects = new Map();
      this.datePickers = new Map();
      this.timePickers = new Map();
      this.init();
    }

    getFormConfig() {
      let customHeaders = {};
      const headersAttr = this.form.getAttribute('data-headers');
      if (headersAttr) {
        try {
          customHeaders = JSON.parse(headersAttr);
        } catch (e) {
          console.warn('FormBuilder: Invalid data-headers JSON:', headersAttr);
        }
      }
      return {
        action: this.form.getAttribute('data-action') || this.form.action || '',
        method: (this.form.getAttribute('data-method') || this.form.method || 'POST').toUpperCase(),
        contentType: this.form.getAttribute('data-content-type') || 'application/json',
        successMessage: this.form.getAttribute('data-success-message') || 'Form submitted successfully!',
        errorMessage: this.form.getAttribute('data-error-message') || 'An error occurred. Please try again.',
        customHeaders
      };
    }

    init() {
      this.initMultiSelects();
      this.initDatePickers();
      this.initTimePickers();
      this.initToggleSwitches();
      this.bindBlurValidation();
      
      this.form.addEventListener('submit', (e) => {
        e.preventDefault();
        this.handleSubmit();
      });
      
      const resetBtn = this.form.querySelector('[type="reset"], [data-reset]');
      resetBtn?.addEventListener('click', (e) => {
        e.preventDefault();
        this.resetForm();
      });
    }

    initMultiSelects() {
      const containers = this.form.querySelectorAll('.form-builder__dropdown');
      containers.forEach((container) => {
        this.multiSelects.set(container, new MultiSelectDropdown(container));
      });
    }

    initDatePickers() {
      const containers = this.form.querySelectorAll('.form-builder__date');
      containers.forEach((container) => {
        this.datePickers.set(container, new DatePicker(container));
      });
    }

    initTimePickers() {
      const containers = this.form.querySelectorAll('.form-builder__time');
      containers.forEach((container) => {
        this.timePickers.set(container, new TimePicker(container));
      });
    }

    initToggleSwitches() {
      const toggles = this.form.querySelectorAll('.form-builder__toggle-input');
      toggles.forEach((toggle) => {
        toggle.addEventListener('change', () => {
          const container = toggle.closest('.form-builder__field');
          if (container) {
            this.validator.clearError(container);
          }
        });
      });
    }

    bindBlurValidation() {
      const fields = this.form.querySelectorAll(
        'input:not([type="submit"]):not([type="reset"]):not([type="hidden"]), textarea, select'
      );
      fields.forEach((field) => {
        field.addEventListener('blur', () => {
          this.validateSingleField(field);
        });
        field.addEventListener('change', () => {
          this.validateSingleField(field);
        });
      });
    }

    validateSingleField(field) {
      const container = field.closest('.form-builder__field');
      if (!container) return true;
      
      const result = this.validator.validateField(field);
      if (!result.isValid) {
        this.validator.showError(container, result.errorMessage);
        return false;
      } else {
        this.validator.clearError(container);
        return true;
      }
    }

    validateAllFields() {
      let isFormValid = true;
      const fieldContainers = this.form.querySelectorAll('.form-builder__field');
      
      fieldContainers.forEach((container) => {
        const field = container.querySelector(
          'input:not([type="submit"]):not([type="reset"]), textarea, select, .form-builder__dropdown-value'
        );
        if (field) {
          const result = this.validator.validateField(field);
          if (!result.isValid) {
            this.validator.showError(container, result.errorMessage);
            isFormValid = false;
          } else {
            this.validator.clearError(container);
          }
        }
      });
      
      return isFormValid;
    }

    collectFormData() {
      const data = {};
      const fieldContainers = this.form.querySelectorAll('.form-builder__field');
      
      fieldContainers.forEach((container) => {
        const field = container.querySelector(
          'input:not([type="submit"]):not([type="reset"]), textarea, select, .form-builder__dropdown-value'
        );
        if (field && field.name) {
          const fieldType = field.getAttribute('type') || field.tagName.toLowerCase();
          switch (fieldType) {
            case 'checkbox':
              data[field.name] = field.checked;
              break;
            case 'hidden':
              if (field.classList.contains('form-builder__dropdown-value')) {
                // Multi-select dropdown
                const value = field.value;
                data[field.name] = value ? value.split(',') : [];
              } else {
                // Hidden text field
                data[field.name] = field.value || '';
              }
              break;
            default:
              data[field.name] = field.value || '';
          }
        }
      });
      
      // Collect toggle values
      const toggles = this.form.querySelectorAll('.form-builder__toggle-input');
      toggles.forEach((toggle) => {
        if (toggle.name) {
          data[toggle.name] = toggle.checked;
        }
      });

      // Collect hidden input fields placed directly in the form (not inside .form-builder__field)
      const hiddenInputs = this.form.querySelectorAll('input[type="hidden"].form-builder__hidden-input');
      hiddenInputs.forEach((input) => {
        if (input.name && !(input.name in data)) {
          data[input.name] = input.value || '';
        }
      });
      
      return data;
    }

    async handleSubmit() {
      if (!this.validateAllFields()) {
        const firstError = this.form.querySelector('.form-builder__field--error');
        firstError?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        return;
      }
      
      const formData = this.collectFormData();
      const submitBtn = this.form.querySelector('[type="submit"]');
      
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.classList.add('form-builder__submit--loading');
      }
      
      try {
        const response = await this.sendRequest(formData);
        if (response.ok) {
          const responseData = await response.json().catch(() => ({}));
          const serverMessage = responseData.message || responseData.data || '';
          this.dialogManager.show(
            'Success',
            this.config.successMessage,
            serverMessage,
            true,
            () => this.resetForm()
          );
        } else {
          const errorData = await response.json().catch(() => ({}));
          const serverError = errorData.message || errorData.error || errorData.data || '';
          this.dialogManager.show(
            'Error',
            this.config.errorMessage,
            serverError,
            false,
            () => {} // Don't reset on error
          );
        }
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Network error occurred';
        this.dialogManager.show(
          'Error',
          this.config.errorMessage,
          errorMessage,
          false,
          () => {}
        );
      } finally {
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.classList.remove('form-builder__submit--loading');
        }
      }
    }

    async sendRequest(data) {
      const headers = {
        'Content-Type': this.config.contentType,
        'Accept': 'application/json',
        ...this.config.customHeaders
      };
      const options = {
        method: this.config.method,
        headers
      };
      
      let url = this.config.action;
      
      if (this.config.method === 'GET') {
        const params = new URLSearchParams();
        Object.entries(data).forEach(([key, value]) => {
          if (Array.isArray(value)) {
            value.forEach((v) => params.append(key, v));
          } else if (typeof value === 'boolean') {
            params.append(key, String(value));
          } else if (value !== null) {
            params.append(key, value);
          }
        });
        url += (url.includes('?') ? '&' : '?') + params.toString();
      } else {
        options.body = JSON.stringify(data);
      }
      
      return fetch(url, options);
    }

    resetForm() {
      this.form.reset();
      this.multiSelects.forEach((ms) => ms.reset());
      this.datePickers.forEach((dp) => dp.reset());
      this.timePickers.forEach((tp) => tp.reset());
      
      const errorFields = this.form.querySelectorAll('.form-builder__field--error');
      errorFields.forEach((field) => {
        this.validator.clearError(field);
      });
    }
  }

  // Initialize Form Builder
  function initFormBuilder() {
    const forms = document.querySelectorAll('[data-component="form-builder"]');
    forms.forEach((form) => {
      new FormBuilder(form);
    });
  }

  // Initialize on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initFormBuilder);
  } else {
    initFormBuilder();
  }
})();
