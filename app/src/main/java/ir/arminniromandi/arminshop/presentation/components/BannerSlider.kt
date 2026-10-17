package ir.arminniromandi.arminshop.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.arminniromandi.arminshop.R
import kotlinx.coroutines.launch

/**
 * Banner Slider component featuring banner.png with discountbaner.png badge
 * positioned at the top-left corner, moving along with slides, and side white pill navigation buttons.
 */
@Composable
fun BannerSlider(
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    val banners = listOf(
        R.drawable.baner,
        R.drawable.baner
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                // Banner Image
                Image(
                    painter = painterResource(id = banners[page]),
                    contentDescription = "Banner Slide $page",
                    modifier = Modifier.fillMaxSize()

                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Discount Badge overlay at top-left corner (TopStart)
                Image(
                    painter = painterResource(id = R.drawable.discountbaner),
                    contentDescription = "Discount Badge",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding( end = 18.dp)
                        .offset(y = (-2).dp)
                        .height(32.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Left white side button (previous)
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(width = 24.dp, height = 48.dp)
                .shadow(2.dp, RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                .clickable {
                    coroutineScope.launch {
                        val prev = if (pagerState.currentPage > 0) pagerState.currentPage - 1 else 1
                        pagerState.animateScrollToPage(prev)
                    }
                },
            shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
            color = Color.White
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Slide",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Right white side button (next)
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 24.dp, height = 48.dp)
                .shadow(2.dp, RoundedCornerShape(12.dp))
                .clickable {
                    coroutineScope.launch {
                        val next = if (pagerState.currentPage < 1) pagerState.currentPage + 1 else 0
                        pagerState.animateScrollToPage(next)
                    }
                },
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Slide",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, locale = "fa")
@Composable
private fun BannerSliderPreview() {
    BannerSlider()
}
