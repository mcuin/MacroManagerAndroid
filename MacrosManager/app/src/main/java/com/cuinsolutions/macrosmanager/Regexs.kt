package com.cuinsolutions.macrosmanager

/**
 * Created by mykalcuin on 10/10/17.
 */

class Regexs {

    fun validNumber(number: String): Boolean {

        val numberRegex = Regex("^[0-9]+(\\.[0-9]{1,2})?$")

        return numberRegex.matches(number)
    }
}

