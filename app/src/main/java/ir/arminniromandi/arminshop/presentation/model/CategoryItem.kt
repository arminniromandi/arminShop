package ir.arminniromandi.arminshop.presentation.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable


@Immutable
data class CategoryItem(
    val id: String,
    val name: String,
    @DrawableRes
    val iconRes: Int
)

