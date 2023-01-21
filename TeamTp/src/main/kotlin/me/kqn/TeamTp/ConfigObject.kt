package me.kqn.TeamTp

import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import java.io.File

object ConfigObject {
    @Config(autoReload = true)
    lateinit var config:Configuration

    fun save(){
        config.saveToFile(File("plugins/TeamTp/config.yml"))
    }
}