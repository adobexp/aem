# Flow Diagram

AEM Component for displaying an animated architecture diagram. Stages of nodes are connected by SVG
connectors that are measured from the live DOM and animated with a flowing dash.

## Configuration

This component can be configured through the following tabs:

### Content
- **Title**: Section title above the diagram
- **Subtitle**: Supporting text below the title
- **Caption**: Explanatory line rendered below the diagram
- **ARIA Label**: Accessible name for the diagram section (default: 'Architecture flow')

### Stages
- **Stages**: List of pipeline stages, left to right. Connectors are drawn between consecutive
  stages, so the order here is the flow order.
  - **Stage Label**: Chip displayed above the stage (e.g. 'Sources')
  - **Nodes**: One node per line, pipe-delimited as `Icon|Title|Subtitle|Tag`
    (e.g. `🌐|Websites|JavaScript SDK|Auto page views`). The icon is an emoji or short glyph and may
    be left empty (`|Websites|JS SDK`); the subtitle and tag are optional and trailing fields may be
    omitted. The tag renders as a small pill under the node.

### Appearance
- **Section Style**: Background treatment of the section (None, With Background, Muted Background)

## Usage

This component can be added to AEM pages through the component console.
Configure the fields according to the tab structure above to customize the component's appearance and behavior.

The connector overlay is generated at runtime by the client library, so at least two stages are
needed before any connectors appear. Nodes fade in on scroll and the whole animation is suppressed
when the visitor prefers reduced motion.
