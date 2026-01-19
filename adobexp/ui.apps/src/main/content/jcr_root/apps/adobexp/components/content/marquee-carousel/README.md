# Marquee Carousel

AEM Component for displaying a horizontal scrolling carousel of images with a title and CTA button.

## Configuration

This component can be configured through the following tabs:

### Content
- **Title**: The main headline text for the marquee section (e.g., 'Ready To Create?')
- **CTA Button Text**: Text displayed on the CTA button (e.g., 'Get Started For Free')
- **CTA Link**: URL for the CTA button. Can be internal page path or external URL.
- **Open in new tab**: Open the CTA link in a new browser tab

### Images
- **Image Source**: Select how to add images to the carousel (Add Images Manually or Select DAM Folder)
- **Carousel Images**: List of images for the carousel (each image has:
  - Image: Select an image from the DAM
  - Alt Text: Alternative text for the image (for accessibility)
- **DAM Folder**: Select a DAM folder. All images from this folder will be displayed in the carousel.

### Settings
- **Animation Duration (seconds)**: Duration for one complete scroll cycle in seconds. Leave empty to use default (40s). Lower values = faster scrolling.
- **Pause on Hover**: Pause the carousel animation when user hovers over it
- **Apply Background Color**: Apply a background color to the section (controlled by theme)

## Usage

This component can be added to AEM pages through the component console. 
Configure the fields according to the tab structure above to customize the component's appearance and behavior.

