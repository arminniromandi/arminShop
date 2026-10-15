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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.arminniromandi.arminshop.R
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    gesturesEnabled: Boolean = true,
    userName: String = "بهرام افشاری",
    userPhone: String = "۰۹۱۵۳۴۳۶۴۶۷",
    userAvatarRes: Int = R.drawable.malecloth,
    onProfileClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {},
    onCloseDrawer: () -> Unit = {},
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            modifier = modifier,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White,
                    drawerShape = RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = 24.dp,
                        bottomEnd = 24.dp
                    ),
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Top Drawer Header Row: Logo on Left, Close X button on Right
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Logo Section
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.top_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(36.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.onlineshop),
                                    contentDescription = "Online Shop",
                                    modifier = Modifier.height(20.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Close X Button
                            IconButton(
                                onClick = onCloseDrawer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "بستن منو",
                                    tint = TextDarkColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // User Info Header Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onProfileClick()
                                    onCloseDrawer()
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
                                        .clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = userAvatarRes),
                                        contentDescription = userName,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = userName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = TextDarkColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    )

                                    Text(
                                        text = userPhone,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextGrayColor,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Drawer Menu Items
                        val menuItems = listOf(
                            DrawerMenuItem("پروفایل کاربری", Icons.Outlined.Person, onProfileClick),
                            DrawerMenuItem("سفارشات من", Icons.AutoMirrored.Outlined.ReceiptLong, onOrdersClick),
                            DrawerMenuItem("پشتیبانی", Icons.Outlined.SupportAgent, onSupportClick),
                            DrawerMenuItem("قوانین و مقررات", Icons.Outlined.AssignmentTurnedIn, onTermsClick),
                            DrawerMenuItem("درباره ما", Icons.Outlined.Groups, onAboutUsClick),
                            DrawerMenuItem("ارتباط با ما", Icons.Outlined.Call, onContactUsClick)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            menuItems.forEach { item ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            item.onClick()
                                            onCloseDrawer()
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
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
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFFF5F5F5)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.title,
                                                tint = Color(0xFFFA4A23),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) {
            content()
        }
    }
}
