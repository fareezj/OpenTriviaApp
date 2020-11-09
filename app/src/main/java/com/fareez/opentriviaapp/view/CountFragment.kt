package com.fareez.opentriviaapp.view

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fareez.opentriviaapp.R
import com.fareez.opentriviaapp.model.QuestionCountModel
import com.fareez.opentriviaapp.viewModel.QuestionViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_count.*


class CountFragment : Fragment() {

    private val subscriptions = CompositeDisposable()
    private lateinit var viewModel: QuestionViewModel
    private var chosenCategory: String = ""
    private lateinit var catCode: QuestionCountModel.CategoryDetails

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //Init viewModel
        viewModel = ViewModelProvider(this).get(QuestionViewModel::class.java)

        // Put fragment title
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Questions Count"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_count, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = resources.getStringArray(R.array.Category)
        val spinner = sp_category_count
        if (spinner != null) {
            val adapter = ArrayAdapter(view.context, R.layout.spinner_item, categories)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?, position: Int, id: Long
                ) {
                    chosenCategory = categories[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        btn_start_count.setOnClickListener {

            cv_result_count.visibility = View.GONE
            pb_count.visibility = View.VISIBLE
            requestQuestionCount()
        }


    }

    private fun requestQuestionCount(){
        val subscribe = viewModel.getQuestionCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    // Populate fetched data to the UI
                    val data = it.categories

                    when (chosenCategory) {
                        "Sports" -> catCode = data?.categoryCode21!!
                        "Geography" -> catCode = data?.categoryCode22!!
                        "History" -> catCode = data?.categoryCode23!!
                        "Art" -> catCode = data?.categoryCode25!!
                        "Celebrities" -> catCode = data?.categoryCode26!!
                        "Animals" -> catCode = data?.categoryCode27!!
                        "Vehicles" -> catCode = data?.categoryCode28!!
                    }

                    pb_count.visibility = View.GONE
                    cv_result_count.visibility = View.VISIBLE

                    tv_category_count.text = chosenCategory
                    tv_total_questions.text = catCode.totalQuestion.toString()
                    tv_pending_questions.text = catCode.totalPendingQuestion.toString()
                    tv_verified_questions.text = catCode.totalVerifiedQuestion.toString()
                    tv_rejected_questions.text = catCode.totalRejectedQuestion.toString()

                }, { err ->
                    var msg = err.localizedMessage

                })
        subscriptions.add(subscribe)
    }


}