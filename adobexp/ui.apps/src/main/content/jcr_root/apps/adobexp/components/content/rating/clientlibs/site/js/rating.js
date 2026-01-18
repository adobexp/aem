(() => {
  var STAR_SVG_PATH = "M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z";

  function createStarSvg(type, fillPercentage) {
    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("class", "rating__star");
    svg.setAttribute("viewBox", "0 0 24 24");
    svg.setAttribute("fill", "currentColor");
    svg.setAttribute("aria-hidden", "true");
    if (type === "partial" && fillPercentage !== undefined) {
      svg.classList.add("rating__star--partial");
      const clipId = `star-clip-${Math.random().toString(36).substr(2, 9)}`;
      const defs = document.createElementNS("http://www.w3.org/2000/svg", "defs");
      const clipPath = document.createElementNS("http://www.w3.org/2000/svg", "clipPath");
      clipPath.setAttribute("id", clipId);
      const clipRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      clipRect.setAttribute("x", "0");
      clipRect.setAttribute("y", "0");
      clipRect.setAttribute("width", `${fillPercentage / 100 * 24}`);
      clipRect.setAttribute("height", "24");
      clipPath.appendChild(clipRect);
      defs.appendChild(clipPath);
      svg.appendChild(defs);
      const bgPath = document.createElementNS("http://www.w3.org/2000/svg", "path");
      bgPath.setAttribute("d", STAR_SVG_PATH);
      bgPath.setAttribute("class", "rating__star-bg");
      bgPath.style.color = "var(--rating-star-empty-color, #d1d5db)";
      svg.appendChild(bgPath);
      const fgPath = document.createElementNS("http://www.w3.org/2000/svg", "path");
      fgPath.setAttribute("d", STAR_SVG_PATH);
      fgPath.setAttribute("clip-path", `url(#${clipId})`);
      fgPath.setAttribute("class", "rating__star-fill");
      fgPath.style.color = "var(--rating-star-color, #fbbf24)";
      svg.appendChild(fgPath);
    } else {
      const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
      path.setAttribute("d", STAR_SVG_PATH);
      svg.appendChild(path);
      if (type === "empty") {
        svg.classList.add("rating__star--empty");
      }
    }
    return svg;
  }

  function renderStars(container, config) {
    const { rating, total } = config;
    let starsContainer = container.querySelector(".rating__stars");
    if (!starsContainer) {
      starsContainer = document.createElement("div");
      starsContainer.className = "rating__stars";
      starsContainer.setAttribute("role", "img");
      let starsRow = container.querySelector(".rating__stars-row");
      if (!starsRow) {
        starsRow = document.createElement("div");
        starsRow.className = "rating__stars-row";
        container.insertBefore(starsRow, container.firstChild);
      }
      starsRow.insertBefore(starsContainer, starsRow.firstChild);
    }
    starsContainer.setAttribute("aria-label", `${rating} out of ${total} stars`);
    starsContainer.innerHTML = "";
    const fullStars = Math.floor(rating);
    const partialFill = (rating - fullStars) * 100;
    const emptyStars = total - Math.ceil(rating);
    for (let i = 0; i < fullStars; i++) {
      starsContainer.appendChild(createStarSvg("full"));
    }
    if (partialFill > 0) {
      starsContainer.appendChild(createStarSvg("partial", partialFill));
    }
    for (let i = 0; i < emptyStars; i++) {
      starsContainer.appendChild(createStarSvg("empty"));
    }
    let scoreElement = container.querySelector(".rating__score");
    if (!scoreElement) {
      scoreElement = document.createElement("span");
      scoreElement.className = "rating__score";
      const starsRow = container.querySelector(".rating__stars-row");
      if (starsRow) {
        starsRow.appendChild(scoreElement);
      }
    }
    scoreElement.textContent = rating.toString();
  }

  function parseRatingConfig(element) {
    const rating = parseFloat(element.dataset.starRating || "0");
    const total = parseInt(element.dataset.starTotal || "5", 10);
    return {
      rating: Math.max(0, Math.min(rating, total)),
      // Clamp rating between 0 and total
      total: Math.max(1, total)
      // Ensure at least 1 star
    };
  }

  function initRatingComponents() {
    const ratingContainers = document.querySelectorAll(".rating__stars-section[data-star-rating]");
    ratingContainers.forEach((container) => {
      const config = parseRatingConfig(container);
      renderStars(container, config);
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initRatingComponents);
  } else {
    initRatingComponents();
  }

  window.RatingComponent = {
    init: initRatingComponents,
    render: renderStars,
    parseConfig: parseRatingConfig
  };
})();