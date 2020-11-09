package com.fareez.opentriviaapp.view

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.fareez.opentriviaapp.R
import com.fareez.opentriviaapp.model.QuestionModel
import com.fareez.opentriviaapp.util.Constants
import com.fareez.opentriviaapp.viewModel.QuestionViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_title.*
import org.koin.android.ext.android.inject


class GameFragment : Fragment() {

    private val subscriptions = CompositeDisposable()
    private lateinit var navController: NavController
    private val viewModel by inject<QuestionViewModel>()
    private var categoryValue: Int = 0
    private var extractedData: QuestionModel.Details? = null
    private var marks: Int = 0
    private var TAG: String = "Trivia"
    private val sharedAPIToken = "api_token"
    private val sharedScore = "score"
    private var bundle = Bundle()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init nav controller
        navController = Navigation.findNavController(view)

        // Loading data operation
        pb_question_refresh.visibility = View.VISIBLE
        hideAllUI()

        // Put fragment title on action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Trivia App"

        // Get data from bundle given from previous fragment
        val recipient = arguments?.getString("recipient")
        val fetchedCategory = recipient
        Log.i(TAG, "Recipient - $recipient !!!")
        tv_question.setText(fetchedCategory.toString())

        // Equating fetched category with its codes accordingly
        when(fetchedCategory){
            "Sports" -> categoryValue = 21
            "Geography" -> categoryValue = 22
            "History" -> categoryValue = 23
            "Art" -> categoryValue = 25
            "Celebrities" -> categoryValue = 26
            "Animals" -> categoryValue = 27
            "Vehicles" -> categoryValue = 28
        }

        // Try fetching cached token
        getCachedToken(categoryValue)

        // Request questions from API
        requestGetQuestionFromApi(categoryValue)

        // Validate input results and its operations
        rg_answers.setOnCheckedChangeListener { radioGroup, i ->
            val chosenAnswer = view.findViewById<View>(i) as RadioButton
            validateResult(chosenAnswer.text.toString())
            disableAllAnswer()
            resetAnswer()
        }

        // Request for new questions
        btn_next.setOnClickListener {
            hideAllUI()
            pb_question_refresh.visibility = View.VISIBLE
            cycleQuestion(categoryValue)
        }

