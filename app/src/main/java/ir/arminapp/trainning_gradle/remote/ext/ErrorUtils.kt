package ir.arminapp.trainning_gradle.remote.ext

import com.google.gson.Gson
import retrofit2.Response
import ir.arminapp.trainning_gradle.remote.dataModel.ErrorModel


object ErrorUtils {

    fun getError(response: Response<*>):String{

        var error:String? = null
        var errorBody = response.errorBody()
        if (errorBody != null){
            error = Gson()
                .fromJson(errorBody.string() , ErrorModel::class.java).message

        }

        return error ?: "ارتباط با سرور امکان پذیر نبود"
    }

}