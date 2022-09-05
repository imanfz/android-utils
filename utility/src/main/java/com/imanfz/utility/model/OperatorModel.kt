package com.imanfz.utility.model

import com.imanfz.utility.R

/**
 * Created by Iman Faizal on 30/Aug/2022
 **/

data class OperatorModel(
    val prefix: List<String>,
    val icon: Int
)

val baseListOperator = arrayListOf(
    OperatorModel(
        listOf(
            "0811", "0812", "0813", "0821", "0822", "0823", "0851", "0852", "0853"
        ), R.drawable.ic_telkomsel
    ),
    OperatorModel(
        listOf(
            "0814", "0815", "0816", "0855", "0856", "0857", "0858"
        ), R.drawable.ic_indosat
    ),
    OperatorModel(
        listOf(
            "0817", "0818", "0819", "0859", "0877", "0878", "0879"
        ), R.drawable.ic_xl_axiata
    ),
    OperatorModel(
        listOf(
            "0831", "0832", "0833", "0838"
        ), R.drawable.ic_axis
    ),
    OperatorModel(
        listOf(
            "0881", "0882", "0883", "0884", "0885", "0886", "0887", "0888", "0889"
        ), R.drawable.ic_smartfren
    ),
    OperatorModel(
        listOf(
            "0895", "0896", "0897", "0898", "0899"
        ), R.drawable.ic_tri
    )
)
