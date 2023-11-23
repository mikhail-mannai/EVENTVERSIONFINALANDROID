package com.example.talentahub.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Event(
    @SerializedName("_id") val _id : String,
    @SerializedName("name") val name: String,
    @SerializedName("date") val date: String,
    @SerializedName("description") val description: String,
    @SerializedName("location") val location: String,
    @SerializedName("image") val image: String
): Serializable
