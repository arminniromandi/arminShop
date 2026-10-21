package ir.arminniromandi.arminshop.presentation.model

import androidx.annotation.DrawableRes
import androidx.navigation.NavHostController

data class ProfileMenuItem(
    val title: String,
    @param:DrawableRes val iconRes: Int,
    val onClick: (NavHostController) -> Unit = {}
)
