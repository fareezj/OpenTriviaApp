package com.fareez.opentriviaapp.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

class QuestionCountModel {

    @field:Json(name = "categories")
    val categories : Categories? = null

    class Categories{

        @field:Json(name = "21")
        val categoryCode21 : CategoryDetails? = null

        @field:Json(name = "22")
        val categoryCode22 : CategoryDetails? = null

        @field:Json(name = "23")
        val categoryCode23 : CategoryDetails? = null

        @field:Json(name = "25")
        val categoryCode25 : CategoryDetails? = null

        @field:Json(name = "26")
        val categoryCode26 : CategoryDetails? = null

        @field:Json(name = "27")
        val categoryCode27 : CategoryDetails? = null

        @field:Json(name = "28")
        val categoryCode28 : CategoryDetails? = null
    }

    class CategoryDetails{

        @field:Json(name = "total_num_of_questions")
        val totalQuestion: Int = 0

        @field:Json(name = "total_num_of_pending_questions")
        val totalPendingQuestion: Int = 0

        @field:Json(name = "total_num_of_verified_questions")
        val totalVerifiedQuestion: Int = 0

        @field:Json(name = "total_num_of_rejected_questions")
        val totalRejectedQuestion: Int = 0

    }

}