package com.pepej.gskywars.database

import com.pepej.gskywars.GSkyWars.Companion.instance
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.SqlObjectPlugin


class DatabaseManager {

    val jdbi: Jdbi
    val hikari: HikariDataSource


    init {
        val hConfig = HikariConfig()
        val config = instance.config.rootConfigNode.getNode("mysql")
        hConfig.poolName = "GSkyWars MySQL Connection Pool"
        hConfig.dataSourceClassName = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
        hConfig.addDataSourceProperty("serverName", config.getNode("host").string)
        hConfig.addDataSourceProperty("port",config.getNode("port").int)
        hConfig.addDataSourceProperty("databaseName", config.getNode("database").string)
        hConfig.addDataSourceProperty("user", config.getNode("user").string)
        hConfig.addDataSourceProperty("password", config.getNode("password").string)
        hConfig.addDataSourceProperty("useSSL", config.getNode("useSSL").boolean)
        hikari = HikariDataSource(hConfig)
        jdbi = Jdbi.create(hikari)
        jdbi.installPlugin(SqlObjectPlugin())

    }
}