package com.squarestudio.jzcustomer


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squarestudio.jzcustomer.api.Api_Customer_Message
import com.squarestudio.jzcustomer.api.Api_User_Setting
import com.squarestudio.jzcustomer.dataBase.MysqlConnect
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.engine.apache.Apache
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.client.HttpClient
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respondOutputStream
import io.ktor.routing.get
import io.ktor.sessions.sessions
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URLEncoder

object Jzcustomer {
    var sqlr: String = ""
    var mysqlAccount = ""
    var mysqlPassword = ""

    //    var dataRoot = "jdbc:mysql://180.177.242.55:3306?autoReconnect=false&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
    class Auth(var admin: String, var password: String)

    fun run(rout: Routing, sqlrout: String, mysqlAccount: String, mysqlPassword: String) {
        this.mysqlAccount = mysqlAccount
        this.mysqlPassword = mysqlPassword
        sqlr = sqlrout
        rout {
            HttpClient(Apache) {
                install(Sessions) {
                    cookie<Auth>("auth")
                }
            }
            //登入取得token
            post("/jzApi/login") {
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                val myAccount = map["myAccount"]!!
                val password = map["password"]!!
                if (Api_Customer_Message(myAccount, password).vertifyUser()) {
                    call.sessions.set("auth", Auth(myAccount, password))
                    call.respondText("success")
                } else {
                    call.respondText("false")
                }
            }
            //插入新用戶
            post("/jzApi/insertUser") {
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                if (map["account"]!! == "CustomerService") {
                    call.respondText("User already exists")
                } else {
                    call.respondText(
                        Api_User_Setting().inserUser(map["account"]!!, map["password"]!!, map["pick"]!!, map["head"]!!)
                        , contentType = ContentType.Text.Plain
                    )
                }
            }
            //用戶資料更新
            post("/jzApi/updateUser") {
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                call.respondText(
                    Api_User_Setting().updateUser(
                        map["account"]!!,
                        map["password"]!!,
                        map["changePassword"],
                        map["pick"],
                        map["head"]
                    ), contentType = ContentType.Text.Plain
                )
            }
            //查看訊息降冪排序
            post("/jzApi/getUserMessage") {
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                val myAccount = map["myAccount"]!!
                val password = map["password"]!!
                if(map["chatID"] != null){
                    call.respondText(
                        Api_Customer_Message(myAccount, password).getGroupMessage(
                            map["id"]!!,
                            map["chatID"]!!,
                            map["unicode"] == "true"
                        ),
                        contentType = ContentType.Text.Plain
                    )
                }else{
                    call.respondText(
                        Api_Customer_Message(myAccount, password).getMessage(
                            map["id"]!!,
                            map["toAccount"]!!,
                            map["unicode"] == "true"
                        ),
                        contentType = ContentType.Text.Plain
                    )
                }
            }
            //建立聊天室
            post ("/jzApi/createChatRoom"){
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                val myAccount = map["myAccount"]!!
                val password = map["password"]!!
                val chatName = map["chatName"]!!
                call.respondText(
                    Api_Customer_Message(myAccount, password).createGroupTable(chatName),
                    contentType = ContentType.Text.Plain
                )
            }
            //查看新訊息
            post("/jzApi/getTopMessage") {
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                val myAccount = map["myAccount"]!!
                val password = map["password"]!!
                if(map["chatID"] != null){
                    call.respondText(
                        Api_Customer_Message(myAccount, password).getGroupTopMessage(
                            map["id"]!!,
                            map["chatID"]!!,
                            map["unicode"] == "true"
                        ),
                        contentType = ContentType.Text.Plain
                    )
                }else{
                    call.respondText(
                        Api_Customer_Message(myAccount, password).getTopMessage(
                            map["id"]!!,
                            map["toAccount"]!!,
                            map["unicode"] == "true"
                        ),
                        contentType = ContentType.Text.Plain
                    )
                }
            }
            //插入新訊息
            post("/jzApi/InsertMessage") {
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                val myAccount = map["myAccount"]!!
                val password = map["password"]!!
                val message = map["message"]!!
                val file = map["file"] ?: "nodata"
                if(map["chatID"] != null){
                    call.respondText(
                        Api_Customer_Message(myAccount, password).insertGroupMessage(map["chatID"]!!, message, file),
                        contentType = ContentType.Text.Plain
                    )
                }else{
                    call.respondText(
                        Api_Customer_Message(myAccount, password).insertMessage(map["toAccount"]!!, message, file),
                        contentType = ContentType.Text.Plain
                    )
                }

            }
            //插入新訊息WithTable
            post("/jzApi/InsertMessageWithOther") {
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                val myAccount = map["myAccount"]!!
                val password = map["password"]!!
                val message = map["message"]!!
                val file = map["file"] ?: "nodata"
                call.respondText(
                    Api_Customer_Message(myAccount, password).insertMessage(map["toAccount"]!!, message, file,map["otherAccount"]!!),
                    contentType = ContentType.Text.Plain
                )
            }
            //上傳檔案
            post("/jzApi/UploadFile") {
                val sessions = call.sessions.get("auth") as Auth
                if (Api_Customer_Message(sessions.admin, sessions.password).vertifyUser()) {
                    call.respondText(
                        Api_Customer_Message(sessions.admin, sessions.password).uploadFile(call.receive()),
                        contentType = ContentType.Text.Plain
                    )
                } else {
                    call.respondText("false")
                }
            }
            //添加好友
            post("/jzApi/addFriend"){
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                val myAccount = map["myAccount"]!!
                val password = map["password"]!!
                val toAccount = map["toAccount"]!!
                call.respondText(Api_Customer_Message(myAccount, password).addFriend(toAccount))
            }
            //取得好友
            post("/jzApi/getFriend"){
                val map: MutableMap<String, String> =
                    Gson().fromJson(call.receiveText(), object : TypeToken<MutableMap<String, String>>() {}.type)
                val myAccount = map["myAccount"]!!
                val password = map["password"]!!
                val id = map["id"]!!
                call.respondText(Api_Customer_Message(myAccount, password).getFriend(id))
            }

            //取得檔案
            get("/getFile") {
                val filename = call.parameters["path"]
                val file = File("$filename")
                if (file.exists()) { //檢驗檔案是否存在
                    try {
                        call.response.header(
                            "Content-Disposition",
                            "attachment;filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\""
                        )
                        call.respondOutputStream(contentType = ContentType.Text.Plain, status = HttpStatusCode.OK,
                            producer = {
                                val output: OutputStream = this
                                val `in`: InputStream = FileInputStream(file)
                                val b = ByteArray(2048)
                                var len: Int
                                while (`in`.read(b).also { len = it } > 0) {
                                    output.write(b, 0, len)
                                }
                                `in`.close()
                                output.flush()
                                output.close() //關閉串流
                            })
                    } catch (ex: Exception) {
                        call.respondText("false" + ex.printStackTrace())
                    }
                } else {
                    call.respondText("false no file")
                }
            }
            val sql = MysqlConnect()
            sql.stmt.executeUpdate("CREATE DATABASE if not exists `jzcustomer`")
            sql.stmt.executeUpdate("CREATE DATABASE if not exists `jzuser`")
            sql.stmt.executeUpdate("CREATE DATABASE if not exists `jzchatroom`")
            sql.stmt.executeUpdate("CREATE DATABASE if not exists `jzgroupindex`")
            sql.stmt.executeUpdate("CREATE DATABASE if not exists `jzgrouptable`")
            sql.stmt.executeUpdate("CREATE DATABASE if not exists `jzfriend`")
            //建立用戶資料表
            sql.stmt.executeUpdate(
                "CREATE TABLE if not exists `jzuser`.`users`  (\n" +
                        "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `account` varchar(500) NOT NULL,\n" +
                        "  `password` varchar(500) NOT NULL,\n" +
                        "  `pick` varchar(500) NOT NULL DEFAULT 'NA',\n" +
                        "  `head` varchar(500) NOT NULL DEFAULT 'NA',\n" +
                        "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  `creatDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  PRIMARY KEY (`id`,`account`),\n" +
                        "  KEY `index4` (`time`)\n" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=59688 DEFAULT CHARSET=utf8mb4 COLLATE=default"
            )
            sql.stmt.executeUpdate(
                "CREATE TABLE if not exists `jzuser`.`grouptables`  (\n" +
                        "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `groupid` varchar(500) NOT NULL,\n" +
                        "  `pick` varchar(500) NOT NULL,\n" +
                        "  `head` varchar(500) NOT NULL DEFAULT 'NA',\n" +
                        "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  `creatDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  PRIMARY KEY (`id`,`groupid`),\n" +
                        "  KEY `index4` (`time`)\n" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=59688 DEFAULT CHARSET=utf8mb4 COLLATE=default"
            )

            sql.close()
        }
    }
}