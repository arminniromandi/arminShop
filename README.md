# 🛍️ فروشگاه اینترنتی آرمین (ArminShop)

یک اپلیکیشن فروشگاهی مدرن اندروید، پیاده‌سازی شده با **Jetpack Compose** و معماری **Offline-First** به همراه الگوی **Single Source of Truth (SSOT)** و ارتباط امن با Backend API.

---

## 🌟 ویژگی‌های کلیدی (Key Features)

### ۱. معماری آفلاین‌فرست (Offline-First Architecture)
- استفاده از **Room Database** به عنوان منبع واحد حقیقت (Single Source of Truth).
- لودینگ آنی داده‌ها از دیتابیس محلی بدون معطلی برای پاسخ شبکه.
- همگام‌سازی پس‌زمینه (Background Sync) با سرور بدون ایجاد بلاک یا فریز در UI.
- مقاومت کامل در برابر قطعی اینترنت و عدم کرش در شرایط آفلاین.

### ۲. محاسبات کلاینت‌ساید قیمت و تخفیف (Client-Side Pricing & Discounts)
- محاسبه خودکار قیمت نهایی پس از اعمال تخفیف در کلاینت بر اساس `price` و `discount_percent`:
  $$\text{finalPrice} = \text{price} - \left(\frac{\text{price} \times \text{discountPercent}}{100}\right)$$
- نمایش همزمان قیمت خط‌خورده، برچسب درصد تخفیف و قیمت نهایی به تومان با فرمت ارقام فارسی.

### ۳. سیستم هوشمند نظرات و امتیازدهی (Smart Rating & Reviews)
- **وابستگی امتیاز به نظر:** امکان ثبت امتیاز منحصراً همراه با متن نظر فعال است و کاربر نمی‌تواند بدون نوشتن دیدگاه، امتیاز خالی به سرور بفرستد.
- **محاسبه میانگین امتیاز در کلاینت:** به محض ثبت نظر جدید، میانگین کل امتیاز محصول در کلاینت باز-محاسبه شده و سپس به اندپوینت `/api/products/{id}/rating` ارسال می‌شود:
  $$R_{\text{new}} = \frac{(R_{\text{old}} \times N_{\text{old}}) + R_{\text{user}}}{N_{\text{old}} + 1}$$

### ۴. امنیت پیشرفته و احراز هویت با JWT (Secure Authentication)
- ورود امن دو مرحله‌ای بر پایه ارسال و بررسی کد یک‌بار مصرف ۶ رقمی (OTP) به ایمیل.
- ذخیره‌سازی توکن JWT در **EncryptedSharedPreferences** با کلید سخت‌افزاری **Android KeyStore** و رمزنگاری `AES256-GCM`.
- تزریق خودکار هدر `Authorization: Bearer <TOKEN>` به درخواست‌های نیازمند احراز هویت توسط `AuthInterceptor`.

### ۵. لودینگ شیمر و بارگذاری هوشمند تصاویر (Coil with Shimmer)
- نمایش انیمیشن اسکلتون **Shimmer** هنگام لودینگ اولیه اپلیکیشن و دانلود عکس‌ها.
- کش محلی تصاویر Supabase و سوییچ خودکار به آیکون‌های برداری آفلاین در صورت عدم اتصال به اینترنت.

### ۶. بازطراحی صفحه خانه (Categorized Home Screen)
- اسلایدر بنرهای تبلیغاتی و نوار جستجوی پیشرفته.
- بخش ویژه **پیشنهادهای شگفت‌انگیز (Special Offers)** با برچسب آتش 🔥.
- بخش‌های افقی مجزا برای دسته‌بندی‌ها:
  - 👔 پوشاک مردانه
  - 👗 پوشاک زنانه
  - 🧸 پوشاک بچه‌گانه
  - 👟 انواع کفش و کتانی
- دکمه «مشاهده همه» برای انتقال سریع به محصولات هر دسته‌بندی.

---

## 🛠️ تکنولوژی‌ها و کتابخانه‌ها (Tech Stack)

