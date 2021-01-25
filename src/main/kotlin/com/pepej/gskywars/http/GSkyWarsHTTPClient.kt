package com.pepej.gskywars.http

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

class GSkyWarsHTTPClient {

    fun launch(port: Int): ApplicationEngine = embeddedServer(Netty, port) {
            install(Routing) {
                users()
                islands()
            }
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }

        }.start(false)

}