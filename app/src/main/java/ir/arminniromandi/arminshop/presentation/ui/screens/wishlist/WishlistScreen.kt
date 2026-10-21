package ir.arminniromandi.arminshop.presentation.ui.screens.wishlist

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.presentation.components.formatPersianPrice
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor

data class WishlistItemUi(
    val id: Int,
    val title: String,
    val imageRes: Int,
    val discountPercent: Int,
    val mainPrice: Long,
    val finalPrice: Long
)

@Composable
fun WishlistScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    wishlistItems: List<WishlistItemUi> = emptyList(),
    onProductClick: (Int) -> Unit = {},
    onAddToCart: (Int) -> Unit = {}
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Header: Back icon and Title "علاقه‌مندی‌های من"
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
                    text = "علاقه مندی های من",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextDarkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }

            if (wishlistItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هیچ محصولی در لیست علاقه مندی ها وجود ندارد",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextGrayColor,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                // 2-Column Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(wishlistItems, key = { it.id }) { item ->
                        WishlistCard(
                            item = item,
                            onClick = { onProductClick(item.id) },
                            onAddToCart = { onAddToCart(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistCard(
    item: WishlistItemUi,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE9EDF0),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Row: Bookmark Icon & Discount Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bookmark Icon (Filled, dark gray/black)
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "علاقه‌مندی",
                    tint = Color(0xFF2C3437),
                    modifier = Modifier.size(22.dp)
                )

                // Discount Badge (if > 0)
                if (item.discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryOrangeRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${item.discountPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(20.dp))
                }
            }

            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // White Overlay Card at the Bottom (Image 2 style)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Product Title
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDarkColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bottom Row: Price block + Add circle button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Red Circular Add Button (+)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PrimaryOrangeRed)
                                .clickable { onAddToCart() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "افزودن",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Price column (Final price & Strikethrough main price)
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            // Final Price
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatPersianPrice(item.finalPrice),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextDarkColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "تومان",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextGrayColor,
                                        fontSize = 9.sp
                                    )
                                )
                            }

                            // Original Strikethrough Price
                            if (item.mainPrice > item.finalPrice) {
                                Text(
                                    text = formatPersianPrice(item.mainPrice),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 10.sp,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
