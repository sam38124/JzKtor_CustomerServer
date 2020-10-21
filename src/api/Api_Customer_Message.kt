package com.squarestudio.jzcustomer.api

import com.google.gson.Gson
import com.squarestudio.jzcustomer.dataBase.MysqlConnect
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.toTypedArray


class Api_Customer_Message(var myAccount: String, var password: String) {
    var sql = MysqlConnect()

    //資料表名稱
    var tableName: String = ""

    //通訊Model
    inner class message(
        var id: String,
        var admin: String,
        var message: String,
        var file: String,
        var time: String,
        var head: String,
        var pick: String
    )

    //取得一對一最新Message
    fun getTopMessage(id: String, toAccount: String, unicode: Boolean = false): String {
        if (!vertifyUser()) {
            return "verification failed"
        }
        try {
            checkTable(toAccount)
            var last = ""
            if (id != "-1") {
                last = "and `jzcustomer`.`$tableName`.id>$id"
            }
            val rs = sql.stmt.executeQuery(
                "select `jzcustomer`.`$tableName`.*,`jzuser`.`users`.* from `jzcustomer`.`$tableName`,`jzuser`.`users`" +
                        "where `jzuser`.`users`.`account`=`jzcustomer`.`$tableName`.`account` $last order by `jzcustomer`.`$tableName`.id desc limit 0,50"
            )
            val daraArray = ArrayList<message>()
            while (rs.next()) {
                daraArray.add(
                    message(
                        rs.getString("id"),
                        rs.getString("account"),
                        if (unicode) rs.getString("message").unicodeToString()!! else rs.getString("message")
                        ,
                        rs.getString("file"),
                        rs.getString("time")
                        ,
                        rs.getString("head"),
                        rs.getString("pick")
                    )
                )
            }
            val a = Gson().toJson(daraArray)
            sql.close()
            return a
        } catch (e: Exception) {
            e.printStackTrace()
            return "error:${e.message}"
        }
    }

    //取得聊天室最新Message
    fun getGroupTopMessage(id: String, chatID: String, unicode: Boolean = false): String {
        if (!vertifyUser()) {
            return "verification failed"
        }
        try {
            var last = ""
            if (id != "-1") {
                last = "and `jzcustomer`.`$tableName`.id>$id"
            }
            val rs = sql.stmt.executeQuery(
                "select `jzcustomer`.`$tableName`.*,`jzuser`.`users`.* from `jzcustomer`.`$tableName`,`jzuser`.`users`" +
                        "where `jzuser`.`users`.`account`=`jzcustomer`.`$tableName`.`account` $last order by `jzcustomer`.`$tableName`.id desc limit 0,50"
            )
            val daraArray = ArrayList<message>()
            while (rs.next()) {
                daraArray.add(
                    message(
                        rs.getString("id"),
                        rs.getString("account"),
                        if (unicode) rs.getString("message").unicodeToString()!! else rs.getString("message")
                        ,
                        rs.getString("file"),
                        rs.getString("time")
                        ,
                        rs.getString("head"),
                        rs.getString("pick")
                    )
                )
            }
            val a = Gson().toJson(daraArray)
            sql.close()
            return a
        } catch (e: Exception) {
            e.printStackTrace()
            return "error:${e.message}"
        }
    }

    //取得一對一聊天訊息
    fun getMessage(id: String, toAccount: String, unicode: Boolean = false): String {
        if (!vertifyUser()) {
            return "verification failed"
        }
        try {
            checkTable(toAccount)
            var last = ""
            if (id != "-1") {
                last = "and `jzcustomer`.`$tableName`.id<$id"
            }
            val rs = sql.stmt.executeQuery(
                "select `jzcustomer`.`$tableName`.*,`jzuser`.`users`.* from `jzcustomer`.`$tableName`,`jzuser`.`users`" +
                        "where `jzuser`.`users`.`account`=`jzcustomer`.`$tableName`.`account` $last order by `jzcustomer`.`$tableName`.id desc limit 0,50"
            )
            val daraArray = ArrayList<message>()
            while (rs.next()) {
                daraArray.add(
                    message(
                        rs.getString("id"),
                        rs.getString("account"),
                        if (unicode) rs.getString("message").unicodeToString()!! else rs.getString("message")
                        ,
                        rs.getString("file"),
                        rs.getString("time")
                        ,
                        rs.getString("head"),
                        rs.getString("pick")
                    )
                )
            }
            val a = Gson().toJson(daraArray)
            sql.close()
            return a
        } catch (e: Exception) {
            e.printStackTrace()
            return "error:${e.message}"
        }
    }

