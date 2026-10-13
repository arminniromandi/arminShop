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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.data.mock.MockProducts
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.presentation.components.BannerSlider
import ir.arminniromandi.arminshop.presentation.ui.theme.IranSansFontFamily

private val ScreenBackground = Color(0xFFFFF6EE)
private val PrimaryOrangeRed = Color(0xFFFA4A23)
private val TextDarkColor = Color(0xFF1E1E1E)
private val TextGrayColor = Color(0xFF888888)
private val CardBgSlate = Color(0xFFE5E9EE)

data class CategoryItem(
    val id: String,
    val name: String,
    val iconRes: Int
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onSeeAllProductsClick: () -> Unit = {},
    onSearch: (String) -> Unit = {}
) {
    val categories = listOf(
        CategoryItem("1", "زنانه", R.drawable.woman_category),
        CategoryItem("2", "مردانه", R.drawable.man_category),
        CategoryItem("3", "کفش", R.drawable.shoes_category),
        CategoryItem("4", "بچهگانه", R.drawable.baby_category)
    )

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
                style = TextStyle(
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextDarkColor
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
                        style = TextStyle(
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextDarkColor
                        )
                    )

                    Row(
                        modifier = Modifier.clickable { onSeeAllProductsClick() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Text(
                            text = "مشاهده همه",
                            style = TextStyle(
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextDarkColor
                            )

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
                            onClick = { onProductClick(product) }
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
                        style = TextStyle(
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(
                        fontFamily = IranSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = TextDarkColor
                    ),
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
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(80.dp)
            .height(96.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = category.iconRes),
                contentDescription = category.name,
                modifier = Modifier.size(42.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = category.name,
                style = TextStyle(
                    fontFamily = IranSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextDarkColor
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ProductItemCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(195.dp)
            .height(230.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(CardBgSlate)
            .clickable { onClick() }
    ) {

        if (product.discountPrec > 0) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                    color = PrimaryOrangeRed
                ) {
                    Text(
                        text = "${formatPersianPrice(product.discountPrec.toLong())}٪",
                        style = TextStyle(
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }


        // Top Section: Product Image & Discount Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {




            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            // Top-Right Red Discount Badge

        }

        // Bottom White Card Container with Red Add Button overlapping its left edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {
            // Inner White Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp, end = 10.dp, start = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = product.name,
                        style = TextStyle(
                            fontFamily = IranSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextDarkColor
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                            Text(
                                text = formatPersianPrice(product.mainPrice),
                                style = TextStyle(
                                    fontFamily = IranSansFontFamily,
                                    fontWeight = if (product.discountPrec != 0) FontWeight.Normal else FontWeight.Bold,
                                    fontSize = if (product.discountPrec != 0) 10.sp else 12.sp,
                                    color = if (product.discountPrec != 0)  TextGrayColor else TextDarkColor,
                                    textDecoration = if (product.discountPrec != 0) TextDecoration.Underline else TextDecoration.None
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))


                        if (product.discountPrec != 0)
                            Text(
                            text = formatPersianPrice(product.finalPrice),
                            style = TextStyle(
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextDarkColor
                            )
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "تومان",
                            style = TextStyle(
                                fontFamily = IranSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp,
                                color = TextDarkColor
                            )
                        )
                    }
                }
            }

            // Circular Red Floating Button explicitly centered on the LEFT edge of the white box
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (-12).dp)
                        .size(36.dp)
                        .clickable { /* Add to cart */ },
                    shape = CircleShape,
                    color = PrimaryOrangeRed,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatPersianPrice(price: Long): String {
    val formatted = "%,d".format(price)
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return formatted.map { char ->
        if (char in '0'..'9') persianDigits[char - '0'] else char
    }.joinToString("")
}

@Preview(showBackground = true, backgroundColor = 0xFFFFF6EE)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
