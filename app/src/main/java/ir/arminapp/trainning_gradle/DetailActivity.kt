package ir.arminapp.trainning_gradle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import ir.arminapp.trainning_gradle.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val image = intent.getStringExtra("image")
        val price = intent.getStringExtra("price")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")

        Glide.with(this)
            .load(image)
            .into(binding.image)

        binding.Detail.text = title
        binding.txtPrice.text = price
        binding.txtDes.text = description

        binding.back.setOnClickListener {
            finish()
        }


    }
}