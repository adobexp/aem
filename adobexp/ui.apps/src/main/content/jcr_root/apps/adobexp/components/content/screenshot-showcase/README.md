# Screenshot Showcase

AEM Component for displaying product screenshots inside a browser chrome frame, with scroll
parallax and pointer tilt. Supports separate dark and light theme imagery.

## Configuration

This component can be configured through the following tabs:

### Content
- **Title**: Section title above the screenshot frames
- **Subtitle**: Supporting text below the title
- **Aria Label**: Accessible label for the section (default: 'Product screenshots')

### Frames
- **Screenshot Frames**: List of browser chrome frames
  - **Screenshot (Dark Theme)**: DAM asset shown in the dark theme. Required - frames without it are skipped
  - **Screenshot (Light Theme)**: DAM asset shown in the light theme. Leave empty to reuse the dark theme screenshot
  - **Alt Text**: Alternative text for both screenshots (falls back to the Caption Title)
  - **Address Bar Text**: Text shown in the fake browser address bar (e.g., 'app.infralytiqs.com/#/infralytiqs/reports')
  - **Caption Badge**: Short uppercase pill above the caption heading (e.g., 'Report dashboard')
  - **Caption Title**: Caption heading below the screenshot
  - **Caption Description**: Caption paragraph below the caption title
  - **Parallax Depth**: Multiplier applied to this frame's parallax drift (default: 1)
  - **Image Width (px)**: Intrinsic width used to avoid layout shift (default: 1280)
  - **Image Height (px)**: Intrinsic height used to avoid layout shift (default: 720)

### Appearance
- **Section Style**: Background treatment of the section (None, With Background, Muted Background)
- **Grid Columns**: Number of frames per row on tablet and above (1 or 2)
- **Parallax Strength (px)**: Scroll drift distance in pixels (default: 26, 0 disables parallax)
- **Enable Pointer Tilt**: Tilt frames toward the pointer on devices that support hover

## Usage

This component can be added to AEM pages through the component console.
Configure the fields according to the tab structure above to customize the component's appearance and behavior.

Theme colours are delivered as CSS custom properties by `SiteThemeServlet`, so the dark and light
theme appearance is controlled by the page theme rather than by this component.
