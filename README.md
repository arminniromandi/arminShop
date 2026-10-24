# 🛍️ ArminShop

> **A modern, resilient, offline-first Android e-commerce client crafted with Jetpack Compose and Clean Architecture.**

ArminShop is built around a simple principle: **an unstable network should never ruin a shopping experience.** By treating local storage as the primary source of truth and syncing with the backend in the background, users can browse products, manage their cart, and explore deals instantly — online or offline.

---

## ⚡ Tech Stack at a Glance

| Layer / Role | Tools & Libraries | Purpose |
| :--- | :--- | :--- |
| **Language & Build** | Kotlin 2.1 • Gradle 9.6 • KSP | Fast compilation & type-safe codebase |
| **UI Framework** | Jetpack Compose • Material 3 | Declarative, modern UI with fluid animations |
| **Navigation** | Jetpack Navigation Compose | Tab-based nested graphs with backstack isolation |
| **Local Database (SSOT)** | Room Database 2.6 • DataStore | Fast local caching, reactive streams (`Flow`) |
| **Dependency Injection** | Dagger Hilt 2.60 | Clean dependency management & testability |
| **Networking & API** | Retrofit 2.11 • OkHttp 4.12 • Gson | REST API client with automatic token interception |
| **Security** | AndroidX Security Crypto (MasterKey) | Hardware-backed JWT storage (`AES256-GCM`) |
| **Image Loading** | Coil Compose 2.7 | Image caching with animated shimmer placeholders |
| **Testing** | JUnit 4 • Kotlin Coroutines Test | Unit testing core business & calculation logic |

---

## ✨ What Makes ArminShop Special?

### 📴 True Offline-First Architecture
Rather than waiting on network calls before rendering screens, the UI listens directly to **Room Database** via reactive Kotlin `Flow`s.
- **Zero latency:** Cached catalogs, special offers, and cart items appear immediately on launch.
- **Silent background sync:** When online, the repository silently queries the remote backend and updates local tables; Room then pushes fresh data to the UI automatically.
- **Zero crashes on disconnect:** If the server is unreachable or the user is on airplane mode, the app functions smoothly without blank screens or interruptions.

### 🏷️ On-Device Price & Discount Computation
The backend supplies raw catalog data (`price` and `discount_percent`). ArminShop handles the math on the client side:

$$\text{finalPrice} = \text{price} - \left(\frac{\text{price} \times \text{discountPercent}}{100}\right)$$

Every screen — from the home carousel to the cart summary — displays formatted Persian currency, strikethrough original prices, and discount badges consistently.

### ⭐ Honest Reviews & Client-Calculated Ratings
To keep product feedback authentic:
1. **No empty ratings:** Users cannot submit a star rating without writing a review.
2. **Instant average recalculation:** When a review is posted, the client computes the updated weighted average rating:
   $$R_{\text{new}} = \frac{(R_{\text{old}} \times N_{\text{old}}) + R_{\text{user}}}{N_{\text{old}} + 1}$$
3. The review and the newly calculated score are dispatched to the backend, while local Room tables update immediately so the user sees their feedback without waiting.

### 🔐 Hardware-Backed JWT Security
Authentication uses email-based OTP verification:
- One-time verification codes (6-digit) are validated against `/api/auth/verify-otp`.
- Received JWT tokens are encrypted with `AES256_GCM` using **Android KeyStore** through `EncryptedSharedPreferences`.
- An `AuthInterceptor` automatically injects the `Authorization: Bearer <TOKEN>` header into protected cart and review endpoints.

### 🎨 Smooth Visuals & Shimmer Loading
- Network images (Supabase CDN) load gracefully with **animated shimmer placeholders**.
- If offline, images automatically fall back to bundled category vectors, ensuring cards never look broken.
- Categorized home feed features:
  - 🔥 **Special Offers** banner
  - 👔 **Men's Collection**
  - 👗 **Women's Collection**
  - 🧸 **Kids' Wear**
  - 👟 **Footwear & Sneakers**

---

## 🏛️ Architecture Overview

The codebase adheres to **Clean Architecture** and **MVVM** principles, separating concerns into three decoupled layers:

```
app/src/main/java/ir/arminniromandi/arminshop/
│
├── data/                         # DATA LAYER
│   ├── local/                    # Room DB (Entities, DAOs) & SecureTokenManager
│   ├── remote/                   # Retrofit API interface, DTOs & AuthInterceptor
│   ├── mapper/                   # DTO ↔ Domain ↔ Entity mappers & discount math
│   └── repository/               # Repository implementations (SSOT orchestrators)
│
├── domain/                       # DOMAIN LAYER (Pure Kotlin)
│   ├── model/                    # Product, Category, User, and Comment models
│   ├── repository/               # Repository contracts/interfaces
│   └── usecase/                  # Email validation & OTP verification use cases
│
├── presentation/                 # PRESENTATION LAYER
│   ├── ui/screens/               # HomeScreen, ProductDetailScreen, LoginScreen, CartScreen
│   ├── components/               # ProductItemCard, ProductImage, ShimmerPlaceholder
│   ├── viewmodels/               # StateFlow-driven ViewModels
│   └── navigation/               # Nested tab graphs & shared element transitions
│
└── utils/                        # Utilities & Constants (BASE_URL, category constants)
```

---

## 🚀 Getting Started

### 1. Clone & Open
```bash
git clone https://github.com/your-username/arminShop.git
```
Open the project in **Android Studio Ladybug (or newer)** with JDK 21+ configured.

### 2. Configure Backend URL
Set your API endpoint inside [`Constance.kt`](file:///c:/Users/ARMINAPP/AndroidStudioProjects/arminShop/app/src/main/java/ir/arminniromandi/arminshop/utils/Constance.kt):

```kotlin
object Constance {
    // Android Emulator host loopback:
    const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Or point to your machine's local IP for physical devices:
    // const val BASE_URL = "http://192.168.1.50:8080/api/"
}
```

### 3. Run Automated Tests
Verify discount calculation, rating recalculation, and category mapping logic:
```bash
./gradlew testDebugUnitTest
```

### 4. Build Debug APK
```bash
./gradlew assembleDebug
```

---

## 📜 Verification & Status
- **Build Status:** ✅ `BUILD SUCCESSFUL`
- **Unit Tests:** ✅ All business logic tests passed (`BusinessLogicTest`)
- **Min SDK:** `24` (Android 7.0+)
- **Target SDK:** `37`

---

## 👤 Author
Developed with attention to detail by **Armin Niromandi**.
Contributions and feedback are always welcome!
