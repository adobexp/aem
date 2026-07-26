# Code Snippet

AEM Component for displaying a tabbed code viewer with copy-to-clipboard, an optional line-number
gutter and dependency-free syntax highlighting.

## Configuration

This component can be configured through the following tabs:

### Content
- **Title**: Section title above the code viewer
- **Subtitle**: Supporting text below the title
- **Accessible Label**: Screen-reader label for the section (default: 'Code samples')

### Snippets
- **Snippets**: List of code samples, one tab each. The first snippet is shown by default.
  - **Tab Label**: Short label shown on the tab (e.g. 'Website (JS)')
  - **Language**: Highlighting language (JavaScript, JSON, Bash / shell, SQL, XML / HTML, Java, Plain text)
  - **Filename**: Optional caption shown in the tab bar (e.g. 'infralytiqs-bootstrap.js')
  - **Code**: The source, rendered exactly as typed. Markup is escaped automatically, so paste raw
    code. Avoid leading or trailing blank lines so the line numbers line up.

### Appearance
- **Section Style**: Background treatment of the section (None, With Background, Muted Background)
- **Show Line Numbers**: Show a line-number gutter to the left of every snippet (default: on)

## Usage

This component can be added to AEM pages through the component console.
Configure the fields according to the tab structure above to customize the component's appearance and behavior.

Highlighting, the line-number gutter, tab switching and copy-to-clipboard are all handled client-side
by `clientlibs/site/js/code-snippet.js`; the HTL only emits the raw, escaped source.
