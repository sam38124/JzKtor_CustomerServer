package com.squarestudio.jzcustomer

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object extension{
    fun getText(tempurl: String, timeout: Int, method: String,data:String="",dataArray: ByteArray?): String? {
        try {
            val url =tempurl
            val conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = timeout
            conn.requestMethod = method.toUpperCase()
            if (method.toUpperCase() == "POST" ) {
                conn.doOutput = true;
            }
            conn.doInput = true;
            if (method.toUpperCase() == "POST") {
                if(dataArray !=null){
                    val wr = DataOutputStream(conn.outputStream)
                    wr.write(dataArray)
                    wr.flush()
                    wr.close()
                }else{
                    val wr = DataOutputStream(conn.outputStream)
                    wr.writeBytes(data)
                    wr.flush()
                    wr.close()
                }
            }
            val reader = BufferedReader(InputStreamReader(conn.inputStream, "utf-8"))
            var line: String? = null
            val strBuf = StringBuffer()
            line = reader.readLine()
            while (line != null) {
                strBuf.append(line)
                line = reader.readLine()
            }
            return strBuf.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
