package ir.arminniromandi.arminshop.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.presentation.components.ProductItemCard
import ir.arminniromandi.arminshop.presentation.components.formatPersianPrice
import ir.arminniromandi.arminshop.presentation.viewmodels.SearchViewModel

private val ScreenBackground = Color(0xFFFFF6EE)
private val PrimaryOrangeRed = Color(0xFFFA4A23)
private val TextDarkColor = Color(0xFF1E1E1E)
private val TextGrayColor = Color(0xFF888888)

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onProductClick: (Product) -> Unit = {},
    onAddToCart: (Product) -> Unit = {}
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val products by viewModel.searchResults.collectAsStateWithLifecycle()

    SearchScreenContent(
        modifier = modifier,
        query = query,
        products = products,
        onQueryChange = viewModel::onQueryChange,
        onClearQuery = viewModel::clearQuery,
        onProductClick = onProductClick,
        onAddToCart = onAddToCart
    )
}

@Composable
fun SearchScreenContent(
    modifier: Modifier = Modifier,
    query: String,
    products: List<Product>,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input Field
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجو",
                        tint = PrimaryOrangeRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "نام محصول را وارد کنید ...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF9E9E9E))
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDarkColor),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    focusManager.clearFocus()
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = onClearQuery,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "پاک کردن",
                                tint = TextGrayColor
                            )
                        }
                    }
                }
            }

            // Title / Results count
            if (query.isNotBlank()) {
                Text(
                    text = "نتایج جستجو برای: «$query» (${formatPersianPrice(products.size.toLong())} محصول)",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextDarkColor),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "همه محصولات",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextDarkColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Products Grid
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هیچ محصولی با این مشخصات یافت نشد.",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextGrayColor),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onAddToCartClick = onAddToCart,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
