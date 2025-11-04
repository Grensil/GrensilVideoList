# Grensil Video List

Android application for browsing videos and photos from Pexels API.(https://www.pexels.com/api/)

## Features

- Browse popular videos and photos
- Bookmark favorite media
- Material Design 3 UI
- Multi-module architecture

## Architecture

- **Clean Architecture** with MVVM pattern
- **Multi-module** structure (app, core, feature modules)
- **Dependency Injection** with Hilt
- **Reactive Programming** with Kotlin Flow
- **Local Storage** with Room Database
- **Network** with Retrofit + OkHttp

## Setup

1. Clone the repository
```bash
git clone [repository-url]
cd GrensilVideoList
```

2. Get API Key from [Pexels](https://www.pexels.com/api/)

3. Create `local.properties` in the project root:
```properties
sdk.dir=/path/to/android/sdk
API_KEY=your_pexels_api_key_here
```

4. Build and run
```bash
./gradlew assembleDebug
```

## Module Structure

```
app/                    # Application module
core/
  ├── data/            # Data layer (repositories, data sources)
  ├── domain/          # Domain layer (use cases, models)
  ├── network/         # Network layer (API services)
  └── designsystem/    # Design system (themes, components)
feature/
  ├── main/            # Home screen feature
  └── bookmark/        # Bookmark feature
```

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **DI**: Hilt
- **Network**: Retrofit, OkHttp
- **Database**: Room
- **Image Loading**: Coil
- **Async**: Kotlin Coroutines + Flow
- **Paging**: Paging 3

## Security

- API keys managed via `local.properties` (not committed to git)
- ProGuard rules for release builds
- HTTPS only for network requests

## License

[Add your license here]
