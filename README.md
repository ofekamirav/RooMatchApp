# Roomatch

Roomatch is an Android application designed to help roommates and property seekers find the perfect living match easily and intuitively.

## Features

🔍 **Discover Matches:** Swipe through personalized roommate and property matches.

✅ **Smart Preferences:** Advanced filtering based on your lifestyle and needs.

🏠 **Property Management:** Owners can add, manage, and publish rental properties.

👫 **Roommate Profiles:** See potential roommates' profiles and match scores.

📍 **Google Places Integration:** Autocomplete for property addresses.

🔐 **Secure Login/Register:** Using JWT and Google Authentication.

🚀 **Offline-First:** Caching & fallback for smooth UX even with poor connectivity.

✨ **Gemini Suggestion:** Using Gemini to generate your personal bio.


## Tech Stack
| Layer               | Technology                                   |
|---------------------|----------------------------------------------|
| **Language**        | Kotlin                                       |
| **UI**              | Jetpack Compose, Material 3                  |
| **Architecture**    | MVVM + Clean Architecture                    |
| **Networking**      | Ktor Server, Retrofit Client                 |
| **Database**        | Room, MongoDB Atlas            
**Navigation**              | Compose Destinations - Raam Costa
**Image Uploading** | Cloudinary SDK 
| **Places API**      | Google Places SDK                            |
| **Coroutines**      | Kotlin Coroutines, Flow                      |


## Installation

### Prerequisites

- Android Studio (latest recommended)

- Android SDK 29+

- JDK 11+

- Active Internet connection

- RooMatch server must be up and reachable

#### 1. Clone Project

```bash
git clone https://github.com/YOUR_USERNAME/roomatch.git
cd roomatch

```
#### 2. Build the app `./gradlew build`
#### 3. Change the `$BASE_URL` in `app/di/AppDependencies`
        if Amulator -> BASE_URL=10.0.2.2
        if Device   -> BASE_URL = your_computer_ip_address
#### 4. Run the app on Amulator or Device.

## Environment Variables

To run this project, you will need to add the following environment variables to your `gradle.properties` file

```bash
CLOUD_NAME=your_cloudinary_cloud_name
API_KEY=your_cloudinary_api_key
API_SECRET=your_cloudinary_api_secret
GOOGLE_PLACES_API_KEY=your_google_places_api_key
RELEASE_ID_TOKEN=your_release_id_token_secret_for_google_OAuth
```


## Project Structure 

```
com.example.roomatchapp
├── data
│   ├── base                # Base classes or abstractions
│   ├── local
│   │   ├── dao             # Room DAO interfaces
│   │   ├── session         # User session and token management
│   │   └── AppLocalDB.kt
│   │  
│   ├── model               # Shared data models
│   ├── remote
│   │   ├── api             # API service interfaces and implementations (Ktor)
│   │   └── dto             # Data transfer objects
│   └── repository          # Repository implementations
├── di                     # Dependency injection setup (AppDependencies)
├── domain
│   └── repository         # Repository interfaces (Domain layer)
├── presentation
│   ├── components         # Reusable composables
│   ├── login              # Login screen logic and UI
│   ├── navigation         # Compose Destinations & Navigation Graph
│   ├── owner              # Owner-specific screens
│   ├── register           # Registration screens
│   ├── roommate           # Roommate-specific screens
│   ├── screens            # Shared/general screens
│   └── theme              # Theme (colors, typography, etc.)
├── utils                  # Helper and extension functions
```
## Collaborators
- [@OfekAmirav](https://www.github.com/ofekamirav)
- [@NitzanNaveh](https://www.github.com/NitzanNaveh)
- [@BarKobi](https://www.github.com/barkobi40)
- [@ZurShani](https://www.github.com/ZurShani)
- [@EladShirazi](https://www.github.com/eladshirazi)

## License

This project is licensed under the [MIT](https://choosealicense.com/licenses/mit/) License.

## Author

- [@OfekAmirav](https://www.github.com/ofekamirav)



