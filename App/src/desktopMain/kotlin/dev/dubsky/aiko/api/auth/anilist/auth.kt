package dev.dubsky.aiko.api.auth.anilist

import com.sun.net.httpserver.HttpServer
import dev.dubsky.aiko.api.auth.anilist_authUrl
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import java.awt.Desktop
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.URI

fun loadHtmlFile(): String {
    Logger.log(LogLevel.INFO, "Auth", "Loading HTML file...")
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("html/auth.html")
        ?: run {
            Logger.log(LogLevel.ERROR, "Auth", "HTML file not found in resources.")
            throw IllegalStateException("HTML file not found in resources.")
        }
    return InputStreamReader(inputStream).use { it.readText() }
}

fun startLocalServer(): String? {
    var accessToken: String? = null
    val server = HttpServer.create(InetSocketAddress(8080), 0)

    Logger.log(LogLevel.INFO, "Auth", "Starting local server on port 8080...")
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
        Logger.log(LogLevel.INFO, "Auth", "Access token received.")
    }

    server.start()
    Logger.log(LogLevel.INFO, "Auth", "Local server started. Waiting for access token...")
    while (accessToken == null) {
        Thread.sleep(100)
    }
    return accessToken
}

fun openBrowser() {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(URI(anilist_authUrl))
    } else {
        Logger.log(
            LogLevel.ERROR,
            "Auth",
            "Open Browser is not supported. Please open the following URL manually: $anilist_authUrl"
        )
    }
}

fun do_auth() {

    Logger.log(LogLevel.INFO, "Auth", "Starting anilist authentication...")
    if (ConfigManager.config.authToken != "") {
        Logger.log(LogLevel.INFO, "Auth", "Authentication not needed")
        return
    }

    openBrowser()

    val accessToken = startLocalServer()
    if (accessToken != null) {
        ConfigManager.setToken(accessToken)
        Logger.log(LogLevel.INFO, "Auth", "Authentication successful.")
    } else {
        Logger.log(LogLevel.ERROR, "Auth", "Access token is could not be retrieved.")
    }
}