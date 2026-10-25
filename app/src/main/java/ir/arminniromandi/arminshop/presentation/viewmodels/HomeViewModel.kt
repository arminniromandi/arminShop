package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductCategory
import ir.arminniromandi.arminshop.domain.repository.ProductRepository
import ir.arminniromandi.arminshop.utils.ToastNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val specialOffers: List<Product> = emptyList(),
    val menProducts: List<Product> = emptyList(),
    val womenProducts: List<Product> = emptyList(),
    val kidsProducts: List<Product> = emptyList(),
    val shoesProducts: List<Product> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val toastNotifier: ToastNotifier
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        productRepository.getSpecialOffers(),
        productRepository.getProductsByCategory("مردانه"),
        productRepository.getProductsByCategory("زنانه"),
        productRepository.getProductsByCategory("بچه گانه"),
        productRepository.getProductsByCategory("کفش")
    ) { special, men, women, kids, shoes ->
        val hasData = special.isNotEmpty() || men.isNotEmpty() || women.isNotEmpty() || kids.isNotEmpty() || shoes.isNotEmpty()
        HomeUiState(
            isLoading = !hasData,
            specialOffers = special,
            menProducts = men,
            womenProducts = women,
            kidsProducts = kids,
            shoesProducts = shoes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                productRepository.refreshAll()
                toastNotifier.showSuccess("اطلاعات فروشگاه با موفقیت به‌روزرسانی شد")
            } catch (e: Exception) {
                toastNotifier.showError(e.message ?: "خطا در برقراری ارتباط با سرور جهت بروزرسانی")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
