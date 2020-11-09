package com.fareez.opentriviaapp.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

class TokenModel {

    @field:Json(name = "response_code")
    var responseCode: Int? = 0

    @field:Json(name = "response_message")
    var responseMessage: String? = null

    @field:Json(name = "token")
    var responseToken: String? = null
}