    //取得聊天室訊息
    fun getGroupMessage(id: String, chatID: String, unicode: Boolean = false): String {
        if (!vertifyUser()) {
            return "verification failed"
        }
        try {
            var last = ""
            if (id != "-1") {
                last = "and `jzcustomer`.`$tableName`.id<$id"
            }
            val rs = sql.stmt.executeQuery(
                "select `jzcustomer`.`$tableName`.*,`jzuser`.`users`.* from `jzcustomer`.`$tableName`,`jzuser`.`users`" +
                        "where `jzuser`.`users`.`account`=`jzcustomer`.`$tableName`.`account` $last order by `jzcustomer`.`$tableName`.id desc limit 0,50"
            )
            val daraArray = ArrayList<message>()
            while (rs.next()) {
                daraArray.add(
                    message(
                        rs.getString("id"),
                        rs.getString("account"),
                        if (unicode) rs.getString("message").unicodeToString()!! else rs.getString("message")
                        ,
                        rs.getString("file"),
                        rs.getString("time")
                        ,
                        rs.getString("head"),
                        rs.getString("pick")
                    )
                )
            }
            val a = Gson().toJson(daraArray)
            sql.close()
            return a
        } catch (e: Exception) {
            e.printStackTrace()
            return "error:${e.message}"
        }
    }

    //插入一對一聊天訊息
    fun insertMessage(toAccount: String, message: String, file: String, otherAccount: String? = null): String {
        if (!vertifyUser()) {
            return "verification failed"
        }
        return try {
            checkTable(toAccount)
            sql.stmt.executeUpdate("insert into `jzcustomer`.`$tableName` (account,message,file) values ('${otherAccount ?: myAccount}','$message','$file')")
            sql.stmt.executeUpdate("insert ignore into `jzuser`.`$myAccount` (account,tit,reader,activity) values ('$toAccount','$message','1','myAccount')")
            sql.stmt.executeUpdate("insert ignore into `jzuser`.`$toAccount` (account,tit,reader,activity) values ('$myAccount','$message','0','toAccount')")
            sql.stmt.executeUpdate("update `jzuser`.`$myAccount` set `time` = CURRENT_TIMESTAMP where account='$toAccount'");
            sql.stmt.executeUpdate("update `jzuser`.`$myAccount` set `reader` = 1 where account='$toAccount'");
            sql.stmt.executeUpdate("update `jzuser`.`$myAccount` set `activity` = 'myAccount' where account='$toAccount'");
            sql.stmt.executeUpdate("update `jzuser`.`$myAccount` set `tit` = '$message' where account='$toAccount'");
            sql.stmt.executeUpdate("update `jzuser`.`$toAccount` set `time` = CURRENT_TIMESTAMP where account='$myAccount'");
            sql.stmt.executeUpdate("update `jzuser`.`$toAccount` set `reader` = 0 where account='$myAccount'");
            sql.stmt.executeUpdate("update `jzuser`.`$toAccount` set `activity` = 'toAccount' where account='$myAccount'");
            sql.stmt.executeUpdate("update `jzuser`.`$toAccount` set `tit` = '$message' where account='$myAccount'");
            sql.close()
            "success"
        } catch (e: Exception) {
            e.printStackTrace()
            "error:${e.message}"
        }
    }

    //添加好友
    fun addFriend(toAccount: String): String {
        if (!vertifyUser()) {
            return "verification failed"
        }
        return try {
            sql.stmt.executeUpdate("insert ignore into `jzfriend`.`$myAccount` (account,block) values ('$toAccount','0')")
            sql.close()
            "success"
        } catch (e: Exception) {
            e.printStackTrace()
            "error:${e.message}"
        }
    }

    inner class myFriend(var id: String, var admin: String, var block: String, time: String,var head:String,var pick:String)

    //取得好友
    fun getFriend( id: String): String{
        val item = ArrayList<myFriend>()
        if (!vertifyUser()) {
            return "verification failed"
        }
        val rs = sql.stmt.executeQuery(
            if (id == "-1")
                "select `jzuser`.`users`.*,`jzfriend`.`$myAccount`.* from `jzfriend`.`$myAccount`,`jzuser`.`users` where `jzuser`.`users`.account=`jzfriend`.`$myAccount`.account  order by `jzuser`.`users`.id desc limit 0,50"
            else
                "select `jzuser`.`users`.*,`jzfriend`.`$myAccount`.* from `jzfriend`.`$myAccount`,`jzuser`.`users` where `jzuser`.`users`.account=`jzfriend`.`$myAccount`.account and `jzuser`.`users`.id<$id order by `jzuser`.`users`.id desc limit 0,50"
        )
        while (rs.next()) {
            item.add(myFriend(rs.getString("id"), rs.getString("account"), rs.getString("block"), rs.getString("time"),rs.getString("head"),rs.getString("pick")))
        }
        return Gson().toJson(item)
    }

    //插入聊天室訊息
    fun insertGroupMessage(chatID: String, message: String, file: String): String {
        if (!vertifyUser()) {
            return "verification failed"
        }
        return try {
            sql.stmt.executeUpdate("insert into `jzcustomer`.`$tableName` (account,message,file) values ('$myAccount','$message','$file')")
            sql.close()
            "success"
        } catch (e: Exception) {
            e.printStackTrace()
            "error:${e.message}"
        }
    }

    //驗證用戶
    fun vertifyUser(): Boolean {
        val rs =
            sql.stmt.executeQuery("select  count(1) from jzuser.users where `account`='${myAccount}' and `password`='${password}' ")
        rs.next()
        return rs.getString("count(1)") == "1"
    }

