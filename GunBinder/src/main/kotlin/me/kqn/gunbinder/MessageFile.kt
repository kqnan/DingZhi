package me.kqn.gunbinder

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

object MessageFile {
    @Config(autoReload = true, value = "message.yml")
    lateinit var message:Configuration
    @ConfigNode(value="NotOwner",bind="message.yml")
    lateinit var notowner:String
}