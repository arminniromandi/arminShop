package ir.arminapp.trainning_gradle.remote

import ir.arminapp.trainning_gradle.remote.apiRepository.AllProductApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    private const val baseUrl = "https://fakestoreapi.com/"

    private val retrofit : Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService : AllProductApiService = retrofit.create(AllProductApiService::class.java)


}