package com.cuinsolutions.macrosmanager

import java.util.regex.Pattern

/**
 * Created by mykalcuin on 10/10/17.
 */

class Regexs {

    fun validNumber(number: String): Boolean {

        val numberRegex = Regex("^[0-9]+(\\.[0-9]{1,2})?$")

        return numberRegex.matches(number)
    }

    fun validEmail(email: String): Boolean {

        return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
}

