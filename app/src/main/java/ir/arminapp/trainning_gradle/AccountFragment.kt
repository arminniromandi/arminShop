package ir.arminapp.trainning_gradle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.arminapp.trainning_gradle.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {
    private lateinit var binding : FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)

        return binding.root
    }


}