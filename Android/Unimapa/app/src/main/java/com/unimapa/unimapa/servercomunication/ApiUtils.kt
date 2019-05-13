package com.unimapa.unimapa.servercomunication
import kotlin.jvm.javaClass
/**
 * Created by flavio.matheus on 10/05/19.
 */
object ApiUtils {

    fun getApiService() : ServerEndPoints {
        return RetrofitClientInstance.getRetrofitInstance().create(ServerEndPoints::class.java)
    }
}