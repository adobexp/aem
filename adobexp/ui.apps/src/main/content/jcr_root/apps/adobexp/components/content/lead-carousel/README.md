# Lead Carousel

AEM component for a full-bleed hero slideshow with overlay copy, an optional promo lockup, autoplay, pause/play, and progress-fill pagination.

Maps to the Modular UI Kit `LeadCarousel` static component.

## Configuration

### Settings
- **Accessible Label**: Screen-reader label for the carousel landmark (default: Featured stories)
- **Autoplay**: Start the slideshow automatically (default: on)
- **Slide Interval (ms)**: Time each slide is shown before advancing (default: 7000)

### Slides
Composite multifield. Each slide can author:
- **Image** + **Alt Text**
- **Title** and **Text**
- **CTA Text**, **CTA Link**, **Open in new tab**
- Optional promo stats: left/right **value** + **label** (rendered top-right when present)

## Usage

Add **Lead Carousel** from **Adobe XP Components - Content**. Theme tokens come from **AdobeXP - Lead Carousel Theme Configuration**.
