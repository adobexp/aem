# Steps Timeline

AEM Component for displaying numbered walkthrough steps connected by a vertical rail that fills as the reader scrolls.

## Configuration

This component can be configured through the following tabs:

### Content
- **Title**: Section title above the steps
- **Subtitle**: Supporting text below the title
- **Accessible Label**: Screen reader label for the section (default: 'Getting started steps')

### Steps
- **Steps**: List of walkthrough steps, rendered in authored order
  - **Step Title**: Headline of the step (e.g. 'Create your account with SSO')
  - **Description**: Rich text body of the step (supports lists, links, inline code)
  - **Meta Chip**: Short uppercase chip shown under the step (e.g. '2 min', 'Console'). Leave blank to hide the chip
  - **Step Number Override**: Optional. Leave blank to number the steps automatically from 1. Set it only when the marker must read something else (e.g. '1a' or '0')

### Appearance
- **Section Style**: Background treatment applied to the whole section (None, With Background, Muted Background)

## Behaviour

Each step fades in as it enters the viewport, and the vertical rail fills to track the last
revealed step. When the reader prefers reduced motion, or the browser has no
`IntersectionObserver`, every step is shown immediately and the rail is filled completely.

## Usage

This component can be added to AEM pages through the component console.
Configure the fields according to the tab structure above to customize the component's appearance and behavior.
