# Masonry Gallery

AEM Component for displaying a masonry-style gallery with images and videos.

## Configuration

This component can be configured through the following tabs:

### Gallery Media
- **Media Source**: Choose how to populate the gallery media items (Add Media Manually or Select DAM Folder)
- **Media Items**: List of media items (each item has:
  - Media Type: Select image or video
  - Media Path: Select an image or video from DAM
  - Title (Optional): Title text displayed on hover overlay
  - Alt Text: Alternative text for accessibility
  - Video Start Time (for videos only): Time in seconds to skip at the beginning of the video
  - Enable Video Autoplay on Viewport Visibility (for videos only): Automatically play video when it becomes visible in the viewport
- **DAM Folder**: Select a folder from DAM. All images and videos will be automatically loaded from this folder.
- **Default Video Start Time (seconds)**: Default start time for all videos loaded from the DAM folder
- **Enable Video Autoplay on Viewport Visibility**: Automatically play all videos from the DAM folder when they become visible in the viewport
- **Count of Assets to Display**: Number of assets to display from the DAM folder. Leave empty, 0, or -1 to display all assets.
- **Sort Assets By**: Sort assets before displaying. Select 'None' to keep original order from DAM folder.
- **Sort Order**: Sort order direction (only applies when Sort Assets is not 'None')

### CTA Button
- **CTA Link**: URL for the CTA button. Can be internal page path or external URL.
- **CTA Button Text**: Text displayed on the CTA button (e.g., 'View All Work', 'See More')
- **Open in new tab**: Open the CTA link in a new browser tab

### Style Options
- **Apply Background Color**: Apply a background color to the gallery section
- **Show Title Overlay on Hover**: Display title overlay on hover for items that have titles configured
- **Aria Label**: Accessible label for the gallery section (e.g., 'Photo Gallery', 'Media Gallery')

## Usage

This component can be added to AEM pages through the component console. 
Configure the fields according to the tab structure above to customize the component's appearance and behavior.

