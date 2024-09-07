package ir.arminapp.trainning_gradle.tool

import android.content.Context
import android.util.Log

import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.arminapp.trainning_gradle.adapter.RecyclerAdapter
import ir.arminapp.trainning_gradle.remote.RetrofitService
import ir.arminapp.trainning_gradle.remote.dataModel.ProductModelItem
import ir.arminapp.trainning_gradle.remote.ext.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetCategory(private val context:Context, private val recyclerView: RecyclerView) {
    private val retrofit = RetrofitService.apiService

    fun getElectronic() {

        CoroutineScope(Dispatchers.IO).launch {
            val data = retrofit.getElectronic()
            CoroutineScope(Dispatchers.Main).launch {
                if (data.isSuccessful) {
                    data.body()?.let { setRecycler(it.toList()) }
                }else
                    Toast.makeText(context, ErrorUtils.getError(data), Toast.LENGTH_SHORT).show()

            }

        }
    }

    fun getJewelery() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = retrofit.getJewelery()
            CoroutineScope(Dispatchers.Main).launch {
                if (data.isSuccessful) {
                    data.body()?.let { setRecycler(it.toList()) }
                }else
                    Toast.makeText(context, ErrorUtils.getError(data), Toast.LENGTH_SHORT).show()

            }

        }
    }

    fun getManCloth() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = retrofit.getManCloth()
            CoroutineScope(Dispatchers.Main).launch {
                if (data.isSuccessful) {
                    data.body()?.let { setRecycler(it.toList()) }
                }else
                    Toast.makeText(context, ErrorUtils.getError(data), Toast.LENGTH_SHORT).show()

            }

        }
    }

    fun getWomanCloth() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = retrofit.getWomanCloth()
            CoroutineScope(Dispatchers.Main).launch {
                if (data.isSuccessful) {
                    data.body()?.let { setRecycler(it.toList()) }
                }else
                    Toast.makeText(context, ErrorUtils.getError(data), Toast.LENGTH_SHORT).show()

            }

        }
    }


    private fun setRecycler(data: List<ProductModelItem>) {

        recyclerView.layoutManager = GridLayoutManager(
            context,
            2,
            RecyclerView.VERTICAL,
            false
        )
        recyclerView.adapter = RecyclerAdapter(data, context)


    }
}
