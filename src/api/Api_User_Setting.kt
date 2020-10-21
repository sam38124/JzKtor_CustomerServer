package com.squarestudio.jzcustomer.api

import com.squarestudio.jzcustomer.dataBase.MysqlConnect
import kotlinx.css.strong
import java.lang.Exception

class Api_User_Setting  {
    var sql = MysqlConnect()
    //插入新用戶
    fun inserUser(account: String, password: String, pick: String, head: String): String {
        try {
            val rs = sql.stmt.executeQuery("select  count(1) from jzuser.users where `account`='$account'  ")
            rs.next()
            if (rs.getString("count(1)") == "1") {
                return "user already exists"
            }
            //建立個人的通訊表
            sql.stmt.executeUpdate(
                "CREATE TABLE if not exists `jzuser`.`$account`(\n" +
                        "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `account` varchar(45) NOT NULL,\n" +
                        "  `tit` varchar(3000) NOT NULL,\n" +
                        "  `reader` int(11) NOT NULL DEFAULT '0',\n" +
                        "  `activity` varchar(45) NOT NULL DEFAULT 'myAccount',\n" +
                        "  `type` varchar(45) NOT NULL DEFAULT 'single',\n" +
                        "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  PRIMARY KEY (`id`),\n" +
                        "  UNIQUE KEY `account_UNIQUE` (`account`),\n" +
                        "  KEY `index3` (`reader`),\n" +
                        "  KEY `index4` (`type`)\n" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=default"
            )
            //建立個人的CHATROOM INDEX
            sql.stmt.executeUpdate(
                 "CREATE TABLE if not exists `jzgroupindex`.`$account`(\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `groupId` varchar(45) NOT NULL,\n" +
                    "  `auth` varchar(45) NOT NULL DEFAULT 'member',\n" +
                    "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  KEY `groupId` (`groupId`)"+
                    ") ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=default")
            //建立個人的好友名單
            sql.stmt.executeUpdate(
                "CREATE TABLE if not exists `jzfriend`.`$account`(\n" +
                        "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `account` varchar(45) NOT NULL,\n" +
                        "  `block` int(1) NOT NULL DEFAULT 0,\n" +
                        "  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  PRIMARY KEY (`id`,`account`),\n" +
                        "  KEY `account` (`account`)"+
                        ") ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=default")
            sql.stmt.executeUpdate(
                "insert into jzuser.users " +
                        "(account,password,pick,head) values" +
                        "('${account}','${password}','${pick}','${head}')"
            )
            sql.close()
            return "success"
        } catch (e: Exception) {
            e.printStackTrace()
            return "${e.message}"
        }
    }

    //用戶資料更新
    fun updateUser(account: String, password: String,changePassword:String?, pick: String?, head: String?): String {
        try {
            val rs =
                sql.stmt.executeQuery("select  count(1) from jzuser.users where `account`='${account}' and `password`='${password}' ")
            rs.next()
            if (rs.getString("count(1)") == "1") {
                if(changePassword!=null){
                    sql.stmt.executeUpdate("update jzuser.users set `password`='${changePassword}' where `account`='${account}'")
                }
                if(pick!=null){
                    sql.stmt.executeUpdate("update jzuser.users set `pick`='${pick.stringToUnicode()}' where `account`='${account}'")
                }
                if(head!=null){
                    sql.stmt.executeUpdate("update jzuser.users set `head`='$head' where `account`='${account}'")
                }
                sql.close()
                return "updateSuccess"
            } else {
                sql.close()
                return "verifyFalse"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "updateFalse"
        }
    }
}