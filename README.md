# F1 Calendar App

An Android app for tracking the current Formula 1 season, race schedules, and championship standings.

## Features

- **📅 Race Calendar**: View all races for the current F1 season with dates and times in UK timezone (BST)
- **🏆 Driver Standings**: Track the championship standings for all drivers
- **🏎️ Constructor Standings**: Monitor team championship positions
- **🏁 Race Results**: View detailed results for completed races including:
  - All session results (Practice, Qualifying, Sprint, Race)
  - Driver positions with team colors
  - Fastest lap indicator
  - Driver numbers and team information
- **⏰ Upcoming Races**: See session times for upcoming race weekends in UK timezone
- **💾 Offline Support**: Race data is cached locally for offline viewing

## Tech Stack

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - UI components and theming
- **MVVM Architecture** - Clean architecture pattern
- **Retrofit** - API client for F1 data
- **Room Database** - Local data persistence
- **Coroutines & Flow** - Asynchronous programming
- **Navigation Compose** - Navigation component

## Data Source

This app uses the Ergast F1 API (via api.jolpi.ca mirror) to fetch:
- Race schedules and results
- Driver and constructor standings
- Session information and timings

## Build

### Requirements

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 34
- Minimum Android version: 7.0 (API 24)

### Building locally

1. Clone the repository
```bash
git clone <repository-url>
cd fennec-f1
```

2. Open the project in Android Studio

3. Sync Gradle and build
```bash
./gradlew assembleDebug
```

The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`

## CI/CD

The app includes a GitHub Actions workflow that automatically builds a debug APK on every commit. The APK can be downloaded from the Actions artifacts.

## Features Breakdown

### Tabs

1. **Races Tab**:
   - Lists all races in the current season
   - Shows race status (completed/upcoming)
   - Displays session times in UK timezone for upcoming races
   - Click on a race to view detailed information

2. **Drivers Tab**:
   - Championship standings for all drivers
   - Points and position information
   - Team colors and driver numbers
   - Points difference from leader

3. **Constructors Tab**:
   - Team championship standings
   - Total points for each constructor
   - Visual team color indicators

### Race Detail Screen

- Circuit information and location
- List of all sessions (FP1, FP2, FP3, Qualifying, Sprint, Race)
- Session results with detailed driver information
- Fastest lap indicator (⚡) for race results
- Qualifying times (Q1, Q2, Q3)
- Team colors and driver numbers throughout

## Screenshots

(Screenshots would go here once the app is running)

## Future Enhancements

- Driver and team photos/logos
- Live timing during race weekends
- Push notifications for race starts
- Lap-by-lap telemetry data
- Historical season data
- Dark mode toggle

## License

This project is built for educational purposes.

## Credits

- F1 data provided by the Ergast Developer API
- UI designed following F1 branding guidelines
- Built with ❤️ for F1 fans
