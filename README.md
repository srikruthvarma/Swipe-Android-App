# Swipe Product Management App

This is a comprehensive Android application built with Jetpack Compose that allows users to upload products to an API. It features a modern, multi-screen architecture with a splash screen, a dynamic home dashboard, a list of all public products, and a dedicated screen for user-specific uploads.

The app is built with an offline-first approach, ensuring a seamless user experience even without an internet connection. All user-added products are saved locally, and a robust background process handles uploads to prevent the UI from freezing.

## ✨ Features
-   **Main UI**: This app follows stock ui , so based on the theme or the color of your colour pallet in your phone         the app changes its colour, even supports light and drak theme
-   **Tabbed Navigation**: A clean, three-tab layout for easy navigation between Home, Products, and My Uploads.
-   **Dynamic Home Screen**:
    -   A personalized, time-based greeting (e.g., "Good morning!").
    -   A modern dashboard showing statistics for total and pending uploads.
    -   A list of the user's 3 most recently added products.
-   **"All Products" Screen**:
    -   Fetches and displays a list of all public products.
    -   Features a collapsing `LargeTopAppBar` with an animated refresh button.
    -   Includes a pill-shaped search bar for filtering products.
-   **"My Uploads" Screen**:
    -   A dedicated screen showing only the user's own added products from a local database.
    -   Displays pending items in a "grayed out" state with a clock icon (Optimistic UI).
-   **Full Offline Support**:
    -   if the user is offline and he uploads in the "my uploads section you will the product will be grayed out              which means it waiting for the online connection..
    -   All user-added products are saved to a local Room database.
    -   `WorkManager` handles the entire upload process (including slow file I/O) in a separate background process
-   **User Feedback**: Provides clear feedback via snackbars, system notifications, and an animated "Refreshed!" text.
-   **Empty States**: Displays friendly messages for offline states, empty lists, and "no search results" scenarios.

## 🛠 Tech Stack & Architecture

-   **UI**: Jetpack Compose
-   **Architecture**: MVVM
-   **Navigation**: Navigation Compose
-   **Asynchronous Programming**: Kotlin Coroutines & Flow
-   **Networking**: Retrofit
-   **Dependency Injection**: Koin
-   **Database**: Room for local persistence.
-   **Background Processing**: WorkManager for reliable, decoupled uploads.
-   **Image Loading**: Coil
-   **Custom Font**: Montserrat

## 🚀 How to Build and Run

1.  **Clone the repository**.
2.  **Open in Android Studio**: Use the latest stable version of Android Studio.
3.  **Sync Gradle**: Allow the IDE to download and sync all project dependencies.
4.  **Run the App**: Click the 'Run' button (▶️) and select an emulator or a physical Android device.

## 📂 Project Structure

-   **/data**: The data layer, including models, network services (Retrofit), local database (Room), the background `WorkManager`, and the repository.
-   **/di**: The Koin dependency injection module.
-   **/navigation**: Defines the app's navigation routes and screens.
-   **/presentation**: The UI layer, including all Composable screens and the `ProductViewModel`.
-   **/ui/theme**: Theme files for Jetpack Compose, including custom colors and typography.
-   **/util**: Utility classes, such as the `NotificationHelper`.
