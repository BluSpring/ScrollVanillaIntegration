package xyz.bluspring.scrollvanillaintegration

import com.google.gson.*
import com.mojang.serialization.JsonOps
import io.github.dockyardmc.scroll.*
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import java.util.*
import io.github.dockyardmc.scroll.Component as ScrollComponent

private val GSON = GsonBuilder()
    .disableHtmlEscaping()
    .create()

fun ScrollComponent.toVanilla(): Component {
    return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(this.toJson())).getOrThrow(::JsonParseException)
}

fun Component.toScroll(): ScrollComponent {
    val json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow(::JsonParseException)
    return json.asScrollComponent()
}

private fun JsonElement.asScrollComponent(): ScrollComponent {
    return if (this.isJsonPrimitive)
        ScrollComponent(text = this.asString)
    else if (this.isJsonArray) {
        recursiveLoadScrollComponents(this.asJsonArray)
    } else {
        val json = this.asJsonObject

        ScrollComponent(
            extra = json.getOrNull("extra")?.asJsonArray?.map { it.asScrollComponent() }?.toMutableList(),
            keybind = json.getOrNull("keybind")?.asString,
            text = json.getOrNull("text")?.asString,
            translate = json.getOrNull("translate")?.asString,
            color = json.getOrNull("color")?.asString,
            bold = json.getOrNull("bold")?.asBoolean,
            italic = json.getOrNull("italic")?.asBoolean,
            underlined = json.getOrNull("underlined")?.asBoolean,
            strikethrough = json.getOrNull("strikethrough")?.asBoolean,
            obfuscated = json.getOrNull("obfuscated")?.asBoolean,
            font = json.getOrNull("font")?.asString,
            insertion = json.getOrNull("insertion")?.asString,
            hoverEvent = json.getOrNull("hoverEvent")?.asJsonObject?.asScrollHoverEvent(),
            clickEvent = json.getOrNull("clickEvent")?.asJsonObject?.asScrollClickEvent()
        )
    }
}

private fun JsonObject.asScrollHoverEvent(): HoverEvent {
    return HoverEvent(
        action = HoverAction.valueOf(this.get("action").asString.uppercase(Locale.ENGLISH)),
        contents = this.getOrNull("contents")?.asScrollComponent()
    )
}

private fun JsonObject.asScrollClickEvent(): ClickEvent {
    return ClickEvent(
        action = ClickAction.valueOf(this.get("action").asString.uppercase(Locale.ENGLISH)),
        value = this.getOrNull("value")?.asString
    )
}

private fun JsonObject.getOrNull(name: String): JsonElement? {
    return if (this.has(name))
        this.get(name)
    else null
}

private fun recursiveLoadScrollComponents(json: JsonArray): ScrollComponent {
    val components = mutableListOf<ScrollComponent>()

    for (element in json) {
        components.add(element.asScrollComponent())
    }

    return Components.new(components)
}