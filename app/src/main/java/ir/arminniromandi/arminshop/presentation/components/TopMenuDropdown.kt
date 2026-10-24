package ir.arminniromandi.arminshop.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor

data class TopMenuDropdownItem(
    val title: String,
    val icon: Int,
    val onClick: () -> Unit
)

@Composable
fun TopMenuDropdown(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    userName: String? = null,
    userEmail: String? = null,
    userPhone: String? = null,

    onProfileClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {}
) {
    if (isOpen) {
        // Transparent clickable dismiss area (no dark blur/dim overlay so the content underneath stays visible)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(99f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest
                )
        )
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 350)
        ) + fadeIn(animationSpec = tween(durationMillis = 250)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 200)),
        modifier = modifier.zIndex(100f)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Prevents clicking the menu from dismissing it
                    )
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                color = Color.White,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    // Profile Header Card (Peach/Cream tinted background)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onProfileClick()
                                onDismissRequest()
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFDEEE6)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    if (!userName.isNullOrBlank()) userName.first().toString() else "ن",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                )
                            }

                            Text(
                                text = if (!userName.isNullOrBlank()) userName else "نام کاربر ثبت نشده",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextDarkColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )

                            Spacer(modifier = Modifier.weight(1f))



                                Text(
                                    text = if (!userEmail.isNullOrBlank()) userEmail else if (!userPhone.isNullOrBlank()) userPhone else "ایمیل یا شماره ثبت نشده",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                )

                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Menu items list
                    val menuItems = listOf(
                        TopMenuDropdownItem("پروفایل کاربری", R.drawable.user_nav) {
                            onProfileClick()
                            onDismissRequest()
                        },
                        TopMenuDropdownItem("سفارشات من", R.drawable.task_square) {
                            onOrdersClick()
                            onDismissRequest()
                        },
                        TopMenuDropdownItem("درباره ما", R.drawable.people) {
                            onAboutUsClick()
                            onDismissRequest()
                        },
                        TopMenuDropdownItem("ارتباط با ما", R.drawable.music_play) {
                            onContactUsClick()
                            onDismissRequest()
                        }
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        menuItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { item.onClick() }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = TextDarkColor,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                )

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF7F7F7))
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFFEEEEEE),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter =painterResource(item.icon) ,
                                        contentDescription = item.title,
                                        tint = Color(0xFFFA4A23),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
