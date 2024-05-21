package xyz.bluspring.scrollvanillaintegration

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import io.github.dockyardmc.scroll.serializers.JsonToComponentSerializer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import io.github.dockyardmc.scroll.Component as ScrollComponent

private val GSON = GsonBuilder()
    .disableHtmlEscaping()
    .create()

fun ScrollComponent.toVanilla(): Component {
    return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(this.toJson())).getOrThrow(::JsonParseException)
}

fun Component.toScroll(): ScrollComponent {
    return JsonToComponentSerializer.serialize(GSON.toJson(ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow(::JsonParseException)))
}