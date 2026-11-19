# EVC - E-Video Cloud (Movie Streaming Demo App)

## Project Overview
This is a JavaFX-based Movie Streaming Demo App inspired by Netflix, Disney+, and Amazon Prime. It was built for the Interactive Multimedia group assignment. The app loads data locally (no database) and streams trailers directly from YouTube.

## Current Status (What is DONE)
- **UI Design:** Dark-mode Netflix-style interface with a Sidebar and Header.
- **Categories:** 3 Categories (Action, Comedy, Sci-Fi) with exactly 8 movies each.
- **Thumbnails & Titles:** Images and titles are displaying correctly.
- **5-Video Scroll Rule:** Each category displays exactly 5 cards and allows horizontal scrolling for the rest.
- **Hover Effects:** Movie cards scale up smoothly when hovered.
- **Sound Effects:** Custom category-specific sounds play on hover. A click sound plays on buttons.
- **Loading Animation:** A custom, pulsing "EVC" circle displays while the WebView loads.
- **Custom Buttons:** No `javafx.scene.control.Button` class was used. All buttons are custom Labels with click events.

## Notes
- Trailer playback uses YouTube embeds. If an individual trailer stops working, check that its video ID in `MovieData.java` is available and allows embedding. Playback can also be affected by internet connectivity or YouTube restrictions on embedded browsers.

## Optional UI Polish
   - The "Settings" button in the sidebar currently does nothing. 
   - The Search bar in the header does not filter movies yet. You can implement an `onKeyTyped` listener to filter the `MovieData` Map.
   - Removed "Your Account" and Profile icon since there is no login system.

## Project Structure
- `src/main/java/com/evc/`
  - `App.java` (Main entry point)
  - `MainController.java` (Wires the UI)
  - `model/` (Contains `Movie.java` and `MovieData.java`)
  - `view/` (Contains all UI components: Sidebar, Header, MovieCard, FeaturedMovies, CategoryBar, VideoPlayer, LoadingAnimation)
- `src/main/resources/com/evc/`
  - `styles.css` (All CSS styling)
  - `images/posters/` (Movie images)
  - `sounds/` (Hover and click sounds)

## How to Run
1. Ensure you have Java 25 (LTS) and Maven installed.
2. Open the terminal in the root folder.
3. Run `mvn clean compile` to download dependencies and compile.
4. Run the app using `mvn javafx:run` or by clicking "Run" on `App.java` in your IDE.