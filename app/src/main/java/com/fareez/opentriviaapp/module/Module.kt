package com.fareez.opentriviaapp.module

import com.fareez.opentriviaapp.viewModel.QuestionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module{

}

val viewModelModule = module {

    //Scoping
    viewModel {  QuestionViewModel(get()) }
}