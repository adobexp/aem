# Data Table

AEM Component for a responsive data table with configurable headers and rows.

## Configuration

This component can be configured through the following tabs:

### General
- **Title**: Section title above the table
- **Subtitle**: Supporting text below the title
- **Apply Background Color**: Apply a background color to the data table section

### Table
- **Column Headers**: Multifield of header cells (one per column)
- **Rows**: Multifield of rows. Each row has a **Cells (pipe-delimited)** field.
  - Separate cell values with `|`
  - Provide one value per column matching the header count
  - Inline HTML such as `<strong>` and `<code>` is allowed in cells
  - Example: `<strong>Java</strong> | <code>.java</code> | classes, methods`

## Usage

Add this component to AEM pages through the component browser under **Adobe XP Components - Content**. Theme (dark/light) is controlled by the page template.
