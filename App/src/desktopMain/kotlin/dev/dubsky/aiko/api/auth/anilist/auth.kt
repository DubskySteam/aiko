package dev.dubsky.aiko.api.auth.anilist

import com.sun.net.httpserver.HttpServer
import dev.dubsky.aiko.api.auth.anilist_authUrl
import dev.dubsky.aiko.config.ConfigManager
import java.awt.Desktop
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.URI

fun loadHtmlFile(): String {
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("html/auth.html")
        ?: throw IllegalStateException("HTML file not found in resources.")
    return InputStreamReader(inputStream).use { it.readText() }
}

fun startLocalServer(): String? {
    var accessToken: String? = null
    val server = HttpServer.create(InetSocketAddress(8080), 0)

    server.createContext("/anilist_callback") { exchange ->
        val htmlContent = loadHtmlFile().toByteArray()
        exchange.sendResponseHeaders(200, htmlContent.size.toLong())
        exchange.responseBody.use { it.write(htmlContent) }
    }

    server.createContext("/capture") { exchange ->
        val query = exchange.requestURI.query
        accessToken = query.substringAfter("access_token=", "")

        val response = "Token received. You can close this window now."
        exchange.sendResponseHeaders(200, response.length.toLong())
        OutputStreamWriter(exchange.responseBody).use { it.write(response) }
        server.stop(1)
    }

    server.start()
    println("Local server started. Waiting for access token...")
    while (accessToken == null) {
        Thread.sleep(100)
    }
    return accessToken
}

fun openBrowser() {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(URI(anilist_authUrl))
    } else {
        println("Desktop is not supported. Please open the following URL manually: $anilist_authUrl")
    }
}

fun do_auth() {

    if (ConfigManager.config.token != "") {
        println("User is already logged in.")
        return
    }

    openBrowser()

    val accessToken = startLocalServer()
    if (accessToken != null) {
        ConfigManager.setToken(accessToken)
        println("Access token received: $accessToken")
    } else {
        println("Failed to retrieve access token.")
    }
}