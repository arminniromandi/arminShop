package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.domain.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun onCommentTextChanged(text: String) {
        _commentText.value = text
    }

    fun onUserRatingChanged(rating: Float) {
        _userRating.value = rating
    }

    fun submitComment(userName: String = "کاربر محترم") {
        val text = _commentText.value.trim()
        if (text.isBlank()) return

        val currentRating = _userRating.value
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        viewModelScope.launch {
            val newComment = ProductComment(
                id = 0,
                userName = userName,
                rating = currentRating,
                text = text,
                date = currentDate
            )
            productRepository.addComment(productId, newComment)

            // Reset input
            _commentText.value = ""
            _userRating.value = 5f
        }
    }
}
