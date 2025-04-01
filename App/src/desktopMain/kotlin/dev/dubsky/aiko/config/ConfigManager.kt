package dev.dubsky.aiko.config

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import dev.dubsky.aiko.theme.AppTheme
import java.io.File
import java.nio.file.Paths

object ConfigManager {

    private val configDir: String = when {
        System.getProperty("os.name").contains("Windows", ignoreCase = true) -> {
            Paths.get(System.getenv("AppData"), "Aiko").toString()
        }

        else -> {
            Paths.get(System.getProperty("user.home"), ".config", "Aiko").toString()
        }
    }

    private val configFile: File = File(configDir, "config.toml")

    private val toml = Toml()
    private val tomlWriter = TomlWriter()

    var config: AppConfig = loadConfig()

    private fun loadConfig(): AppConfig {
        return if (configFile.exists()) {
            toml.read(configFile).to(AppConfig::class.java)
        } else {
            AppConfig()
        }
    }

    fun isValid(): Boolean {
        return config.api.isNotEmpty() && config.proxy.isNotEmpty() && config.refer.isNotEmpty()
    }

    fun setProxy(proxy: String) {
        config.proxy = proxy
        saveConfig()
    }

    fun setRefer(refer: String) {
        config.refer = refer
        saveConfig()
    }

    fun setAutoUpdate(autoUpdate: Boolean) {
        config.autoUpdate = autoUpdate
        saveConfig()
    }

    fun setApi(api: String) {
        config.api = api
        saveConfig()
    }

    fun setLogging(logging: Boolean) {
        config.logging = logging
        saveConfig()
    }

    fun setTheme(theme: AppTheme) {
        config.theme = theme
        saveConfig()
    }

    fun setResolution(resolution: String) {
        config.resolution = resolution
        saveConfig()
    }

    fun setToken(token: String) {
        config.authToken = token
        saveConfig()
    }

    fun setUser(name: String) {
        config.userName = name
        saveConfig()
    }

    fun saveConfig() {
        if (!configFile.parentFile.exists()) {
            configFile.parentFile.mkdirs()
        }
        tomlWriter.write(config, configFile)
    }
}