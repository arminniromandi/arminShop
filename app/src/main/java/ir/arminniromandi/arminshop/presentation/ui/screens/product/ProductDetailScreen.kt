package ir.arminniromandi.arminshop.presentation.ui.screens.product

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.data.mock.MockProducts
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.presentation.animation.productSharedElement
import ir.arminniromandi.arminshop.presentation.components.ProductImage
import ir.arminniromandi.arminshop.presentation.components.ProductItemCard
import ir.arminniromandi.arminshop.presentation.components.formatPersianPrice
import ir.arminniromandi.arminshop.presentation.ui.theme.ArminShopTheme
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor
import ir.arminniromandi.arminshop.presentation.viewmodels.ProductDetailViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onProductClick: (Product) -> Unit = {},
    onAddToCart: (Product) -> Unit = {}
) {
    val product by viewModel.product.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val commentText by viewModel.commentText.collectAsStateWithLifecycle()
    val userRating by viewModel.userRating.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (product != null) {
        ProductDetailContent(
            modifier = modifier,
            product = product!!,
            similarProducts = allProducts.filter { it.id != product!!.id },
            commentText = commentText,
            userRating = userRating,
            isFavorite = isFavorite,
            onToggleFavorite = viewModel::toggleFavorite,
            onCommentChange = viewModel::onCommentTextChanged,
            onRatingChange = viewModel::onUserRatingChanged,
            onSubmitComment = { viewModel.submitComment() },
            onSubmitReply = { replyText -> viewModel.submitReply(replyText) },
            onProductClick = onProductClick,
            onAddToCart = onAddToCart
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "در حال بارگذاری محصول...",
                style = MaterialTheme.typography.bodyLarge.copy(color = TextDarkColor)
            )
        }
    }
}