    //上傳圖片
    fun uploadFile(image: ByteArray): String {
        if (!vertifyUser()) {
            return "verification failed"
        }
        return try {
            val fil = File("imageFile")
            fil.mkdir()
            val name =
                "_${SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss").format(Date())}".replace(" ", "").replace("-", "")
                    .replace(":", "_")
            val file = File("imageFile/$name")
            val output = FileOutputStream(file)
            output.write(image)
            output.close()
            "imageFile/$name"
        } catch (e: Exception) {
            e.printStackTrace()
            "false"
        }
    }

    //建立一對一聊天室
    fun checkTable(toAccount: String) {
        val input = arrayOf(myAccount, toAccount!!)
        val list = listOf(*input)
        Collections.sort(list)
        val output = list.toTypedArray()
        this.tableName = "${output[0]}_${output[1]}"
        //建立對方的通訊表
        sql.stmt.executeUpdate(
            "CREATE TABLE if not exists `jzuser`.`$toAccount`(\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `account` varchar(45) NOT NULL,\n" +
                    "  `tit` varchar(3000) NOT NULL,\n" +
                    "  `reader` int(11) NOT NULL DEFAULT '0',\n" +
                    "  `activity` varchar(45) NOT NULL DEFAULT 'myAccount',\n" +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  UNIQUE KEY `account_UNIQUE` (`account`),\n" +
                    "  KEY `index3` (`reader`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=default"
        )
        //建立共同的通訊資料表
        sql.stmt.executeUpdate(
            "CREATE TABLE if not exists `jzcustomer`.`${this.tableName}` (\n" +
                    "        `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "        `account` varchar(45) NOT NULL,\n" +
                    "        `file` varchar(500) NOT NULL DEFAULT 'nodata',\n" +
                    "        `message` varchar(5000) NOT NULL,\n" +
                    "        `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "        PRIMARY KEY (`id`)\n" +
                    "        ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=default"
        )
    }

    //加入聊天室
    fun joinChatRoom(chatID: String, auth: String) {
        sql.stmt.executeUpdate("insert ignore into `jzchatroom`.`$chatID` (account) values ('$myAccount')")
        sql.stmt.executeUpdate("insert ignore into `jzgroupindex`.`$myAccount` (groupid,auth) values ('$chatID','$auth')")
    }

    //建立多人聊天室
    fun createGroupTable(pick: String): String {
        this.tableName = SimpleDateFormat("YYYY-MM-DD HH:MM:SS:ssss").format(Date())
        //建立共同的通訊資料表
        sql.stmt.executeUpdate(
            "CREATE TABLE if not exists `jzgrouptable`.`${this.tableName}` (\n" +
                    "        `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "        `account` varchar(45) NOT NULL,\n" +
                    "        `file` varchar(500) NOT NULL DEFAULT 'nodata',\n" +
                    "        `message` varchar(5000) NOT NULL,\n" +
                    "        `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "        PRIMARY KEY (`id`)\n" +
                    "        ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=default"
        )
        //建立聊天室資料表關聯人物
        sql.stmt.executeUpdate(
            "CREATE TABLE if not exists `jzchatroom`.`${this.tableName}`  (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `account` varchar(500) NOT NULL,\n" +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "  PRIMARY KEY (`id`,`account`),\n" +
                    "  KEY `index4` (`time`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=59688 DEFAULT CHARSET=utf8mb4 COLLATE=default"
        )
        //建立聊天室
        sql.stmt.executeUpdate("insert into jzuser.grouptables (groupid,pick) values ('$tableName','$pick')")
        //加入聊天室
        joinChatRoom(this.tableName, "manger")
        return this.tableName
    }


}

//将unicode的汉字码转换成utf-8格式的汉字
fun String.unicodeToString(): String? {
    val string = StringBuffer()
    val hex = this.replace("\\\\u", "\\u").split("\\u").toTypedArray()
    for (i in 1 until hex.size) { //        System.out.println(hex[i].length());
        if (hex[i].length > 4) {
            string.append(hex[i].substring(4))
        }
        val data = hex[i].substring(0, 4).toInt(16)
        // 追加成string
        string.append(data.toChar())
    }
    return if (hex.size <= 1) this else string.toString()
}

//将utf-8的汉字转换成unicode格式汉字码
fun String.stringToUnicode(): String? {
    var str = this
    str = str ?: ""
    var tmp: String
    val sb = StringBuffer(1000)
    var c: Char
    var i: Int
    var j: Int
    sb.setLength(0)
    i = 0
    while (i < str.length) {
        c = str[i]
        sb.append("\\\\u")
        j = c.toInt() ushr 8 //取出高8位
        tmp = Integer.toHexString(j)
        if (tmp.length == 1) sb.append("0")
        sb.append(tmp)
        j = c.toInt() and 0xFF //取出低8位
        tmp = Integer.toHexString(j)
        if (tmp.length == 1) sb.append("0")
        sb.append(tmp)
        i++
    }
    return String(sb)
}