package com.example.talentahub.Repository

import com.example.talentahub.models.Event
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


interface RestApi {

    @GET("/event/all")
    fun getAllEvent(): Call<MutableList<Event>>


    companion object {

        var BASE_URL = "http://192.168.1.198:9090"

        fun create(): RestApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(RestApi::class.java)
        }
    }
}