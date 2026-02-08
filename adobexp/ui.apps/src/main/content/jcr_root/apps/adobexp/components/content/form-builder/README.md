# Form Builder Component

A comprehensive form builder component that provides a customizable form with various field types, validation, and submission handling.

## Overview

The Form Builder component renders a complete form section with:
- Section title and description
- Configurable form action URL and HTTP method
- Success and error messages
- Submit and reset buttons
- Dropzone container for form field child components

## Component Group

Adobe XP Components - Content

## Usage

Add the Form Builder component to your page and configure:

1. **Content Tab**: Set form title and description
2. **Form Configuration Tab**: Set action URL, HTTP method, and content type
3. **Messages Tab**: Configure success and error messages
4. **Buttons Tab**: Customize button text and visibility
5. **Style Options Tab**: Enable background and set form ID

Then add child field components from the "Form Builder Fields" dropzone.

## Available Field Components

All field components are located at `form-builder/components/`:

| Component | Description | Features |
|-----------|-------------|----------|
| **Text Field** | Standard text input | Label, placeholder, pattern validation, required |
| **Text Area** | Multi-line text input | Label, placeholder, rows, required |
| **Email Field** | Email input with validation | Built-in email regex, required |
| **Phone Field** | Phone number input | Phone validation pattern, helper text |
| **Dropdown Field** | Multi-select dropdown | Searchable, multifield options, required |
| **Calendar Field** | Date picker | Calendar popup, date selection |
| **Time Field** | Time picker | Hour/minute selection |
| **Toggle Switch** | Boolean toggle | On/off switch, default state |
| **Checkbox** | Checkbox with label | Rich text label (supports links), required |

## Component Dialog Properties

### Content Tab
- `formTitle` (textfield): Section title displayed above the form
- `formDescription` (textarea): Description text below the title

### Form Configuration Tab
- `formAction` (textfield): URL where form data is submitted
- `formMethod` (select): HTTP method (POST, GET, PUT)
- `contentType` (select): Content type for submission

### Messages Tab
- `successMessage` (textarea): Message shown on successful submission
- `errorMessage` (textarea): Message shown on failed submission

### Buttons Tab
- `submitButtonText` (textfield): Text for submit button
- `showResetButton` (checkbox): Show/hide reset button
- `resetButtonText` (textfield): Text for reset button

### Style Options Tab
- `withBackground` (checkbox): Apply background color
- `formId` (textfield): Unique HTML ID for the form

## Clientlibs

**Category**: `adobexp.formbuilder`
**Dependencies**: `adobexp.base`

The clientlib includes:
- CSS: Complete form styling with theme support (light/dark)
- JS: Form validation, submission, calendar/time pickers, dropdown functionality

## Data Attributes

The component uses data attributes for JavaScript functionality:

### Form Level
- `data-component="form-builder"`: Identifies the form
- `data-action`: Form submission URL
- `data-method`: HTTP method
- `data-content-type`: Request content type
- `data-success-message`: Success message
- `data-error-message`: Error message

### Field Level
- `data-required`: Field is required
- `data-required-message`: Required validation message
- `data-pattern`: Regex validation pattern
- `data-error-message`: Validation error message

## Theme Support

The component supports both light and dark themes via CSS custom properties:
- `--standard-primary-site-text-color`
- `--standard-secondary-site-text-color`
- `--form-field-bg`
- `--form-field-border`
- `--form-dropdown-bg`
- `--primary-text-color`
- `--site-body-bg`

## Example Structure

```
form-builder
├── .content.xml
├── _cq_dialog/.content.xml
├── _cq_editConfig.xml
├── form-builder.html
├── README.md
├── clientlibs/
│   └── site/
│       ├── .content.xml
│       ├── css.txt
│       ├── js.txt
│       ├── css/form-builder.css
│       └── js/form-builder.js
└── components/
    ├── textfield/
    ├── textarea/
    ├── email/
    ├── phone/
    ├── dropdown/
    ├── calendar/
    ├── time/
    ├── toggle/
    └── checkbox/
```

## Dependencies

- Sling Model: `FormBuilderDropdown` for dropdown field options
- Base clientlib: `adobexp.base`
- Responsive grid: `wcm/foundation/components/responsivegrid`

## Validation

Client-side validation is handled via:
- Required field validation
- Regex pattern validation (for text, email, phone fields)
- Checkbox required validation
- Dropdown selection validation

Validation errors are displayed inline below each field.

## Form Submission

The form handles submission via JavaScript:
1. Validates all fields
2. Collects form data as JSON
3. Sends request to configured action URL
4. Shows success/error dialog based on response
5. Optionally resets form on success