@Composable
fun ProductDetailContent(
    modifier: Modifier = Modifier,
    product: Product,
    similarProducts: List<Product>,
    commentText: String,
    userRating: Float,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onCommentChange: (String) -> Unit,
    onRatingChange: (Float) -> Unit,
    onSubmitComment: () -> Unit,
    onSubmitReply: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    var selectedTab by remember { mutableStateOf("توضیحات") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val topOffsetPx = with(density) { 16.dp.toPx() }

    // Coordinates for each section to enable smooth scrolling
    var parentCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var descriptionCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var featuresCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var commentsCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var similarProductsCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // Reset tab to "توضیحات" when scrolling back to the top
    LaunchedEffect(scrollState.value) {
        if (scrollState.value <= 50 && selectedTab != "توضیحات") {
            selectedTab = "توضیحات"
        }
    }

    fun scrollToSection(targetCoords: LayoutCoordinates?) {
        val parent = parentCoordinates
        val target = targetCoords
        if (parent != null && target != null && parent.isAttached && target.isAttached) {
            val yInParent = parent.localPositionOf(target, Offset.Zero).y
            val targetScroll =
                (scrollState.value + yInParent - topOffsetPx).roundToInt().coerceAtLeast(0)
            coroutineScope.launch {
                scrollState.animateScrollTo(targetScroll)
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .verticalScroll(scrollState)
                .onGloballyPositioned { parentCoordinates = it }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 1. TOP MAIN WHITE CARD (Product Details Card)
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {


                    // 1.1 Product Image Container (Light grey background)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF1F1F5))
                    ) {
                        // Discount Badge (Top Right)
                        if (product.discountPrec > 0) {

                            Surface(
                                modifier = Modifier
                                    .padding(top = 14.dp),
                                shape = RoundedCornerShape(
                                    topEndPercent = 100,
                                    bottomEndPercent = 100
                                ),
                                color = PrimaryOrangeRed
                            ) {
                                Text(
                                    text = "${formatPersianPrice(product.discountPrec.toLong())}٪",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )

                            }
                        }


                        // Product Image in Center
                        ProductImage(
                            imageUrl = product.imageUrl,
                            fallbackRes = product.imageRes,
                            contentDescription = product.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .productSharedElement("product-image-${product.id}"),
                            contentScale = ContentScale.Fit
                        )
                    }



                    Column(

                        modifier = modifier
                            .background(Color.White)
                            .offset(y = (-18).dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(14.dp)

                    ) {


                        // 1.2 Action Pill (Share, Rating from DB, Bookmark, Comments count with badge from DB)

                        Row(
                            modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 25.dp))
                                .background(Color.White)
                                .padding(horizontal = 18.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Share Icon
                            Icon(
                                painter = painterResource(R.drawable.share),
                                contentDescription = "Share",
                                tint = Color(0xFF616161),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        val shareText = buildString {
                                            appendLine(product.name)
                                            if (product.discountPrec > 0) {
                                                appendLine(
                                                    "قیمت: ${formatPersianPrice(product.finalPrice)} تومان (${
                                                        formatPersianPrice(
                                                            product.discountPrec.toLong()
                                                        )
                                                    }٪ تخفیف)"
                                                )
                                            } else {
                                                appendLine("قیمت: ${formatPersianPrice(product.mainPrice)} تومان")
                                            }
                                            if (product.description.isNotBlank()) {
                                                appendLine()
                                                appendLine(product.description)
                                            }
                                        }
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            putExtra(Intent.EXTRA_TITLE, product.name)
                                            type = "text/plain"
                                        }
                                        val shareIntent =
                                            Intent.createChooser(sendIntent, "اشتراک‌گذاری محصول")
                                        context.startActivity(shareIntent)
                                    }
                            )

                            // Rating: Gold Star + Persian rating from Database
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFFB800),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = formatPersianNumber("%.1f".format(product.rating)),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkColor,
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            // Bookmark Icon
                            Icon(
                                painter = if (isFavorite) painterResource(R.drawable.save_fil) else painterResource(R.drawable.save),
                                contentDescription = "Bookmark",
                                tint = if (isFavorite) PrimaryOrangeRed else Color(0xFF616161),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onToggleFavorite() }
                            )

                            // Comment bubble with small red badge (click scrolls to comments section)
                            Box(
                                contentAlignment = Alignment.BottomStart,
                                modifier = Modifier.clickable {
                                    selectedTab = "نظرات"
                                    scrollToSection(commentsCoordinates)
                                }
                            ) {
                                Icon(
                                    painter=painterResource(R.drawable.messages),
                                    contentDescription = "Comments",
                                    tint = Color(0xFF616161),
                                    modifier = Modifier.size(22.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryOrangeRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = formatPersianNumber(product.comments.size.toString()),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }


                        // 1.4 Tabs / Filter Pills with Animated Scroll to Section
                        val tabs = listOf("توضیحات", "ویژگی ها", "نظرات", "محصولات مشابه")
                        val tabsListState = rememberLazyListState()

                        LaunchedEffect(selectedTab) {
                            val index = tabs.indexOf(selectedTab)
                            if (index >= 0) {
                                tabsListState.animateScrollToItem(index)
                            }
                        }

                        LazyRow(
                            state = tabsListState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tabs) { tab ->
                                val isSelected = tab == selectedTab
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            selectedTab = tab
                                            when (tab) {
                                                "توضیحات" -> scrollToSection(descriptionCoordinates)
                                                "ویژگی ها" -> scrollToSection(featuresCoordinates)
                                                "نظرات" -> scrollToSection(commentsCoordinates)
                                                "محصولات مشابه" -> scrollToSection(
                                                    similarProductsCoordinates
                                                )
                                            }
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) Color.White else Color(0xFFF7F7F8),
                                    border = if (isSelected) BorderStroke(
                                        1.dp,
                                        PrimaryOrangeRed
                                    ) else null
                                ) {
                                    Text(
                                        text = tab,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isSelected) PrimaryOrangeRed else Color(
                                                0xFF757575
                                            ),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        ),
                                        modifier = Modifier.padding(
                                            horizontal = 14.dp,
                                            vertical = 7.dp
                                        )
                                    )
                                }
                            }
                        }

                        // 1.5 Section: "توضیحات" (Description)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { descriptionCoordinates = it },
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1.3 Product Title
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkColor,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .productSharedElement("product-title-${product.id}")
                            )
                            Text(
                                text = "توضیحات",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkColor,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            val descriptionText = product.description.ifBlank {
                                "** با توجه به تفاوت رنگ‌ها در صفحه نمایش دستگاه‌های مختلف، ممکن است رنگ محصولات در تصویر تا ۲۰٪ با واقعیت متفاوت باشد ** دارای ۵ رنگ جذاب، تمام نخ پنبه، پارچه پیراهن کشمیر، دم دست و پایین بلوز کشباف، سایز بندی M تا 4XL\n\n** با توجه به تفاوت رنگ‌ها در صفحه نمایش دستگاه‌های مختلف، ممکن است رنگ محصولات در تصویر تا ۲۰٪ با واقعیت متفاوت باشد ** دارای ۵ رنگ جذاب، تمام نخ پنبه، پارچه پیراهن کشمیر، دم دست و پایین بلوز کشباف، سایز بندی M تا 4XL"
                            }

                            Text(
                                text = descriptionText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF424242),
                                    lineHeight = 21.sp,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        // 1.6 Section: "ویژگی ها" (Features)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned {
                                    featuresCoordinates = it
                                }
                                .padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ویژگی ها",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkColor,
                                    fontSize = 14.sp
                                )
                            )

                            val featuresList = listOf(
                                "وزن خالص: ${product.features.weight.ifBlank { "۳۴۰ گرم" }}",
                                "رنگ: ${product.features.color.ifBlank { "متنوع" }}",
                                "وزن خالص: ${product.features.weight.ifBlank { "۳۴۰ گرم" }}",
                                "وزن با بسته‌بندی: ۴۰۰ گرم"
                            )

                            featuresList.forEach { feature ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF7F7F8)
                                ) {
                                    Text(
                                        text = feature,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF424242),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(
                                            horizontal = 14.dp,
                                            vertical = 10.dp
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(modifier.height(8.dp))


                    }


                }
            }

            // ==========================================
            // 2. SUBMIT COMMENT SECTION ("ثبت نظر")
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ثبت نظر",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor,
                        fontSize = 15.sp
                    )
                )

                // White Comment Input Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (commentText.isEmpty()) {
                            Text(
                                text = "نظر خود را وارد کنید",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF9E9E9E),
                                    fontSize = 13.sp
                                )
                            )
                        }
                        BasicTextField(
                            value = commentText,
                            onValueChange = onCommentChange,
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkColor,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Row: Submit Button on Left, Rating Stars on Right (RTL context)
                val isSubmitEnabled = commentText.isNotBlank()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Submit Button with Rating validation
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = isSubmitEnabled) { onSubmitComment() },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSubmitEnabled) PrimaryOrangeRed else Color(0xFFBDBDBD)
                    ) {
                        Text(
                            text = "ثبت نظر و امتیاز",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }

                    // Interactive Rating Stars (1 to 5)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "امتیاز دهید",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkColor,
                                fontSize = 12.sp
                            )
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..5) {
                                val starValue = i.toFloat()
                                Icon(
                                    imageVector = if (userRating >= starValue) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Star $i",
                                    tint = if (userRating >= starValue) Color(0xFFFFB800) else Color(
                                        0xFFBDBDBD
                                    ),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onRatingChange(starValue) }
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 3. USER REVIEWS SECTION ("نظرات کاربران" from DB)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { commentsCoordinates = it },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "نظرات کاربران (${formatPersianNumber(product.comments.size.toString())})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor,
                            fontSize = 15.sp
                        )
                    )

                    // Average rating summary
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Average rating",
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = formatPersianNumber("%.1f".format(product.rating)),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkColor,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                if (product.comments.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هنوز نظری برای این محصول ثبت نشده است. اولین نظر را شما ثبت کنید!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextGrayColor,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                } else {
                    // Display all comments dynamically fetched from Room Database
                    product.comments.forEach { comment ->
                        DatabaseCommentCard(
                            comment = comment,
                            onSendReply = { replyText -> onSubmitReply(replyText) }
                        )
                    }
                }
            }

            // ==========================================
            // 4. SIMILAR PRODUCTS SECTION ("محصولات مشابه")
            // ==========================================
            if (similarProducts.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { similarProductsCoordinates = it },
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "محصولات مشابه",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor,
                            fontSize = 15.sp
                        )
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(similarProducts, key = { it.id }) { similarProduct ->
                            ProductItemCard(
                                product = similarProduct,
                                onClick = { onProductClick(similarProduct) },
                                onAddToCartClick = onAddToCart
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// -------------------------------------------------------------
// Component: Database-Backed Comment Card matching Design
// -------------------------------------------------------------
@Composable
private fun DatabaseCommentCard(
    comment: ProductComment,
    onSendReply: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var replyText by remember { mutableStateOf("") }
    var isReplying by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User Header Row (User Name on Right, Star Rating + Date on Left)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor,
                        fontSize = 13.sp
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (comment.date.isNotBlank()) {
                        Text(
                            text = comment.date,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF9E9E9E),
                                fontSize = 10.sp
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star",
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = formatPersianNumber("%.1f".format(comment.rating)),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDarkColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Comment text
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF424242),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            )

            // Reply input box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF7F7F8)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (replyText.isEmpty()) {
                        Text(
                            text = "پاسخ خود را وارد کنید",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF9E9E9E),
                                fontSize = 11.sp
                            )
                        )
                    }
                    BasicTextField(
                        value = replyText,
                        onValueChange = {
                            replyText = it
                            isReplying = it.isNotBlank()
                        },
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            color = TextDarkColor,
                            fontSize = 11.sp
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Show "ارسال پاسخ" button if user starts typing in reply box
            if (isReplying) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (replyText.isNotBlank()) {
                                onSendReply(replyText)
                                replyText = ""
                                isReplying = false
                            }
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, PrimaryOrangeRed)
                ) {
                    Text(
                        text = "ارسال پاسخ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryOrangeRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Helper: Format Persian Digits
// -------------------------------------------------------------
private fun formatPersianNumber(numberStr: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return numberStr.map { char ->
        if (char in '0'..'9') persianDigits[char - '0'] else char
    }.joinToString("")
}

@Preview(showBackground = true, locale = "fa")
@Composable
private fun ProductDetailContentPreview() {
    ArminShopTheme {
        ProductDetailContent(
            product = MockProducts.sampleProducts.first(),
            similarProducts = MockProducts.sampleProducts.drop(1),
            commentText = "",
            userRating = 4f,
            isFavorite = false,
            onToggleFavorite = {},
            onCommentChange = {},
            onRatingChange = {},
            onSubmitComment = {},
            onSubmitReply = {},
            onProductClick = {},
            onAddToCart = {}
        )
    }
}
