package com.squarestudio.jzcustomer.dataBase


import com.squarestudio.jzcustomer.Jzcustomer
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class MysqlConnect() {
    var conn: Connection
    var stmt: Statement
    init {
        Class.forName("com.mysql.jdbc.Driver")
        conn = DriverManager.getConnection(Jzcustomer.sqlr, Jzcustomer.mysqlAccount, Jzcustomer.mysqlPassword)
        stmt = conn.createStatement()
    }
    fun close() {
        conn.close()
        stmt.close()
    }
}

interface callback {
    fun callback(rs: ResultSet)
}

fun String.mySqlQuery(callback: callback){
    val a= MysqlConnect()
    val rs=a.stmt.executeQuery(this)
    while (rs.next()){
        callback.callback(rs)
    }
    a.close()
}