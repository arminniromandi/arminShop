package ir.arminniromandi.arminshop.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.arminniromandi.arminshop.data.mock.MockProducts
import ir.arminniromandi.arminshop.domain.model.Product
import ir.arminniromandi.arminshop.presentation.animation.productSharedElement
import ir.arminniromandi.arminshop.presentation.ui.theme.ArminShopTheme
import ir.arminniromandi.arminshop.presentation.ui.theme.CardBgSlate
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor

@Composable
fun ProductItemCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAddToCartClick: (Product) -> Unit = {}
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
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
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
            ProductImage(
                imageUrl = product.imageUrl,
                fallbackRes = product.imageRes,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp)
                    .productSharedElement("product-image-${product.id}"),
                contentScale = ContentScale.Fit
            )
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
                        .padding(top = 10.dp, bottom = 10.dp, end = 10.dp, start = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextDarkColor),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.productSharedElement("product-title-${product.id}")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatPersianPrice(product.mainPrice),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (product.discountPrec != 0) FontWeight.Normal else FontWeight.Bold,
                                color = if (product.discountPrec != 0) TextGrayColor else TextDarkColor,
                                textDecoration = if (product.discountPrec != 0) TextDecoration.LineThrough else TextDecoration.None
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        if (product.discountPrec != 0) {
                            Text(
                                text = formatPersianPrice(product.finalPrice),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextDarkColor)
                            )
                        }
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "تومان",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDarkColor)
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
                        .clickable { onAddToCartClick(product) },
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

@Preview(showBackground = true, locale = "fa")
@Composable
private fun ProductItemCardPreview() {
    ArminShopTheme {
        ProductItemCard(
            product = MockProducts.sampleProducts.first(),
            onClick = {},
            onAddToCartClick = {}
        )
    }
}
