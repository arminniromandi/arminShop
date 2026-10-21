package ir.arminniromandi.arminshop.presentation.ui.screens.experiences

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.arminniromandi.arminshop.data.local.entity.OrderEntity
import ir.arminniromandi.arminshop.presentation.components.formatPersianPrice
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor
import ir.arminniromandi.arminshop.presentation.viewmodels.MyExperiencesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyExperiencesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyExperiencesViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsStateWithLifecycle()

    var activeOrderForComment by remember { mutableStateOf<OrderEntity?>(null) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header with Back icon and Title "تجربه‌های خرید من"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "بازگشت",
                        tint = TextDarkColor
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "تجربه های خرید من",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextDarkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "محصول خریداری‌شده‌ای برای ثبت تجربه یافت نشد",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextGrayColor,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        ExperienceItemCard(
                            order = order,
                            onSubmitCommentClick = {
                                activeOrderForComment = order
                            }
                        )
                    }
                }
            }
        }

        // BottomSheet for submitting review/comment
        activeOrderForComment?.let { order ->
            SubmitReviewBottomSheet(
                order = order,
                onDismiss = { activeOrderForComment = null },
                onSubmit = { rating, comment ->
                    viewModel.submitReview(
                        orderId = order.id,
                        productId = order.productId,
                        rating = rating,
                        commentText = comment,
                        onSuccess = {
                            activeOrderForComment = null
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun ExperienceItemCard(
    order: OrderEntity,
    onSubmitCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Details (Left side in RTL)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Product Name
                    Text(
                        text = order.productName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextDarkColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ),
                        maxLines = 2
                    )

                    // Price + Review Action/Rating Row (Image 5 style)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Action / Rating
                        if (!order.hasCommented) {
                            // "ثبت نظر" button in primary orange/red
                            Text(
                                text = "ثبت نظر",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = PrimaryOrangeRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSubmitCommentClick() }
                                    .padding(vertical = 4.dp, horizontal = 4.dp)
                            )
                        } else {
                            // Star Rating (e.g. ⭐ 4.5 or ⭐ 3)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = if (order.commentRating != null) {
                                        if (order.commentRating % 1 == 0f) "${order.commentRating.toInt()}"
                                        else "${order.commentRating}"
                                    } else "۴",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextDarkColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Price
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatPersianPrice(order.price),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextDarkColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "تومان",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextGrayColor,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Product Image (Right side in RTL)
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F3F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = order.productImageRes),
                        contentDescription = order.productName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }
            }

            // If user has commented and there is text, display it below (Image 5 style)
            if (order.hasCommented && !order.commentText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = order.commentText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF616161),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubmitReviewBottomSheet(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onSubmit: (Float, String) -> Unit
) {
    var rating by remember { mutableFloatStateOf(5f) }
    var commentText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "ثبت نظر برای: ${order.productName}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor,
                        fontSize = 15.sp
                    )
                )

                // Rating Stars Selector
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "امتیاز شما به این محصول:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextGrayColor,
                            fontSize = 12.sp
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            val isSelected = star <= rating
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = "$star ستاره",
                                tint = if (isSelected) Color(0xFFFFB300) else Color(0xFFBDBDBD),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { rating = star.toFloat() }
                            )
                        }
                    }
                }

                // Review Comment TextField
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text(
                            text = "نظر و تجربه خود را درباره کیفیت و جنس این محصول بنویسید...",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFAAAAAA))
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrangeRed,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                // Submit Button
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onSubmit(rating, commentText)
                        }
                    },
                    enabled = commentText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "ثبت و ارسال نظر",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
