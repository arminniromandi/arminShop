package ir.arminapp.trainning_gradle

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.arminapp.trainning_gradle.adapter.RecyclerAdapter
import ir.arminapp.trainning_gradle.databinding.FragmentHomeBinding
import ir.arminapp.trainning_gradle.remote.RetrofitService
import ir.arminapp.trainning_gradle.remote.ext.ErrorUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

            CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                val data = RetrofitService.apiService.getProduct()

                withContext(Dispatchers.Main) {

                    if (data.isSuccessful){
                        binding.recycler.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.INVISIBLE

                        binding.recycler.layoutManager = GridLayoutManager(requireContext() ,
                            2 ,
                            RecyclerView.VERTICAL,
                            false
                        )
                        binding.recycler.adapter = data?.let { it.body()
                            ?.let { it1 -> RecyclerAdapter(it1.toList(), requireContext()) } }

                    }else
                        Toast.makeText(requireContext(), ErrorUtils.getError(data), Toast.LENGTH_SHORT).show()


                }

            }


        }


        return binding.root
    }

}