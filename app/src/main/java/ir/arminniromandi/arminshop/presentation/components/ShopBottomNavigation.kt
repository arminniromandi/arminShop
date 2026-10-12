package ir.arminniromandi.arminshop.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.arminniromandi.arminshop.presentation.navigation.BottomNavItem
import ir.arminniromandi.arminshop.presentation.ui.theme.ArminShopTheme
import ir.arminniromandi.arminshop.presentation.ui.theme.ShopSecondaryText

/**
 * Bottom navigation bar items
 */


/**
 * Custom Animated Bottom Navigation Bar with smooth sliding top indicator line.
 *
 * @param selectedItem Currently selected [BottomNavItem]
 * @param onItemSelected Callback when an item is selected
 * @param iconContent Custom composable for rendering item icons (use painterResource / Image / Icon here)
 */
@Composable
fun ShopBottomNavigation(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
    iconContent: @Composable (item: BottomNavItem, isSelected: Boolean, tint: Color) -> Unit = { _, isSelected, tint ->
        // Placeholder box icon for user to easily replace with painterResource(R.drawable.my_icon)
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(tint.copy(alpha = if (isSelected) 1f else 0.35f))
        )
    }
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val items = BottomNavItem.entries
        val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
            ) {
                val totalWidth = maxWidth
                val itemCount = items.size
                val itemWidth = totalWidth / itemCount
                val indicatorWidth = 36.dp

                // Calculate animated X offset for top sliding indicator line
                val indicatorTargetOffset =
                    (itemWidth * selectedIndex) + ((itemWidth - indicatorWidth) / 2)
                val animatedIndicatorOffset by animateDpAsState(
                    targetValue = indicatorTargetOffset,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "IndicatorOffsetAnimation"
                )

                // Top Orange Indicator Line sliding smoothly between tabs
                Box(
                    modifier = Modifier
                        .offset(x = animatedIndicatorOffset, y = 0.dp)
                        .width(indicatorWidth)
                        .height(3.5.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )

                // Items Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val isSelected = index == selectedIndex
                        val activeColor = MaterialTheme.colorScheme.primary
                        val inactiveColor = ShopSecondaryText

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onItemSelected(item)
                                }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Custom Icon Slot
                            iconContent(
                                item,
                                isSelected,
                                if (isSelected) activeColor else inactiveColor
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Item Title Text
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) activeColor else inactiveColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ShopBottomNavigationPreview() {
    var selectedItem by remember { mutableStateOf(BottomNavItem.HOME) }

    ArminShopTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ShopBottomNavigation(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    }
}
