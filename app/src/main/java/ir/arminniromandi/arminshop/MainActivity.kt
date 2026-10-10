package ir.arminniromandi.arminshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ir.arminniromandi.arminshop.screens.LoginScreen
import ir.arminniromandi.arminshop.ui.theme.ArminShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArminShopTheme {
                LoginScreen()
            }
        }
    }
}
