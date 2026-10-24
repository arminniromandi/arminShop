package ir.arminniromandi.arminshop.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.presentation.components.BannerSlider
import ir.arminniromandi.arminshop.presentation.components.CategoryCard
import ir.arminniromandi.arminshop.presentation.components.ImageShimmerPlaceholder
import ir.arminniromandi.arminshop.presentation.components.ProductItemCard
import ir.arminniromandi.arminshop.presentation.model.CategoryItem
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor
import ir.arminniromandi.arminshop.presentation.viewmodels.HomeViewModel
import ir.arminniromandi.arminshop.utils.Constance.categories

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (Product) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onSeeAllProductsClick: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    onAddToCart: (Product) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Banner Slider Component
            BannerSlider()

            // 2. Section Heading
            Text(
                text = "تنها با یک کلیک خرید کن!",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextDarkColor,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Search Bar
            HomeSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = onSearch
            )

            // 4. Categories Section
            CategoriesLazyRow(
                categories = categories,
                onCategoryClick = onCategoryClick
            )

            // Initial Loading skeleton or actual sections
            if (uiState.isLoading) {
                HomeLoadingSection()
            } else {
                // 5. Special Offers Section (پیشنهادهای شگفت‌انگیز)
                if (uiState.specialOffers.isNotEmpty()) {
                    ProductSection(
                        title = "پیشنهادهای شگفت‌انگیز",
                        products = uiState.specialOffers,
                        badgeIcon = {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Special Offers",
                                tint = PrimaryOrangeRed,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onSeeAllClick = { onSeeAllProductsClick() },
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart
                    )
                }

                // 6. Men's Section (مردانه)
                if (uiState.menProducts.isNotEmpty()) {
                    ProductSection(
                        title = "پوشاک مردانه",
                        products = uiState.menProducts,
                        onSeeAllClick = { onCategoryClick("مردانه") },
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart
                    )
                }

                // 7. Women's Section (زنانه)
                if (uiState.womenProducts.isNotEmpty()) {
                    ProductSection(
                        title = "پوشاک زنانه",
                        products = uiState.womenProducts,
                        onSeeAllClick = { onCategoryClick("زنانه") },
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart
                    )
                }

                // 8. Kids Section (بچه‌گانه)
                if (uiState.kidsProducts.isNotEmpty()) {
                    ProductSection(
                        title = "پوشاک بچه‌گانه",
                        products = uiState.kidsProducts,
                        onSeeAllClick = { onCategoryClick("بچه گانه") },
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart
                    )
                }

                // 9. Shoes Section (کفش)
                if (uiState.shoesProducts.isNotEmpty()) {
                    ProductSection(
                        title = "انواع کفش و کتانی",
                        products = uiState.shoesProducts,
                        onSeeAllClick = { onCategoryClick("کفش") },
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProductSection(
    title: String,
    products: List<Product>,
    onSeeAllClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier,
    badgeIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                badgeIcon?.invoke()
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextDarkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSeeAllClick() }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "مشاهده همه",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PrimaryOrangeRed,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "See All",
                    tint = PrimaryOrangeRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Products Lazy Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(products, key = { it.id }) { product ->
                ProductItemCard(
                    product = product,
                    onClick = { onProductClick(product) },
                    onAddToCartClick = onAddToCart
                )
            }
        }
    }
}

@Composable
fun HomeLoadingSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(
            color = PrimaryOrangeRed,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = "در حال دریافت و بروزرسانی محصولات...",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextGrayColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(220.dp)
            ) {
                ImageShimmerPlaceholder()
            }
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(220.dp)
            ) {
                ImageShimmerPlaceholder()
            }
        }
    }
}

@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = PrimaryOrangeRed,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        if (query.isNotBlank()) {
                            focusManager.clearFocus()
                            onSearch(query)
                        }
                    }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "هرچی میخوای جستجو کن ...",
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
                            if (query.isNotBlank()) {
                                focusManager.clearFocus()
                                onSearch(query)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CategoriesLazyRow(
    categories: List<CategoryItem>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.name) }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EE)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
