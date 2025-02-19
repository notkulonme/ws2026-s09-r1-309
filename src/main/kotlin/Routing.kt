package hu.notkulonme

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File


fun Application.configureRouting() {

    val assets = environment.config.property("api.assets").getString()

    routing {
        get("/") {
            call.respondFile(File("${assets}index.html"))
        }
        get("{file}"){
            val responseFile = File("${assets}${call.parameters["file"]}")
            if (!responseFile.exists())
                throw NotFoundException("File was not found")
            else
                call.respondFile(responseFile)
        }

    }
}
