# Metric Tiles

AEM Component for displaying KPI tiles with a gradient icon face and a value that counts up when the tile scrolls into view.

## Configuration

This component can be configured through the following tabs:

### Content
- **Title**: Section title above the metric tiles
- **Subtitle**: Supporting text below the title
- **Accessible Label**: Screen-reader label for the section landmark (default: 'Key metrics')

### Tiles
- **Metric Tiles**: List of KPI tiles
  - **Icon**: Emoji or short glyph shown above the value
  - **Label**: Name of the metric (e.g. 'Monthly active users')
  - **Value**: Target number the tile counts up to (e.g. 1284, 4820000, 38.2). Thousands separators are ignored; a value that is not a number is skipped
  - **Prefix**: Text rendered immediately before the number (e.g. '$')
  - **Suffix**: Text rendered immediately after the number (e.g. ' TB', '%')
  - **Decimal Places**: Number of decimal places to display (default: 0)
  - **Abbreviate Large Numbers**: Shorten large numbers, e.g. 4820000 renders as 4.8M
  - **Accent Color**: Accent colour of this tile's gradient face. Must be a CSS colour (hex, rgb/rgba, hsl/hsla, or a named colour); invalid values are ignored
  - **Caption**: Small supporting line below the tile face

### Appearance
- **Section Style**: Background treatment of the section (None, With Background, Muted Background)
- **Grid Columns**: Number of tiles per row on large screens (2, 3, 4 — default 4)

## Behaviour

Tiles fade and slide in with a staggered delay when they enter the viewport, and each value counts up
to its target. Visitors who prefer reduced motion see the final values immediately with no animation.

## Usage

This component can be added to AEM pages through the component console.
Configure the fields according to the tab structure above to customize the component's appearance and behavior.
