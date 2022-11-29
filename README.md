# GithubUsers

GithubUsers is a sample project that presents a modern approach to Android app development.

The project tries to combine popular Android tools and to demonstrate best development practices by utilizing up to date tech-stack like Compose, Kotlin Flow and Hilt.

The sample app layers its presentation through MVVM presentation pattern. It relies on Paging3 to automate scroliing behavoior.

The app has two screen (Users list & User details) Their UI is build with mixed XML views and Compose.

## Download 
Go to the [Releases](https://github.com/Sabboo/GithubUsers/releases) to download the latest APK.

## Screenshots
Go to the [Screenshots Folder](https://github.com/Sabboo/GithubUsers/tree/main/Screenshots#readme) to preview.

## Libraries Used 

* Patterns and frameworks

MVVM (Model-View-ViewModel) using Google's Architecture components ViewModel, Flows, LifecycleObserver, etc.
* Database

Room Persistence Library, part of Google's Architecture components.
* Background Job processing

WorkManager was recomended hear to fit the requirements.
* Remote Call APIs

Retrofit 2 to perform HTTP requests.
* Dependency Injection

Hilt for dependency injection.
* Image Loading

Mixing between Glide (XML views) and Coil (Compose).

