package com.fareez.opentriviaapp.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.fareez.opentriviaapp.R
import com.fareez.opentriviaapp.network.ApiServices
import com.fareez.opentriviaapp.viewModel.QuestionViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_title.*


class TitleFragment : Fragment() {

    private lateinit var navController: NavController
    private var TAG: String = "Trivia"
    private var chosenCategory: String = ""
    private var bundle = Bundle()
    private val sharedScore = "score_pref"

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Put fragment title
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Trivia App"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_title, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get data from bundle
        val fetchedScore = arguments?.getString("score")
        Log.i(TAG, "FETCHED SCORE: ${fetchedScore.toString()}")

        // For existing user
        if(fetchedScore != null){

            // Add score to sharedPreferences
            val sharedPref: SharedPreferences = requireActivity()
                    .getSharedPreferences(sharedScore, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor =  sharedPref.edit()
            editor.putString("fetched_score", fetchedScore.toString())
            editor.apply()
        }

        // For first time user and existing user
        setupMarks()

        // access the items of the list
        val categories = resources.getStringArray(R.array.Category)
        navController = Navigation.findNavController(view)

        val spinner = sp_category
        if (spinner != null) {
            val adapter = ArrayAdapter(view.context, R.layout.spinner_item, categories)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    chosenCategory = categories[position]
                    // Add category to bundle for next fragment
                    bundle = bundleOf("recipient" to chosenCategory)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
        Log.i(TAG, "Send Category -$bundle")


        btn_start.setOnClickListener {
            navController.navigate(
                R.id.action_titleFragment_to_gameFragment,
                bundle
            )
        }

        btn_question_count.setOnClickListener {
            navController.navigate(
                    R.id.action_titleFragment_to_countFragment
            )
        }

    }

    private fun setupMarks(){

        // Try fetch data from sharedPreferences
        val sharedPref: SharedPreferences = requireActivity()
                .getSharedPreferences(sharedScore, Context.MODE_PRIVATE)
        val cachedScore = sharedPref.getString("fetched_score", null)
        Log.i("Aryan", "Cache SCOREE: $cachedScore")

        if(cachedScore != null){
            tv_main_score.text = cachedScore.toString()
        }else{
            tv_main_score.text = "0"
        }
    }
}
