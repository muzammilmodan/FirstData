package com.googlemapwithdrawpathapp.polilineAnimator

import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapHttpConnection {
    @Throws(IOException::class)
    fun readUrl(mapsApiDirectionsUrl: String): String {
        var data = ""
        var istream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(mapsApiDirectionsUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            istream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(istream!!))
            val sb = StringBuffer()
            var line = ""
            //MM Closed....
            /*while ((line = br.readLine()) != null) {
                sb.append(line)
            }*/
            data = sb.toString()
            br.close()


        } catch (e: Exception) {
            Log.d("Exception while reading url", e.toString())
        } finally {
            istream!!.close()
            urlConnection!!.disconnect()
        }
        return data

    }
}
