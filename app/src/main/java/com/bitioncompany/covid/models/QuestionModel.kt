package com.bitioncompany.covid.models

data class QuestionModel(
    val id: Int,
    val question: String,
    val optionOne: Map<String, Int>,
//    val optionOne: String,
    val optionTwo: String,
    val optionThree: String,
)
