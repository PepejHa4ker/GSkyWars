package com.pepej.gskywars.http

import com.pepej.gskywars.GSkyWars
import com.pepej.gskywars.GSkyWars.Companion.instance
import com.pepej.gskywars.http.view.ResponseError
import com.pepej.gskywars.model.Island
import com.pepej.gskywars.model.serialization.Serializer
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.users() {
    get("/users/{user}") {
        val username =
            context.parameters["user"] ?: return@get call.respond(ResponseError("Неверно указаны параметры запроса"))
        val user = instance.userManager[username]
        call.respond(user ?: ResponseError("Не удалось найти пользователя с таким именем"))

    }
}

fun Route.islands() {
    get("/islands/{id}") {
        val id = context.parameters["id"]?.toInt() ?: 0
        val island = Island.islands.find { it.id == id }
        call.respond(
            Serializer.serialize(
                island ?: return@get call.respond(ResponseError("Не удалось найти остров с таким номером"))
            ).toString()
        )

    }
}