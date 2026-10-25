package ir.arminniromandi.arminshop.data.remote

import ir.arminniromandi.arminshop.utils.ToastNotifier
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Interceptor that observes HTTP response codes and surfaces server errors (5xx, 4xx)
 * as user-facing Persian Toasts.
 */
@Singleton
class ServerErrorInterceptor @Inject constructor(
    private val toastNotifier: ToastNotifier
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            // Network connection error (e.g. server down, no wifi)
            // Note: We don't spam toasts for background pre-fetches unless needed,
            // but we can log or notify if it was an active request
            throw e
        }

        // Intercept HTTP errors (500-599 server errors and 401/403/404)
        if (!response.isSuccessful) {
            val code = response.code
            val rawBody = try {
                response.peekBody(2048).string()
            } catch (_: Exception) {
                null
            }

            val parsedMessage = parseErrorMessage(rawBody)

            when (code) {
                in 500..599 -> {
                    toastNotifier.showServerError(
                        code = code,
                        rawError = parsedMessage ?: "خطایی در سرور رخ داد. لطفاً بعداً تلاش کنید."
                    )
                }
                401 -> {
                    toastNotifier.showServerError(
                        code = code,
                        rawError = "اعتبار نشست شما به پایان رسیده است. لطفاً مجدداً وارد شوید."
                    )
                }
                403 -> {
                    toastNotifier.showServerError(
                        code = code,
                        rawError = "شما اجازه دسترسی به این بخش را ندارید."
                    )
                }
                400, 422 -> {
                    if (!parsedMessage.isNullOrBlank()) {
                        toastNotifier.showError("خطای درخواست: $parsedMessage")
                    }
                }
            }
        }

        return response
    }

    private fun parseErrorMessage(body: String?): String? {
        if (body.isNullOrBlank()) return null
        return try {
            val json = JSONObject(body)
            when {
                json.has("message") -> json.getString("message")
                json.has("error") -> json.getString("error")
                json.has("detail") -> json.getString("detail")
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}
