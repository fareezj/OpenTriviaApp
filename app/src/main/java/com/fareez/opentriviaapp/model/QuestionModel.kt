package com.fareez.opentriviaapp.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

class QuestionModel {

    @field:Json(name = "response_code")
    var responseCode: Int? = 0

    @field:Json(name = "results")
    var results: List<Details>? = null

    class Details{

        @field:Json(name = "category")
        var category: String = ""

        @field:Json(name = "type")
        var type: String = ""

        @field:Json(name = "difficulty")
        var difficulty: String = ""

        @field:Json(name = "question")
        var question: String = ""

        @field:Json(name = "correct_answer")
        var correct_answer: String = ""

        @field:Json(name = "incorrect_answers")
        var incorrect_answers: MutableList<String>? = null
    }
}


