# HES Hunt - Agent Documentation

This app is a personal tool to track Historic Environment Scotland’s History Hunt collectible cards.

## Technical Stack
- **UI**: Jetpack Compose with Material 3.
- **Architecture**: MVVM with Use Cases and Repository pattern.
- **Dependency Injection**: Koin with Annotations and KSP.
- **Data Storage**: Room for collected cards status.
- **Network**: OkHttp for fetching GeoJSON property data.
- **Serialization**: Kotlin Serialization for data parsing.
- **Map**: MapLibre Compose SDK for Android.
- **Testing**: MockK and Turbine for Unit Tests.

## Project Rules
- **Compose**: `Modifier` must be the last parameter if present.
- **Formatting**: Files must end with a single newline.
- **Commas**: No dangling commas at the end of lists.
- **Imports**: No unused imports.
- **UiState**: Defined next to `ViewModel` as `internal`.
- **Testing**: Prioritize unit tests in `test` folder. No manual UI testing by agent.
- **DI**: Prefer Koin annotations (`@Single`, `@Factory`, `@KoinViewModel`).
- **cards.json**: Never edit the cards file.

## Data Sources
- **Cards**: `app/src/main/assets/cards.json`
- **Properties**: GeoJSON from `https://inspire.hes.scot/arcgis/rest/services/HES/Properties_in_care_points/MapServer/0/query?where=1%3D1&outFields=*&f=geojson`

