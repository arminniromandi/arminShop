package ir.arminniromandi.arminshop.presentation.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.arminniromandi.arminshop.data.mock.MockProducts
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.presentation.components.BannerSlider
import ir.arminniromandi.arminshop.presentation.components.CategoryCard
import ir.arminniromandi.arminshop.presentation.components.ProductItemCard
import ir.arminniromandi.arminshop.presentation.components.formatPersianPrice
import ir.arminniromandi.arminshop.presentation.model.CategoryItem
import ir.arminniromandi.arminshop.presentation.ui.theme.CardBgSlate
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor
import ir.arminniromandi.arminshop.utils.Constance.categories



@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onSeeAllProductsClick: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    onAddToCart: (Product) -> Unit = {}
) {

    var searchQuery by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Banner Slider Component
            BannerSlider()

            // 2. Section Heading: "تنها با یک کلیک خرید کن!"
            Text(
                text = "تنها با یک کلیک خرید کن!",
                style = MaterialTheme.typography.titleLarge.copy(color = TextDarkColor),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Search Bar
            HomeSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = onSearch
            )

            // 4. Categories Section (Lazy Component)
            CategoriesLazyRow(
                categories = categories,
                onCategoryClick = onCategoryClick
            )

            // 5. Products Section ("پرفروش ترین ها")
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "پرفروش ترین ها",
                        style = MaterialTheme.typography.titleLarge.copy(color = TextDarkColor)
                    )

                    Row(
                        modifier = Modifier.clickable { onSeeAllProductsClick() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Text(
                            text = "مشاهده همه",
                            style = MaterialTheme.typography.labelLarge.copy(color = TextDarkColor)
                        )
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
                    items(MockProducts.sampleProducts) { product ->
                        ProductItemCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onAddToCartClick = onAddToCart
                        )
                    }
                }
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
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFF6EE)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
