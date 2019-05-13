package com.unimapa.unimapa.servercomunication

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

/**
 * Created by flavio.matheus on 05/05/19.
 */
object RetrofitClientInstance {

    private var retrofit: Retrofit? = null
    private val BASE_URL = "http://07f465a1.ngrok.io/"

    fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {
            retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit!!
    }
}