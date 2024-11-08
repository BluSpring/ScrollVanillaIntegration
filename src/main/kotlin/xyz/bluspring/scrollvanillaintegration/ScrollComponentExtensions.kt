package xyz.bluspring.scrollvanillaintegration

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import io.github.dockyardmc.scroll.Components
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
    val json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow(::JsonParseException)

    if (json.isJsonPrimitive) {
        return ScrollComponent(text = json.asString)
    } else if (json.isJsonArray) {
        return recursiveLoadScrollComponents(json.asJsonArray)
    }

    return JsonToComponentSerializer.serialize(GSON.toJson(json))
}

private fun recursiveLoadScrollComponents(json: JsonArray): ScrollComponent {
    val components = mutableListOf<ScrollComponent>()

    for (element in json) {
        if (element.isJsonPrimitive) {
            components.add(ScrollComponent(text = element.asString))
        } else if (element.isJsonArray) {
            components.add(recursiveLoadScrollComponents(element.asJsonArray))
        } else {
            components.add(JsonToComponentSerializer.serialize(GSON.toJson(element)))
        }
    }

    return Components.new(components)
}