| بخش | ابزارها و کتابخانه‌ها |
| :--- | :--- |
| **زبان و سیستم بیلد** | Kotlin 2.1 • Gradle 9.6 • KSP |
| **رابط کاربری (UI)** | Jetpack Compose • Material 3 • Compose Navigation • Shared Transition |
| **معماری** | Clean Architecture • MVVM • Offline-First (SSOT) • Coroutines & Flow |
| **تزریق وابستگی (DI)** | Dagger Hilt 2.60 |
| **پایگاه داده محلی** | Room 2.6 • DataStore Preferences |
| **امنیت (Security)** | AndroidX Security Crypto (`EncryptedSharedPreferences` / MasterKey) |
| **شبکه (Networking)** | Retrofit 2.11 • OkHttp 4.12 • Gson • Logging Interceptor |
| **لودینگ تصاویر** | Coil Compose 2.7 • Shimmer Animation |
| **تست‌ها** | JUnit 4 • Kotlin Coroutines Test |

---

## 📁 ساختار پروژه (Project Architecture)

```
app/src/main/java/ir/arminniromandi/arminshop/
├── data/
│   ├── local/
│   │   ├── dao/                 # Room DAOs (ProductDao, CartDao, ...)
│   │   ├── entity/              # دیتابیس لوکال (ProductEntity, CartItemEntity, ...)
│   │   └── security/            # SecureTokenManager با EncryptedSharedPreferences
│   ├── mapper/                  # نگاشت DTOها به Domain و محاسبات تخفیف و ریتینگ
│   ├── mock/                    # داده‌های پیش‌فرض جهت کَش اولیه
│   ├── remote/
│   │   ├── dto/                 # مدل‌های داده‌ای JSON سرور (Auth, Products, Cart, ...)
│   │   ├── AuthInterceptor.kt   # تزریق خودکار توکن Bearer به هدرها
│   │   └── ShopApiService.kt    # رتروفیت اینترفیس API
│   └── repository/              # پیاده‌سازی مخزن‌ها با منطق Offline-First
├── di/                          # ماژول‌های Hilt (AppModule)
├── domain/
│   ├── model/                   # مدل‌های خالص دامنه (Product, User, Category)
│   ├── repository/              # اینترفیس‌های ریپازیتوری
│   └── usecase/                 # یوزکیس‌ها (اعتبارسنجی ایمیل و بررسی کد OTP)
├── presentation/
│   ├── animation/               # ترنزیشن‌ها و انیمیشن‌های Shared Element
│   ├── components/              # کامپوننت‌های عمومی (ProductItemCard, ProductImage, ...)
│   ├── navigation/              # مسیریابی و گراف‌های تودرتو تب‌ها
│   ├── ui/screens/              # اسکرین‌ها (HomeScreen, ProductDetailScreen, LoginScreen, ...)
│   ├── ui/theme/                # رنگ‌ها، تایپوگرافی و تم Material 3
│   └── viewmodels/              # ویومدل‌ها با StateFlow و Coroutines
└── utils/
    └── Constance.kt             # آدرس سرور (BASE_URL) و ثابت‌های برنامه
```

---

## ⚙️ نحوه راه‌اندازی و اجرا (Getting Started)

### ۱. تنظیم آدرس سرور (Base URL)
آدرس اتصال به سرور در فایل `Constance.kt` تعریف شده است:

```kotlin
// utils/Constance.kt
object Constance {
    // برای استفاده در شبیه‌ساز اندروید (Android Emulator):
    const val BASE_URL = "http://10.0.2.2:8080/api/"

    // برای استفاده روی گوشی واقعی در شبکه لوکال:
    // const val BASE_URL = "http://192.168.1.X:8080/api/"
}
```

### ۲. بیلد و اجرای تست‌ها (Build & Test)
برای اجرای تست‌های واحد منطق تجاری (محاسبات قیمت، تخفیف، میانگین امتیاز و دسته‌بندی‌ها):
```bash
./gradlew testDebugUnitTest
```

برای کامپایل و خروجی APK دیباگ:
```bash
./gradlew assembleDebug
```

---

## 🔒 امنیت و دسترسی‌ها (Permissions & Security)
- مجوز اینترنت در منیفست: `<uses-permission android:name="android.permission.INTERNET" />`
- پشتیبانی از پروتکل HTTP لوکال با: `android:usesCleartextTraffic="true"`
- ذخیره توکن‌ها با کلیدهای تحت حفاظت سخت‌افزاری Android KeyStore.

---

## 👨‍💻 توسعه‌دهنده (Developer)
- توسعه داده شده توسط **آرمین نیرومندی (Armin Niromandi)**
- طراحی و پیاده‌سازی شده با استانداردهای مدرن توسعه نرم‌افزار اندروید.
