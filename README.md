# AEM Project Documentation

## Project Overview

This project is an Adobe Experience Manager (AEM) implementation that provides a set of reusable components for building dynamic and responsive web pages. The project follows AEM best practices and includes a comprehensive set of content and structure components for building modern digital experiences.

## Directory Structure

```
aem/
├── core/                    # Core Java components and services
│   └── src/main/java/com/adobexp/aem/core/components
├── ui.apps/                 # AEM application content
│   └── src/main/content/jcr_root/apps/adobexp/components
│       ├── content/         # Content components (editable content)
│       ├── container/       # Container components (layout containers)
│       ├── global/          # Global components (header, footer, etc.)
│       └── structure/       # Structure components (pages, templates)
└── README.md                # Project documentation
```

## Components

### Content Components

These components are used to display content on AEM pages and can be configured through the AEM authoring interface.

| Component | Description |
|-----------|-------------|
| **Video** | AEM Component for displaying a video with teaser overlay |
| **Two Tone Text Teaser** | AEM Component for displaying text with two different color tones and a CTA button |
| **Subscription Plans** | AEM Component for displaying subscription plans with pricing, features, and CTA buttons |
| **Services** | AEM Component for displaying a list of services with icons, headlines, and descriptions |
| **Rating** | AEM Component for displaying a star rating with title, subtitle, images, CTA button and rating values |
| **Quote** | AEM Component for displaying a quote with author information |
| **Masonry Gallery** | AEM Component for displaying a masonry-style gallery with images and videos |
| **Marquee Carousel** | AEM Component for displaying a horizontal scrolling carousel of images with a title and CTA button |
| **Looping Circle Gallery** | AEM Component for displaying a circular gallery with rotating images and an overlay message |
| **Lead Banner** | AEM Component for displaying a banner with primary headline, secondary headline items and secondary text |
| **Lead Media Section** | AEM Component for displaying a section with title, description, media (image or video) and a CTA button |
| **Hello World** | AEM Component for displaying a simple text message |
| **FAQ** | AEM Component for displaying frequently asked questions with expandable answers |
| **Count Up** | AEM Component for displaying animated counters that count up to a specified value |
| **Comparison** | AEM Component for displaying a comparison grid with three columns |
| **Compare Subscription** | AEM Component for comparing subscription plans |
| **Blob Image Section** | AEM Component for displaying content with a blob-shaped image/video container |
| **Text** | AEM Component for displaying rich text content |
| **Image** | AEM Component for displaying images |
| **Teaser** | AEM Component for displaying teaser content with title, description and link |
| **PDF Viewer** | AEM Component for displaying PDF documents |
| **Grid Control** | AEM Component for creating responsive grid layouts |
| **Experience Fragment** | AEM Component for displaying experience fragments |

### Container Components

These components provide layout containers for organizing content.

| Component | Description |
|-----------|-------------|
| **Container** | AEM Container component for organizing content |

### Structure Components

These components define the structure and hierarchy of AEM pages.

| Component | Description |
|-----------|-------------|
| **Directory Page** | AEM Component for directory page structure |
| **Locale Page** | AEM Component for locale page structure |
| **Root Page** | AEM Component for root page structure |
| **Page** | AEM Component for standard page structure |

### Global Components

These components are used throughout the site for consistent branding and navigation.

| Component | Description |
|-----------|-------------|
| **Site Banner** | AEM Component for site-wide banner |
| **Header** | AEM Component for site header |
| **Footer** | AEM Component for site footer |

## How to Use Components

1. **Add Components to Pages**: 
   - Navigate to AEM authoring interface
   - Edit a page and click "Insert Component"
   - Select the desired component from the component console

2. **Configure Components**:
   - Click on a component to open its configuration dialog
   - Configure fields according to the component's documentation
   - Use the "Content" and "Style" tabs to customize appearance and behavior

3. **Publish Pages**:
   - Once components are configured, save and publish the page
   - Components will be rendered according to their configuration

## Best Practices

1. **Component Reusability**: 
   - Design components to be reusable across multiple pages
   - Use consistent naming conventions

2. **Configuration Management**:
   - Keep configuration options focused and intuitive
   - Use descriptive field labels and help text

3. **Performance Optimization**:
   - Optimize media assets (images, videos)
   - Use lazy loading for images and videos when appropriate
   - Minimize component complexity

4. **Accessibility**:
   - Always provide alternative text for images
   - Ensure proper semantic HTML structure
   - Follow WCAG guidelines for color contrast and keyboard navigation

5. **Testing**:
   - Test components across different devices and screen sizes
   - Validate component behavior in both author and publish modes
   - Ensure components work with different content types

## Development Guidelines

1. **Component Structure**:
   - All components should follow the standard AEM component pattern
   - Include proper README.md files for each component
   - Implement appropriate HTL/Sightly templates

2. **Code Organization**:
   - Maintain clear separation between Java components and HTL templates
   - Use consistent naming conventions for files and folders
   - Follow AEM coding standards and best practices

3. **Version Control**:
   - Commit all component changes to version control
   - Document breaking changes in commit messages
   - Use feature branches for new component development

## Troubleshooting

1. **Component Not Appearing**:
   - Verify component is properly installed in AEM
   - Check component permissions in AEM authoring interface
   - Ensure component is added to the correct template

2. **Configuration Issues**:
   - Review component configuration documentation
   - Verify required fields are properly populated
   - Check for validation errors in component dialog

3. **Performance Issues**:
   - Review component complexity and optimize where necessary
   - Ensure media assets are properly optimized
   - Use AEM's built-in performance monitoring tools

## Support

For support with this AEM project, please contact the development team or refer to the component-specific documentation included in each component's directory.