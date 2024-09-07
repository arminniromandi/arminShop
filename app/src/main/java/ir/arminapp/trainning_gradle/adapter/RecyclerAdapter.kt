package ir.arminapp.trainning_gradle.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import ir.arminapp.trainning_gradle.DetailActivity
import ir.arminapp.trainning_gradle.databinding.SampleRecyclerBinding
import ir.arminapp.trainning_gradle.remote.dataModel.ProductModelItem

class RecyclerAdapter(
    private val product: List<ProductModelItem>,
    private val context: Context
    ) : RecyclerView.Adapter<RecyclerAdapter.ProductViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            SampleRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun getItemCount() = product.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.setData(product[position])
    }

    inner class ProductViewHolder(
        private val binding:SampleRecyclerBinding
    ) : ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun setData(data: ProductModelItem) {
            binding.text.text = data.title
            binding.textView2.text = "$ ${data.price}"
            Glide.with(context)
                .load(data.image)
                .into(binding.imageSample)

            binding.root.setOnClickListener {


                Intent(context,DetailActivity::class.java).apply {
                    putExtra("image",data.image)
                    putExtra("price",data.price)
                    putExtra("title",data.title)
                    putExtra("des" ,data.description)
                    context.startActivity(this)
                }
            }



        }
    }



}

