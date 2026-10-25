package ir.arminniromandi.arminshop.presentation.ui.screens.cart

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.arminniromandi.arminshop.data.local.entity.CartItemEntity
import ir.arminniromandi.arminshop.presentation.components.formatPersianPrice
import ir.arminniromandi.arminshop.presentation.components.ProductImage
import ir.arminniromandi.arminshop.presentation.ui.theme.CardBgSlate
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor
import ir.arminniromandi.arminshop.presentation.viewmodels.CartSharedViewModel

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartSharedViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToCheckout: () -> Unit = {}
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val subtotalPrice by viewModel.subtotalPrice.collectAsStateWithLifecycle()
    val discountAmount by viewModel.discountAmount.collectAsStateWithLifecycle()
    val itemsFinalPrice by viewModel.itemsFinalPrice.collectAsStateWithLifecycle()

    CartScreenContent(
        modifier = modifier,
        cartItems = cartItems,
        subtotalPrice = subtotalPrice,
        discountAmount = discountAmount,
        finalPrice = itemsFinalPrice,
        onIncreaseQuantity = { item ->
            viewModel.updateQuantity(item.productId, item.quantity + 1)
        },
        onDecreaseQuantity = { item ->
            viewModel.updateQuantity(item.productId, item.quantity - 1)
        },
        onClearCart = {
            viewModel.clearCart()
        },
        onNavigateToHome = onNavigateToHome,
        onNavigateToCheckout = onNavigateToCheckout
    )
}

@Composable
fun CartScreenContent(
    modifier: Modifier = Modifier,
    cartItems: List<CartItemEntity>,
    subtotalPrice: Long,
    discountAmount: Long,
    finalPrice: Long,
    onIncreaseQuantity: (CartItemEntity) -> Unit,
    onDecreaseQuantity: (CartItemEntity) -> Unit,
    onClearCart: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header: Title "سبد خرید"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp, top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سبد خرید",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextDarkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )

                if (cartItems.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onClearCart() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف همه",
                            tint = PrimaryOrangeRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "حذف همه",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PrimaryOrangeRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            if (cartItems.isEmpty()) {
                // Empty Cart State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(CardBgSlate),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = PrimaryOrangeRed,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Text(
                            text = "سبد خرید شما خالی است!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextDarkColor,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "می‌توانید برای مشاهده محصولات به صفحه اصلی بروید",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextGrayColor),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onNavigateToHome,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "مشاهده فروشگاه",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            } else {
                // Cart Items List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(cartItems, key = { it.productId }) { item ->
                        CartItemCardImageStyle(
                            item = item,
                            onIncrease = { onIncreaseQuantity(item) },
                            onDecrease = { onDecreaseQuantity(item) }
                        )
                    }
                }

                // Summary Card & Checkout Button (Image 1 Style)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // جمع قیمت
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "جمع قیمت:",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextGrayColor,
                                    fontSize = 13.sp
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatPersianPrice(if (subtotalPrice > 0) subtotalPrice else finalPrice),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = TextDarkColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تومان",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextGrayColor)
                                )
                            }
                        }

                        // تخفیف (قرمز / نارنجی)
                        if (discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "تخفیف:",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextGrayColor,
                                        fontSize = 13.sp
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = formatPersianPrice(discountAmount),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = PrimaryOrangeRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "تومان",
                                        style = MaterialTheme.typography.bodySmall.copy(color = PrimaryOrangeRed)
                                    )
                                }
                            }
                        }

                        // مبلغ نهایی
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مبلغ نهایی:",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextGrayColor,
                                    fontSize = 13.sp
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatPersianPrice(finalPrice),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextDarkColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تومان",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextDarkColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // دکمه ادامه خرید
                        Button(
                            onClick = onNavigateToCheckout,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "ادامه خرید",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Cart Item Card exactly matching Image 1:
 * - Right: Title + Final price + Strikethrough original price
 * - Center: Product image in rounded slate container
 * - Left: Vertical counter pill (+ in green, count, - in red)
 */
@Composable
fun CartItemCardImageStyle(
    item: CartItemEntity,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Right Side: Title & Prices
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextDarkColor,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Prices: Final price in تومان and strikethrough original price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${formatPersianPrice(item.price)} تومان",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor,
                            fontSize = 12.sp
                        )
                    )

                    if (item.originalPrice > item.price) {
                        Text(
                            text = formatPersianPrice(item.originalPrice),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextGrayColor,
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // 2. Center: Product Image in soft rounded box
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F3F6)),
                contentAlignment = Alignment.Center
            ) {
                ProductImage(
                    imageUrl = item.imageUrl,
                    fallbackRes = item.imageRes,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 3. Left Side: Vertical Counter Pill (+ / count / -)
            Surface(
                modifier = Modifier
                    .width(32.dp)
                    .height(82.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF7F8FA)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Green Plus Icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { onIncrease() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "افزایش تعداد",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Quantity Number (Persian)
                    Text(
                        text = formatPersianPrice(item.quantity.toLong()),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor,
                            fontSize = 13.sp
                        )
                    )

                    // Red Minus Icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { onDecrease() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "کاهش تعداد",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
