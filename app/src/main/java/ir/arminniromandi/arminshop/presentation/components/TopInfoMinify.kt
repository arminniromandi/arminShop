package ir.arminniromandi.arminshop.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(locale = "fa")
@Composable
private fun TopInfoPreview() {
    TopInfoMinify(
        userName = "آرمین نیرومندی",
        phoneNumber = "۰۹۳۰۰۳۱۸۲۳۹"
    )
}

@Composable
fun TopInfoMinify(
    modifier: Modifier = Modifier,
    userName: String = "بدون نام",
    phoneNumber: String = "شماره ای ثبت نشده."
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        bottomStart = 50.dp,
                        bottomEnd = 50.dp
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
                .padding(top = 16.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with white arc outline
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(65.dp)
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val strokeWidth = 3.5.dp.toPx()
                    val padding = strokeWidth / 2
                    val arcSize = androidx.compose.ui.geometry.Size(
                        width = size.width - strokeWidth,
                        height = size.height - strokeWidth
                    )
                    drawArc(
                        color = Color.White,
                        startAngle = -85f,
                        sweepAngle = 265f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(padding, padding),
                        size = arcSize,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }

                // Avatar Circle placeholder
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color(0xFFE6D1C9)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = userName.firstOrNull()?.toString() ?: "",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}
