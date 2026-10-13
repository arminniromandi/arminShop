package ir.arminniromandi.arminshop.data.mock

import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductCategory
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.model.ProductFeatures

object MockProducts {

    val sampleProducts: List<Product> = listOf(
        Product(
            id = 1,
            imageRes = R.drawable.malecloth,
            name = "پیراهن مردانه اسپرت",
            description = "پیراهن مردانه شیک و با کیفیت با پارچه کتان اعلا، مناسب برای استایل‌های نیمه‌رسمی و روزمره.",
            features = ProductFeatures(
                weight = "۲۵۰ گرم",
                color = "سرمه‌ای"
            ),
            mainPrice = 580000L,
            finalPrice = 493000L,
            discountPrec = 15,
            rating = 4.7f,
            reviewsCount = 24,
            comments = listOf(
                ProductComment(
                    id = 1,
                    userName = "علی محمدی",
                    rating = 5.0f,
                    text = "جنس پارچه عالی بود و تن‌خور بسیار شیکی داره. پیشنهاد می‌کنم.",
                    date = "۱۴۰۲/۱۱/۲۰"
                ),
                ProductComment(
                    id = 2,
                    userName = "رضا حسینی",
                    rating = 4.5f,
                    text = "کیفیت میداشت، سایزش هم دقیقاً طبق جدول سایزبندی بود.",
                    date = "۱۴۰۲/۱۲/۰۲"
                )
            ),
            category = ProductCategory.MEN
        ),
        Product(
            id = 2,
            imageRes = R.drawable.shirtmale,
            name = "پیراهن مردانه کژوال",
            description = "پیراهن مردانه با دوخت تمیز و پارچه خنک و تنفس‌پذیر، مناسب فصل بهار و تابستان.",
            features = ProductFeatures(
                weight = "۲۲۰ گرم",
                color = "آبی روشن"
            ),
            mainPrice = 450000L,
            finalPrice = 405000L,
            discountPrec = 10,
            rating = 4.5f,
            reviewsCount = 12,
            comments = listOf(
                ProductComment(
                    id = 3,
                    userName = "محمد کاظمی",
                    rating = 4.5f,
                    text = "پارچه خنک و سبکی داره، عالی برای روزهای گرم.",
                    date = "۱۴۰۲/۱۰/۱۵"
                )
            ),
            category = ProductCategory.MEN
        ),
        Product(
            id = 3,
            imageRes = R.drawable.malecloth2,
            name = "کاپشن و پوشاک زمستانه مردانه",
            description = "لباس زمستانه مردانه گرم با پارچه مقاوم در برابر سرما و بافت باکیفیت.",
            features = ProductFeatures(
                weight = "۶۵۰ گرم",
                color = "خاکستری"
            ),
            mainPrice = 1850000L,
            finalPrice = 1480000L,
            discountPrec = 20,
            rating = 4.8f,
            reviewsCount = 35,
            comments = listOf(
                ProductComment(
                    id = 4,
                    userName = "امیرحسین رضایی",
                    rating = 5.0f,
                    text = "فوق‌العاده گرم و باکیفیت. دوخت محکم و عالی داره.",
                    date = "۱۴۰۲/۰۹/۱۰"
                ),
                ProductComment(
                    id = 5,
                    userName = "مهدی نوری",
                    rating = 4.6f,
                    text = "خیلی خوبه ولی یکم سنگینه.",
                    date = "۱۴۰۲/۰۹/۱۸"
                )
            ),
            category = ProductCategory.MEN
        ),
        Product(
            id = 4,
            imageRes = R.drawable.maletshirt,
            name = "تی‌شرت مردانه پنبه‌ای",
            description = "تی‌شرت مردانه از جنس ۱۰۰٪ پنبه، بسیار خنک و راحت برای استفاده روزمره و ورزشی.",
            features = ProductFeatures(
                weight = "۱۸۰ گرم",
                color = "مشکی"
            ),
            mainPrice = 290000L,
            finalPrice = 290000L,
            discountPrec = 0,
            rating = 4.3f,
            reviewsCount = 8,
            comments = listOf(
                ProductComment(
                    id = 6,
                    userName = "سارین احمدی",
                    rating = 4.3f,
                    text = "تیشرت خوب و ساده‌ای هست، رنگش هم بعد از شستشو نرفته.",
                    date = "۱۴۰۲/۱۲/۱۰"
                )
            ),
            category = ProductCategory.MEN
        ),
        Product(
            id = 5,
            imageRes = R.drawable.womanshirt,
            name = "شومیز زنانه مجلسی",
            description = "شومیز مجلسی زنانه با پارچه حریر تنفس‌پذیر و طراحی مدرن مناسب مهمانی‌ها و دورهمی‌ها.",
            features = ProductFeatures(
                weight = "۲۰۰ گرم",
                color = "سفید"
            ),
            mainPrice = 620000L,
            finalPrice = 545600L,
            discountPrec = 12,
            rating = 4.6f,
            reviewsCount = 19,
            comments = listOf(
                ProductComment(
                    id = 7,
                    userName = "مریم عباسی",
                    rating = 5.0f,
                    text = "بسیار شیک و خوش‌دوخت، روی تن عالی وایمیسته.",
                    date = "۱۴۰۲/۱۱/۰۵"
                )
            ),
            category = ProductCategory.WOMEN
        ),
        Product(
            id = 6,
            imageRes = R.drawable.womenshoes,
            name = "کفش اسپرت زنانه",
            description = "کفش ورزشی و پیاده‌روی زنانه بسیار سبک با کفی طبی و لایه ضد لغزش.",
            features = ProductFeatures(
                weight = "۳۵۰ گرم",
                color = "سفید - صورتی"
            ),
            mainPrice = 1200000L,
            finalPrice = 900000L,
            discountPrec = 25,
            rating = 4.9f,
            reviewsCount = 42,
            comments = listOf(
                ProductComment(
                    id = 8,
                    userName = "زهرا مرادی",
                    rating = 5.0f,
                    text = "خیلی راحت و سبک، برای پیاده‌روی طولانی عالیه.",
                    date = "۱۴۰۲/۱۰/۲۸"
                )
            ),
            category = ProductCategory.SHOES
        ),
        Product(
            id = 7,
            imageRes = R.drawable.bluemancloth,
            name = "کت و ست مردانه آبی",
            description = "ست پوشاک مردانه شیک به رنگ آبی با برش استاندارد و پارچه مرغوب.",
            features = ProductFeatures(
                weight = "۵۰۰ گرم",
                color = "آبی کاربنی"
            ),
            mainPrice = 2400000L,
            finalPrice = 1968000L,
            discountPrec = 18,
            rating = 4.4f,
            reviewsCount = 15,
            comments = listOf(
                ProductComment(
                    id = 9,
                    userName = "کامران شریفی",
                    rating = 4.4f,
                    text = "کت شیکیه، تن‌خور خوبی داره و جنسش هم باکیفیته.",
                    date = "۱۴۰۲/۱۱/۱۲"
                )
            ),
            category = ProductCategory.MEN
        ),
        Product(
            id = 8,
            imageRes = R.drawable.maleblozandshalvar,
            name = "ست بلوز و شلوار راحتی مردانه",
            description = "ست بلوز و شلوار راحتی مردانه مناسب منزل و فعالیت‌های ورزشی سبک.",
            features = ProductFeatures(
                weight = "۴۲۰ گرم",
                color = "طوسی"
            ),
            mainPrice = 520000L,
            finalPrice = 468000L,
            discountPrec = 10,
            rating = 4.2f,
            reviewsCount = 11,
            comments = listOf(
                ProductComment(
                    id = 10,
                    userName = "حسین کریمی",
                    rating = 4.2f,
                    text = "ست راحتی و خوبیه، جنس نرم و لطیفی داره.",
                    date = "۱۴۰۲/۱۲/۰۱"
                )
            ),
            category = ProductCategory.MEN
        )
    )
}