        // Finish operations
        btn_done.setOnClickListener {
            Log.i(TAG, "Send Score -$bundle")

            navController.navigate(
                    R.id.action_gameFragment_to_titleFragment, bundle
            )
        }
    }

    private fun requestGetQuestionFromApi(catValue: Int){
        val subscribe = viewModel.requestGetQuestionFromApi(catValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    pb_question_refresh.visibility = View.GONE

                    // Check API validity
                    if (it.responseCode!! == 4 || it.responseCode!! == 3) {
                        Log.i(TAG, "Token Response Code - ${it.responseCode} !!!")
                        Log.i(TAG, "Token Should Reset - ${Constants.API_TOKEN} !!!")

                        // Request new token if expired
                        requestNewToken(catValue)
                    } else {
                        Log.i(TAG, "Token Validation code - ${it.responseCode} !!!")
                    }

                    // Populate data to UI
                    showAllUI()
                    val data: List<QuestionModel.Details>? = it.results
                    //var extractedData: QuestionModel.Details
                    if (data != null) {
                        for (i in data) {
                            setupUI(i)
                            extractedData = i
                            //tv_question.setText(extractedData.category)
                        }
                    }
                }, { err ->
                    var msg = err.localizedMessage
                })
        subscriptions.add(subscribe)
    }

    private fun getCachedToken(catValue: Int){

        // Get cached token from sharedPreferences
        val sharedPref: SharedPreferences = requireActivity()
                .getSharedPreferences(sharedAPIToken, Context.MODE_PRIVATE)
        val cachedAPIToken = sharedPref.getString("token_key", null)
        Log.i(TAG, "Cached Token Check----")
        Log.i(TAG, "Cached Token: $cachedAPIToken")

        // Request new token if not exist
        if(cachedAPIToken == null){
            requestNewToken(catValue)
        }else{
            Constants.API_TOKEN = cachedAPIToken
        }
    }

    private fun requestNewToken(catValue: Int){
        val subscribe = viewModel.requestNewToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    Constants.API_TOKEN = it.responseToken.toString()
                    Log.i(TAG, "Token Changes- ${Constants.API_TOKEN} !!!")

                    // Adding new token to sharedPreferences
                    val sharedPref: SharedPreferences = requireActivity()
                            .getSharedPreferences(sharedAPIToken, Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor =  sharedPref.edit()
                    editor.putString("token_key", it.responseToken.toString())
                    editor.apply()

                    // Fire new question request from API
                    requestGetQuestionFromApi(catValue)
                }, { err ->
                    var msg = err.localizedMessage
                    tv_question.setText(msg)
                })
        subscriptions.add(subscribe)
    }

    private fun setupUI(extractedData: QuestionModel.Details){

        tv_question.text = extractedData.question
        tv_category.text = extractedData.category
        tv_difficulty.text = extractedData.difficulty
        // Add list of incorrect answers to MutableList
        var incorrectList: MutableList<String>? = extractedData.incorrect_answers

        val correctAns = extractedData.correct_answer
        // Add list of correct answers to the existing MutableList
        incorrectList?.add(correctAns)
        Log.i(TAG, incorrectList.toString())

        // Shuffle all the answers
        incorrectList!!.shuffle()
        tv_answer.visibility = View.GONE
        tv_result.visibility = View.GONE

        if(incorrectList!!.size > 2){

            rb_ans1.visibility = View.VISIBLE;
            rb_ans2.visibility = View.VISIBLE;
            rb_ans3.visibility = View.VISIBLE;
            rb_ans4.visibility = View.VISIBLE;

            rb_ans1.text = incorrectList.elementAt(0).toString()
            rb_ans2.text = incorrectList.elementAt(1).toString()
            rb_ans3.text = incorrectList.elementAt(2).toString()
            rb_ans4.text = incorrectList.elementAt(3).toString()

        }else{
            rb_ans3.visibility = View.GONE;
            rb_ans4.visibility = View.GONE;
            rb_ans1.text = incorrectList.elementAt(0).toString()
            rb_ans2.text = incorrectList.elementAt(1).toString()
        }
    }

    private fun validateResult(chosenAns: String){

        // Show results to the answers
        if(chosenAns == extractedData!!.correct_answer){

            // If correct, increment the marks
            tv_result.text = "CORRECT !"
            marks++
            val totalMarks = marks
            tv_score.text = totalMarks.toString()
            tv_answer_title.visibility = View.GONE
            tv_answer.visibility = View.GONE
            val totalScore: String = totalMarks.toString()

            //Add the total score to the bundle
            bundle = bundleOf("score" to totalScore)
        }else{
            tv_result.text = "WRONG !"
            tv_answer_title.visibility = View.VISIBLE
            tv_answer.visibility = View.VISIBLE
        }
        tv_answer.text = extractedData!!.correct_answer
        tv_result.visibility = View.VISIBLE

    }

    private fun cycleQuestion(catValue: Int){
        enableAllAnswer()
        getCachedToken(catValue)
        requestGetQuestionFromApi(catValue)
    }

    private fun resetAnswer(){
        rb_ans1.isChecked = false
        rb_ans2.isChecked = false
        rb_ans3.isChecked = false
        rb_ans4.isChecked = false
    }

    private fun disableAllAnswer(){
        rb_ans1.isEnabled = false
        rb_ans2.isEnabled = false
        rb_ans3.isEnabled = false
        rb_ans4.isEnabled = false
    }

    private fun enableAllAnswer(){
        rb_ans1.isEnabled = true
        rb_ans2.isEnabled = true
        rb_ans3.isEnabled = true
        rb_ans4.isEnabled = true
    }

    private fun hideAllUI(){
        cv_category_text.visibility = View.GONE
        cv_difficulty_text.visibility = View.GONE
        cv_question_text.visibility = View.GONE
        rg_answers.visibility = View.GONE
        tv_result.visibility = View.GONE
        tv_answer_title.visibility = View.GONE
        tv_answer.visibility = View.GONE
        btn_next.visibility = View.GONE
    }

    private fun showAllUI(){
        cv_category_text.visibility = View.VISIBLE
        cv_difficulty_text.visibility = View.VISIBLE
        cv_question_text.visibility = View.VISIBLE
        rg_answers.visibility = View.VISIBLE
        tv_result.visibility = View.VISIBLE
        tv_answer_title.visibility = View.VISIBLE
        tv_answer.visibility = View.VISIBLE
        btn_next.visibility = View.VISIBLE
    }

}