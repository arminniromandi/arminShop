package ir.arminapp.trainning_gradle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.arminapp.trainning_gradle.databinding.FragmentCategoryBinding
import ir.arminapp.trainning_gradle.tool.GetCategory

class CategoryFragment : Fragment()  {
    private lateinit var binding: FragmentCategoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        binding.recycler.visibility = View.GONE
        binding.category.root.visibility = View.VISIBLE
        var get = GetCategory(requireContext() , binding.recycler)

        binding.category.electronicImageView.setOnClickListener {
            showRecycler()
            get.getElectronic()
        }

        binding.category.gold.setOnClickListener {
            showRecycler()
            get.getJewelery()
        }

        binding.category.manImageView.setOnClickListener {
            showRecycler()
            get.getManCloth()
        }

        binding.category.womanImageView.setOnClickListener {
            showRecycler()
            get.getWomanCloth()
        }





        return binding.root
    }
    fun showRecycler(){
        binding.recycler.visibility = View.VISIBLE
        binding.category.root.visibility = View.GONE
    }





}


