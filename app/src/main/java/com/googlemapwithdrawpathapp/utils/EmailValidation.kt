package com.QuickHelpVendor.Utils

import java.util.regex.Matcher
import java.util.regex.Pattern

object  EmailValidation {
    private val VALID_EMAIL =
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-].+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    fun checkEmailIsCorrect(strEmail: String): Boolean {
        //		try {
        val isvalid: Boolean
        val pattern = Pattern.compile(VALID_EMAIL)
        val matcher = pattern.matcher(strEmail)
        isvalid = matcher.matches()
        return isvalid
    }

    fun isValidUrl(txtWebsite: String): Boolean {
        val regex = Pattern.compile("^[a-zA-Z0-9\\-\\.]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)$")
        val matcher = regex.matcher(txtWebsite)
        return matcher.matches()
    }

    fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()

    }
}