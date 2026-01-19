# Count Up

AEM Component for displaying animated counters that count up to a specified value.

## Configuration

This component can be configured through the following tabs:

### Header Content
- **Title**: The main headline text for the section (e.g., 'Used by Millions')
- **Subtitle**: Supporting text that appears below the title

### Counter Items
- **Counter Items**: List of counter statistics
  - **End Value**: The final number to count up to (e.g., 10 for '10M')
  - **Unit**: Suffix to append to the number (None, Million (M), Thousand (K), Billion (B), Percent (%), Plus (+))
  - **Custom Unit**: Enter custom unit if not in the dropdown (overrides Unit selection)
  - **Label**: Description text below the number (e.g., 'Total Downloads')
  - **Start Value**: The initial number to start counting from (default: 0)
  - **Decimal Places**: Number of decimal places to display (default: 0)

### Animation
- **Animation Duration (ms)**: Duration of the count-up animation in milliseconds (default: 2000)

### Style Options
- **Apply Background Color**: Apply a background color to the section

## Usage

This component can be added to AEM pages through the component console. 
Configure the fields according to the tab structure above to customize the component's appearance and behavior.

