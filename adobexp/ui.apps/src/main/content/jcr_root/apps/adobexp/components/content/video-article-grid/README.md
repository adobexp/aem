# Video Article Grid

AEM Component for displaying a searchable, sortable, and paginated grid of video article cards. Articles are dynamically sourced from children of a configured content path. Each article page can have a video asset configured via the **Video** tab in Page Properties.

## Features

- **Dynamic Article Sourcing**: Articles are read from children of a configurable JCR content path.
- **Video Player with Hover Playback**: Each card can display a video that plays on hover. If a page image is configured, it is used as the video poster/thumbnail. If no image is configured, the browser renders the first frame of the video as the thumbnail.
- **Client-Side Search**: Real-time search filtering across title, description, and badge fields.
- **Sortable**: Sort by published date, created date, or title in ascending/descending order.
- **Paginated**: Configurable articles per page with previous/next navigation.
- **Fully Configurable Labels**: All UI text (search placeholder, pagination, sort labels, empty states) can be customized through the authoring dialog.
- **Responsive Design**: Adapts from 3 columns on desktop to 2 on tablet and 1 on mobile.

## Configuration

### Component Dialog

This component can be configured through the following tabs:

#### Content
- **Component Title**: Title text displayed as H2 heading.
- **Component Sub Title**: Subtitle text displayed below the title.
- **Articles Source Path**: Path to a content node whose children will be used as article items. Default root: `/content`.

#### Configuration
- **Cards per Row**: Number of cards per row (1-6, default: 3).
- **Play Video on Hover**: Auto-play video on hover (default: enabled).
- **Articles per Page**: Maximum articles per page for pagination (1-50, default: 9).
- **Default Sort By**: Initial sort field - Published Date, Created Date, or Title (default: Published Date).
- **Default Sort Order**: Initial sort direction - Ascending or Descending (default: Descending).

#### Labels
- **Search Placeholder**: Placeholder text for search input.
- **Previous/Next Page**: Button labels for pagination.
- **Page Indicator**: Page indicator text with `{currentPage}` and `{totalPages}` placeholders.
- **Sort By Label**: Accessible label for sort-by dropdown.
- **Sort By Published/Created/Title**: Labels for sort field options.
- **Sort Order Label**: Accessible label for sort order dropdown.
- **ASC/DSC Labels**: Labels for sort order options.
- **No Results/No Items Messages**: Empty state messages.

#### Style Options
- **Apply background color**: Toggles the background color on the section.

### Page Properties (Article Pages)

Each child page under the configured Articles Source Path is read by the component. The following page properties are used:

| Page Property | Maps To | Notes |
|---|---|---|
| **Page Title** (`pageTitle`) | Article title | Falls back to **Title** (`jcr:title`) if Page Title is blank |
| **Title** (`jcr:title`) | Article title (fallback) | Used when Page Title is not set |
| **Description** (`jcr:description`) | Article description | |
| **Page Image** (`image/fileReference`) | Video thumbnail / poster | If not set, the video's first frame is displayed |
| **Video Asset Path** (`videoAssetPath`) | Video source URL | Configured in the **Video** tab of Page Properties. DAM path to the video asset. |
| `badge` | Category badge label | Custom property |
| `publishedDate` | Published date | Custom Date property |
| `createdDate` / `jcr:created` | Created date | Falls back to `jcr:created` |
| `url` | Article link URL | Falls back to `<page-path>.html` |

### Video Tab (Page Properties)

A **Video** tab is added to the page component dialog at `adobexp/components/structure/page`. This tab provides:

- **Video Asset Path**: A path field rooted at `/content/dam` for selecting a video asset. This video is used by the Video Article Grid component for inline playback in the card thumbnail.

## Video Thumbnail Behaviour

| Page Image Configured? | Video Asset Configured? | Behaviour |
|---|---|---|
| Yes | Yes | Page image shown as poster; video plays on hover |
| No | Yes | Video first frame shown as thumbnail; video plays on hover |
| Yes | No | Static image thumbnail (no video playback) |
| No | No | Empty thumbnail area with play button |

## Technical Details

- **Sling Model**: `com.adobexp.aem.core.components.models.VideoArticleGridModel`
- **Resource Type**: `adobexp/components/content/video-article-grid`
- **Client Library Category**: `adobexp.components.videoarticlegrid.v1`
- **Component Group**: Adobe XP Components - Content

## Usage

1. Add the Video Article Grid component to an AEM page.
2. Open the component dialog and configure the title, subtitle, and articles source path.
3. Create child pages under the configured path.
4. For each child page:
   - Set the **Page Title** (or Title) in Page Properties.
   - Optionally add a **Page Image** for the video thumbnail.
   - Open the **Video** tab in Page Properties and select a video asset from DAM.
5. The component renders an interactive grid with video playback, search, sort, and pagination.
