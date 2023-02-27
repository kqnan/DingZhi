package me.kqn.gunbinder

import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

object ConfigFile {
    @Config(autoReload = true)
    lateinit var config: Configuration
}