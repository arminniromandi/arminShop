package ir.arminniromandi.arminshop.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.domain.model.ProductComment
import ir.arminniromandi.arminshop.presentation.components.ProductItemCard
import ir.arminniromandi.arminshop.presentation.components.formatPersianPrice
import ir.arminniromandi.arminshop.presentation.ui.theme.CardBgSlate
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor
import ir.arminniromandi.arminshop.presentation.viewmodels.ProductDetailViewModel

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

    if (product != null) {
        ProductDetailContent(
            modifier = modifier,
            product = product!!,
            similarProducts = allProducts.filter { it.id != product!!.id },
            commentText = commentText,
            userRating = userRating,
            onCommentChange = viewModel::onCommentTextChanged,
            onRatingChange = viewModel::onUserRatingChanged,
            onSubmitComment = { viewModel.submitComment() },
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
            Text(text = "در حال بارگذاری محصول...", style = MaterialTheme.typography.bodyLarge)
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
    onCommentChange: (String) -> Unit,
    onRatingChange: (Float) -> Unit,
    onSubmitComment: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    var selectedTab by remember { mutableStateOf("توضیحات") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Image Container with Discount Badge & Quick Actions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(CardBgSlate)
            ) {
                // Product Image
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
                )

                // Discount Badge (Top Right)
                if (product.discountPrec > 0) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryOrangeRed
                        ) {
                            Text(
                                text = "${formatPersianPrice(product.discountPrec.toLong())}٪",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Overlay Action Icons (Left side)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "علاقه‌مندی",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "نشان‌کردن",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. Rating & Comments Info Pill Card (Overlapping Row)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "اشتراک‌گذاری",
                            tint = TextGrayColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "ذخیره",
                            tint = TextGrayColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Rating Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "امتیاز",
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = formatPersianPriceNumber("%.1f".format(product.rating)),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkColor
                            )
                        )
                    }

                    // Comments Count Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(PrimaryOrangeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatPersianPrice(product.reviewsCount.toLong()),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "نظرات",
                            tint = TextGrayColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 3. Product Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor
                    )
                )
            }

            // 4. Navigation Tabs / Filter Pills
            val tabs = listOf("توضیحات", "ویژگی ها", "نظرات", "محصولات مشابه")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = tab == selectedTab
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedTab = tab },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color.White else Color.Transparent,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryOrangeRed) else null
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isSelected) PrimaryOrangeRed else TextGrayColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 5. Section 1: Description ("توضیحات")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "توضیحات",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor
                    )
                )

                Text(
                    text = product.description.ifBlank {
                        "**با توجه به تفاوت رنگ‌ها در صفحه نمایش دستگاه‌های مختلف، ممکن است رنگ محصولات در تصویر تا ۲۰٪ با واقعیت متفاوت باشد** دارای ۵ رنگ جذاب. تمام نخ پنبه. پارچه پیراهن کشمیر. دم دست و پایین بلوز کشباف. سایزبندی M تا 4XL"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextDarkColor,
                        lineHeight = 22.sp
                    )
                )
            }

            // 6. Section 2: Features ("ویژگی ها")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ویژگی ها",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor
                    )
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "وزن خالص: ${product.features.weight.ifBlank { "۳۴۰ گرم" }}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkColor)
                        )
                        Text(
                            text = "رنگ: ${product.features.color.ifBlank { "متنوع" }}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkColor)
                        )
                        Text(
                            text = "وزن با بسته‌بندی: ۴۰۰ گرم",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkColor)
                        )
                    }
                }
            }

            // 7. Section 3: Submit Comment & Rating ("ثبت نظر")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ثبت نظر",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor
                    )
                )

                // Comment Input Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(16.dp),
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
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextGrayColor)
                            )
                        }
                        BasicTextField(
                            value = commentText,
                            onValueChange = onCommentChange,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextDarkColor),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Interactive Rating Selector & Submit Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onSubmitComment,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "ثبت نظر",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Interactive Star Rating Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "امتیاز دهید",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGrayColor)
                        )
                        Row {
                            for (i in 1..5) {
                                val starRating = i.toFloat()
                                Icon(
                                    imageVector = if (userRating >= starRating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star $i",
                                    tint = if (userRating >= starRating) Color(0xFFFFB800) else TextGrayColor,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { onRatingChange(starRating) }
                                )
                            }
                        }
                    }
                }
            }

            // 8. Section 4: User Reviews List ("نظرات کاربران")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "نظرات کاربران (${formatPersianPrice(product.comments.size.toLong())})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor
                    )
                )

                if (product.comments.isEmpty()) {
                    Text(
                        text = "هنوز نظری برای این محصول ثبت نشده است. اولین نفری باشید که نظر ثبت می‌کنید!",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextGrayColor),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    product.comments.forEach { comment ->
                        UserCommentCard(comment = comment)
                    }
                }
            }

            // 9. Section 5: Similar Products ("محصولات مشابه")
            if (similarProducts.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "محصولات مشابه",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
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
        }
    }
}

@Composable
fun UserCommentCard(
    comment: ProductComment,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatPersianPriceNumber("%.1f".format(comment.rating)),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor
                        )
                    )
                }
            }

            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextDarkColor),
                lineHeight = 20.sp
            )
        }
    }
}

private fun formatPersianPriceNumber(numberStr: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return numberStr.map { char ->
        if (char in '0'..'9') persianDigits[char - '0'] else char
    }.joinToString("")
}
