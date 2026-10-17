package ir.arminniromandi.arminshop.presentation.ui.screens.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.arminniromandi.arminshop.presentation.components.BannerSlider
import ir.arminniromandi.arminshop.presentation.components.CategoryCard
import ir.arminniromandi.arminshop.presentation.ui.theme.TextDarkColor
import ir.arminniromandi.arminshop.utils.Constance.categories

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .padding(horizontal = 8.dp)
    ) {
        BannerSlider()
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "دسته بندی",
            style = MaterialTheme.typography.titleLarge.copy(color = TextDarkColor),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        LazyVerticalGrid(
             columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)

            ) {
            items(
                items = categories,
            ) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}
