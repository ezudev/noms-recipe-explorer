# Noms - Recipe Explorer App

**Noms** is a modern Android application for exploring, searching, filtering, and bookmarking recipes. Built using **Kotlin**, **Jetpack Compose (Material 3)** following the **MVVM (Model-View-ViewModel)** architectural pattern.

---

## Setup & Build Instructions

### Prerequisites
- **Android Studio** Ladybug (2024.2.1) or newer.
- **JDK**: Java 11 or higher.
- **Android SDK**: Min SDK 24 (Android 7.0), Target/Compile SDK 37.
- **Device / Emulator**: Android 7.0+ device or emulator with internet connectivity.

### Building & Running the App

1. **Open Project**:
   Open the root project folder in Android Studio.

2. **Sync Gradle**:
   Let Android Studio sync Gradle dependencies automatically or run:
   ```bash
   ./gradlew sync
   ```

3. **Run Unit Tests**:
   To execute local unit tests:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Build & Install Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   Or select `app` in Android Studio and press **Run (Shift + F10)** to launch on a connected device or emulator.

---

## Key Features

### 1. Recipe List & Infinite Scrolling
- **10 Recipes Per Page**: Fetches paginated recipes using `limit=10` and `skip` parameters from `https://dummyjson.com/recipes`.
- **Automated Infinite Scrolling**: Automatically loads subsequent recipe batches as the user reaches the bottom of the list.
- **Scroll-to-Top Button**: Appears in the section header when scrolled down past item 0, smoothly animating back to the top on tap.
- **Auto-Reset on Filter Change**: Filter changes (Difficulty / Meal Type) automatically reset the list scroll position back to top item 0.
- **Pagination Error Recovery**: If internet disconnects mid-scroll while fetching page 2+, an interactive error footer appears (*"Connection lost. Tap to retry loading more."*) with one-tap retry functionality.

### 2. Interactive Recipe Details Screen
- **Hero Image Header**: High-resolution recipe image with a circular yellow back button and circular white bookmark button.
- **Title & Rating Pill**: Bold recipe name, cuisine subtitle, and yellow star rating badge (e.g., `★ 4.5`).
- **4 Vertical Capsule Stat Badges**: Yellow capsules with white top icon circles for **Time**, **Servings**, **Calories**, and **Difficulty**.
- **Interactive Ingredients Checklist**: Tap any ingredient to check it off with instant strikethrough styling as you cook.

### 3. Live Search & Dynamic Filtering
- **Optimized Live Search**: Real-time search using `https://dummyjson.com/recipes/search?q=query`, with a 400ms input delay to reduce unnecessary API requests.
- **Dynamic Meal Type Tiles**: Category carousel tiles are dynamically extracted **only from currently loaded recipes** in memory (no empty pre-hardcoded category tiles).
- **Client-Side Filter Bar**: Horizontal filter chip bar for **Difficulty** (*"Easy"*, *"Medium"*, *"Hard"*) and **Meal Type** (*"Lunch"*, *"Dinner"*, *"Snack"*, *"Dessert"*, etc.) with a single-tap **Reset Filters** option.

### 4. Bookmarks
- **Persistent Bookmarking**: Tap the bookmark icon on any recipe card or detail view to save recipes locally using `SharedPreferences` (`noms_favorites_prefs`).
- **Dedicated Saved Tab**: Browse saved favorites offline with full search functionality within saved bookmarks.

### 5. Offline Caching & Pull-to-Refresh
- **Full Recipe Caching**: Automatically persists **all accumulated recipes** (up to 50+) into `RecipeCacheStorage` (`SharedPreferences`).
- **Cumulative Pull-to-Refresh**: Swipe down anywhere on the screen (headers, search bar, empty spaces, or recipe cards) to re-fetch all previously loaded pages in full, preserving loaded item count and category filters.
- **Offline Banner**: Displays an *"Offline Mode"* indicator banner when viewing cached data.
- **User-Friendly Error Messages**: Technical network host errors (`UnknownHostException`) are automatically sanitized into clean messages (*"No internet connection. Please check your network settings and try again."*).

### 6. Image & GIF Loading
- **Glide Integration**: Powered by Glide (`com.github.bumptech.glide:compose`) for automatic LRU memory caching, disk caching, and progressive rendering.
- **Animated GIF Loader**: Custom animated food carousel GIF loader (`food_carousel.gif`) with light grey background placeholder during image fetch.

## Assumptions Made

1. **Client-Side Filtering**: Per instructions, Difficulty and Meal Type filtering are executed client-side on recipes currently loaded in memory.
2. **Cumulative Refresh**: Pull-to-refresh preserves the number of items loaded by the user so far (e.g. 50 recipes), ensuring active filter categories remain populated.
3. **Single Source of Truth**: `RecipeRepository` coordinates network calls, falling back to `RecipeCacheStorage` when offline.
