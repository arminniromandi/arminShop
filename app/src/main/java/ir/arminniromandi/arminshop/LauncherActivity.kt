package ir.arminniromandi.arminshop

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import ir.arminniromandi.arminshop.presentation.ui.theme.ArminShopTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            LaunchedEffect(Unit) {
                delay(3000L.milliseconds)

                startActivity(
                    Intent(
                        this@LauncherActivity,
                        MainActivity::class.java
                    )
                )

                finish()
            }
            ArminShopTheme {
                Column (modifier = Modifier.fillMaxSize()) {

                    Image(
                        painter = painterResource(id = R.drawable.spalsh_screen),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()


                    )

                }
            }
        }
    }
}

