# Mentor Home Loans App

Mentor Home Loans is a robust and modern Android application built using Kotlin and clean architecture principles. It serves as a comprehensive portal for users to manage their home loans, track transactions, view statements, upload documents, make repayments, and receive customer support.

## 🚀 Features

- **Onboarding & Authentication:** Seamless user onboarding, login, and secure session management.
- **Dashboard:** A central hub giving users a quick overview of their loan accounts and upcoming dues.
- **Transactions & Statements:** Detailed transaction histories and downloadable loan statements.
- **Repayment Module:** Easy and secure in-app loan repayments.
- **Document Management:** Upload, view, and manage essential loan documents.
- **Profile & Settings:** Personalized user profiles and customizable app settings.
- **Notifications:** Real-time push notifications for important loan updates.
- **Support:** In-app customer support and query resolution.

## 🏗 Architecture & Tech Stack

The application follows **Clean Architecture** combined with the **MVVM (Model-View-ViewModel)** design pattern. It is structured into multiple layers to ensure separation of concerns, scalability, and testability.

### Key Technologies
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Modern declarative UI)
- **Dependency Injection:** Hilt / Dagger
- **Networking:** Retrofit & OkHttp
- **Local Data:** DataStore (for preferences) & Room Database (for local caching)
- **Security:** Encrypted Shared Preferences & secure token storage
- **Concurrency:** Kotlin Coroutines & Flow

### Module Structure
- `core/` - Foundational components, themes, navigation, common extensions, and network utilities.
- `domain/` - Business logic, use cases, models, and repository interfaces.
- `data/` - Remote API service implementations, local storage logic, and data mappers.
- `feature/` - Individual isolated feature modules (e.g., login, dashboard, transactions).

## 🛠 Getting Started

### Prerequisites
- Android Studio (latest stable version recommended)
- Java Development Kit (JDK) 17+
- Android SDK API 34+

### Build & Run
1. Clone the repository and open the project in Android Studio.
2. Allow Gradle to sync all dependencies (specified in `gradle/libs.versions.toml`).
3. Run the app on an Android Emulator or a physical device via USB debugging.

## 📂 Project Phases Completed

1. **Build System:** Configuration of Gradle version catalogs and root setups.
2. **Core & Domain:** Base extensions, constants, and domain data models.
3. **Use Cases & Repositories:** Business logic implementation spanning 17 use cases.
4. **Network & Security:** Secure network configurations and API client setups.
5. **Data Layer:** DTOs, API interfaces, local entities, and mock repository implementations.
6. **Core UI:** Theme setups, standard components, and navigation routes.
7. **Features:** Development of all 12 key user-facing features.
8. **App Assembly:** Dependency injection, manifest configuration, and final entry points.

---
*Built with ❤️ by the Mentor Home Loans Development Team.*
