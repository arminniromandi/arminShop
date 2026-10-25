package ir.arminniromandi.arminshop.presentation.ui.screens.contact

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.arminniromandi.arminshop.presentation.ui.theme.ArminShopTheme
import ir.arminniromandi.arminshop.presentation.ui.theme.PrimaryOrangeRed
import ir.arminniromandi.arminshop.presentation.ui.theme.ScreenBackground
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.presentation.ui.theme.TextGrayColor

@Composable
fun ContactUsScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    ContactUsContent(
        modifier = modifier,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun ContactUsContent(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val phoneNumber = "09031312719"
    val formattedPhone = formatPersianNumber(phoneNumber)
    val developerName = "آرمین نیرومندی"
    val developerLocation = "تهران، ملارد"

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScreenBackground)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // 1. TOP CURVED GRADIENT HEADER
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 40.dp,
                            bottomEnd = 40.dp
                        )
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFA6238),
                                Color(0xFFEA4326),
                                Color(0xFFE6371D)
                            )
                        )
                    )
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar (Back button + Title)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "ارتباط با ما",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Developer Avatar Box with Glowing Outer Arc
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 4.dp.toPx()
                            val padding = strokeWidth / 2
                            val arcSize = Size(
                                width = size.width - strokeWidth,
                                height = size.height - strokeWidth
                            )
                            drawArc(
                                color = Color.White,
                                startAngle = -85f,
                                sweepAngle = 270f,
                                useCenter = false,
                                topLeft = Offset(padding, padding),
                                size = arcSize,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            )
                        }

                        Surface(
                            modifier = Modifier.size(76.dp),
                            shape = CircleShape,
                            color = Color(0xFFFDE8D7)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "آ",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        color = PrimaryOrangeRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 32.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Developer Name & Title
                    Text(
                        text = developerName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "مدیریت و توسعه‌دهنده آرمین شاپ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // 2. CONTACT DETAILS & ABOUT CARDS
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Phone Number Card
                ContactInfoCard(
                    icon = Icons.Outlined.Call,
                    title = "شماره تماس و پشتیبانی",
                    subtitle = formattedPhone,
                    actionText = "تماس مستقیم",
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "امکان برقراری تماس وجود ندارد", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Location Card
                ContactInfoCard(
                    icon = Icons.Outlined.LocationOn,
                    title = "موقعیت مکانی",
                    subtitle = developerLocation,
                    actionText = "آدرس",
                    onClick = {}
                )

                // About / Description Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color.Black.copy(alpha = 0.08f)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFDEEE6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "درباره ما",
                                    tint = PrimaryOrangeRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Text(
                                text = "درباره آرمین شاپ و توسعه‌دهنده",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkColor,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        Text(
                            text = "اپلیکیشن فروشگاهی آرمین شاپ توسط آرمین نیرومندی با هدف ارائه تجربه‌ای روان، مدرن و باکیفیت برای خرید آنلاین پوشاک و محصولات مد طراحی و پیاده‌سازی شده است.\n\nدر صورت داشتن هرگونه سوال، پیشنهاد، انتقاد یا درخواست پشتیبانی و همکاری می‌توانید از طریق شماره تماس بالا با ما در ارتباط باشید.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF555555),
                                fontSize = 13.sp,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }

                // ==========================================
                // 3. ACTION BUTTONS
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "امکان برقراری تماس وجود ندارد", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrangeRed)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Call,
                                contentDescription = "تماس",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "تماس تلفنی",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            try {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "آرمین نیرومندی - آرمین شاپ\nموقعیت: $developerLocation\nشماره تماس: $phoneNumber"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری اطلاعات ارتباطی"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "امکان اشتراک‌گذاری اطلاعات وجود ندارد", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrangeRed),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryOrangeRed)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "اشتراک‌گذاری",
                                tint = PrimaryOrangeRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "اشتراک‌گذاری",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = PrimaryOrangeRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ContactInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFDEEE6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = PrimaryOrangeRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextGrayColor,
                            fontSize = 12.sp
                        )
                    )

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkColor,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFF0EC)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PrimaryOrangeRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun formatPersianNumber(numberStr: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return numberStr.map { char ->
        if (char in '0'..'9') persianDigits[char - '0'] else char
    }.joinToString("")
}

@Preview(showBackground = true, locale = "fa")
@Composable
fun ContactUsScreenPreview() {
    ArminShopTheme {
        ContactUsContent()
    }
}
