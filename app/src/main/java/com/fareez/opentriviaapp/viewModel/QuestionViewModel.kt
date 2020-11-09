package com.fareez.opentriviaapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.fareez.opentriviaapp.model.QuestionCountModel
import com.fareez.opentriviaapp.model.QuestionModel
import com.fareez.opentriviaapp.model.TokenModel
import com.fareez.opentriviaapp.network.ApiServices
import com.fareez.opentriviaapp.util.Constants
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class QuestionViewModel(application: Application): AndroidViewModel(application) {

    private var questionApi: ApiServices = ApiServices.getServices()

    fun requestGetQuestionFromApi(category: Int): Observable<QuestionModel>{
        return questionApi.getQuestions(category, 1, Constants.API_TOKEN)
    }

    fun requestNewToken(): Observable<TokenModel>{
        return questionApi.resetToken("request")
                .doOnNext {
                    val newToken = it.responseToken.toString()
                    Constants.API_TOKEN = newToken
                    Log.i("Aryan", "Token Changes in view model !!!")
                }
    }

    fun getQuestionCount(): Observable<QuestionCountModel>{
        return questionApi.getQuestionCount()
    }
}