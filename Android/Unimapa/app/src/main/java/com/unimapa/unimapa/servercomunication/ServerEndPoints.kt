package com.unimapa.unimapa.servercomunication

import com.unimapa.unimapa.servercomunication.datamodels.NotificationToken
import com.unimapa.unimapa.servercomunication.datamodels.SignUp
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.Call;
import retrofit2.http.Body

/**
 * Created by flavio.matheus on 05/05/19.
 */
interface ServerEndPoints {

    @POST("/sign-up")
    @FormUrlEncoded
    fun sendSignUpToken(@Body token : SignUp)


    @POST("/notification")
    @FormUrlEncoded
    fun sendNotificationTokenCreated(@Body token : NotificationToken)
}