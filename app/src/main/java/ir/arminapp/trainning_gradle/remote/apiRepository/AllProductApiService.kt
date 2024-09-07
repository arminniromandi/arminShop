package ir.arminapp.trainning_gradle.remote.apiRepository

import ir.arminapp.trainning_gradle.remote.dataModel.ProductModel
import retrofit2.http.GET
import retrofit2.Response

interface AllProductApiService {

    @GET("/products")
     suspend fun getProduct():Response<ProductModel>

    @GET("/products/category/electronics")
    suspend fun getElectronic():Response<ProductModel>

    @GET("/products/category/jewelery")
    suspend fun getJewelery():Response<ProductModel>

    @GET("/products/category/men's clothing")
    suspend fun getManCloth():Response<ProductModel>

    @GET("/products/category/women's clothing")
    suspend fun getWomanCloth():Response<ProductModel>




}