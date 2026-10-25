package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.repository.FavoriteRepository
import ir.arminniromandi.arminshop.domain.repository.ProductRepository
import ir.arminniromandi.arminshop.domain.repository.UserRepository
import ir.arminniromandi.arminshop.utils.ToastNotifier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val favoriteRepository: FavoriteRepository,
    private val toastNotifier: ToastNotifier,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Int = savedStateHandle.get<Int>("productId") ?: 1

    private val _productIdFlow = MutableStateFlow(productId)

    @OptIn(ExperimentalCoroutinesApi::class)
    val product: StateFlow<Product?> = _productIdFlow
        .flatMapLatest { id ->
            productRepository.getProductById(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite: StateFlow<Boolean> = _productIdFlow
        .flatMapLatest { id ->
            favoriteRepository.isFavorite(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun toggleFavorite() {
        viewModelScope.launch {
            val wasFav = isFavorite.value
            favoriteRepository.toggleFavorite(_productIdFlow.value)
            if (wasFav) {
                toastNotifier.showToast("از لیست علاقه‌مندی‌ها حذف شد")
            } else {
                toastNotifier.showSuccess("به لیست علاقه‌مندی‌ها اضافه شد")
            }
        }
    }

    val allProducts: StateFlow<List<Product>> = productRepository.getProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText.asStateFlow()

    private val _userRating = MutableStateFlow(5f)
    val userRating: StateFlow<Float> = _userRating.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun onCommentTextChanged(text: String) {
        _commentText.value = text
    }

    fun onUserRatingChanged(rating: Float) {
        _userRating.value = rating
    }

    fun submitComment(customUserName: String? = null) {
        val text = _commentText.value.trim()
        if (text.isBlank()) {
            val msg = "ثبت امتیاز تنها با نوشتن نظر امکان‌پذیر است. لطفاً نظر خود را بنویسید."
            toastNotifier.showError(msg)
            viewModelScope.launch {
                _userMessage.emit(msg)
            }
            return
        }

        val currentRating = _userRating.value
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        viewModelScope.launch {
            val userPrefs = userRepository.getUserData().firstOrNull()
            val resolvedName = customUserName
                ?: userPrefs?.fullName?.takeIf { it.isNotBlank() }
                ?: userPrefs?.email?.substringBefore("@")
                ?: "کاربر فروشگاه"

            val newComment = ProductComment(
                id = 0,
                userName = resolvedName,
                rating = currentRating,
                text = text,
                date = currentDate
            )
            try {
                productRepository.addComment(productId, newComment, isReply = false)
                _commentText.value = ""
                _userRating.value = 5f
                val msg = "نظر و امتیاز شما با موفقیت ثبت شد"
                toastNotifier.showSuccess(msg)
                _userMessage.emit(msg)
            } catch (e: Exception) {
                val error = e.message ?: "خطا در ثبت نظر"
                toastNotifier.showError(error)
                _userMessage.emit(error)
            }
        }
    }

    fun submitReply(replyText: String, customUserName: String? = null) {
        val text = replyText.trim()
        if (text.isBlank()) {
            toastNotifier.showError("لطفاً متن پاسخ را وارد کنید")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        viewModelScope.launch {
            val userPrefs = userRepository.getUserData().firstOrNull()
            val resolvedName = customUserName
                ?: userPrefs?.fullName?.takeIf { it.isNotBlank() }
                ?: userPrefs?.email?.substringBefore("@")
                ?: "کاربر فروشگاه"

            val newComment = ProductComment(
                id = 0,
                userName = resolvedName,
                rating = 0f,
                text = text,
                date = currentDate
            )
            try {
                productRepository.addComment(productId, newComment, isReply = true)
                val msg = "پاسخ شما با موفقیت ثبت شد"
                toastNotifier.showSuccess(msg)
                _userMessage.emit(msg)
            } catch (e: Exception) {
                val error = e.message ?: "خطا در ثبت پاسخ"
                toastNotifier.showError(error)
                _userMessage.emit(error)
            }
        }
    }
}
