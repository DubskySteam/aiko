package dev.dubsky.aiko.config

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
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

    fun setMode(mode: String) {
        config.Mode = mode
    }

    fun saveConfig() {
        if (!configFile.parentFile.exists()) {
            configFile.parentFile.mkdirs()
        }
        tomlWriter.write(config, configFile)
    }
}