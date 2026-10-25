package ir.arminniromandi.arminshop.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized, thread-safe Toast and Error notification utility for the entire application.
 * Can be injected into ViewModels, Repositories, and OkHttp Interceptors.
 */
@Singleton
class ToastNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastShownMessage: String? = null
    private var lastShownTime: Long = 0L

    /**
     * Displays a native Android Toast on the Main Thread with duplicate debouncing.
     */
    fun showToast(message: String, isLong: Boolean = false) {
        val trimmed = message.trim()
        if (trimmed.isBlank()) return

        val currentTime = System.currentTimeMillis()
        // Prevent spamming identical toasts within 2.5 seconds
        if (trimmed == lastShownMessage && (currentTime - lastShownTime) < 2500) {
            return
        }
        lastShownMessage = trimmed
        lastShownTime = currentTime

        mainHandler.post {
            Toast.makeText(
                context,
                trimmed,
                if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Helper for server-side HTTP errors (5xx, 4xx).
     */
    fun showServerError(code: Int? = null, section: String? = null, rawError: String? = null) {
        val detail = when {
            !rawError.isNullOrBlank() && !rawError.contains("<!doctype", ignoreCase = true) && !rawError.contains("<html", ignoreCase = true) -> rawError
            code != null -> when (code) {
                400 -> "درخواست نامعتبر است (کد ۴۰۰)"
                401 -> "نشست کاربری شما منقضی شده است (کد ۴۰۱)"
                403 -> "دسترسی غیرمجاز (کد ۴۰۳)"
                404 -> "اطلاعات در سرور یافت نشد (کد ۴۰۴)"
                429 -> "درخواست‌های مکرر. لطفاً کمی بعد تلاش کنید (کد ۴۲۹)"
                500 -> "خطای داخلی سرور (کد ۵۰۰)"
                502 -> "خطای درگاه سرور (کد ۵۰۲)"
                503 -> "سرور موقتاً در دسترس نیست (کد ۵۰۳)"
                else -> "خطای سرور (کد $code)"
            }
            else -> "مشکلی در سرور رخ داده است"
        }

        val message = if (!section.isNullOrBlank()) {
            "خطای سرور در $section: $detail"
        } else {
            "خطای سرور: $detail"
        }
        showToast(message)
    }

    /**
     * Helper for network connection drops or server timeouts.
     */
    fun showNetworkError(section: String? = null) {
        val message = if (!section.isNullOrBlank()) {
            "خطا در برقراری ارتباط با سرور در $section. لطفاً اتصال اینترنت خود را بررسی کنید."
        } else {
            "خطا در برقراری ارتباط با سرور. لطفاً اتصال اینترنت خود را بررسی کنید."
        }
        showToast(message)
    }

    fun showError(message: String) {
        showToast(message)
    }

    fun showSuccess(message: String) {
        showToast(message)
    }
}
