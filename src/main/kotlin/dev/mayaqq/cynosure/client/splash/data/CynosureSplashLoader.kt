package dev.mayaqq.cynosure.client.splash.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import dev.mayaqq.cynosure.Cynosure
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import kotlin.random.Random

/**
 * Loader for Splashes Data
 * Create a simple assets/modid/splashes/splashes.json file and put your strings into a simple JsonArray with the name "splashes".
 *
 * Additionally, please increment *amount* in CynosureSplashLoader with the amount of strings you added, does not have to be exact
 *
 */
public object CynosureSplashLoader : SimpleJsonResourceReloadListener(Gson(), "splashes") {

    public var splashes: MutableList<Component> = mutableListOf()

    /**
     * Set the amount of splashes you added, sadly currently Minecraft Loads Data After Splashes, so this is just
     *  an assumed amount
     */
    public var amount: Int = 0;

    override fun apply(items: MutableMap<ResourceLocation, JsonElement>, manager: ResourceManager, profiler: ProfilerFiller) {
        this.splashes.clear()
        items.forEach { (key, value) ->
            if (key.path.equals("splashes")) {
                value.getAsJsonObject().getAsJsonArray("splashes").forEach { element ->
                    if (element.isJsonObject) {
                        try {
                            val formatted = parse(element.getAsJsonObject().toString())
                            Component.Serializer.fromJsonLenient(formatted)?.let { this.splashes.add(it) }
                        } catch (e: Exception) {
                            Cynosure.warn("Failed to parse splash text", e)
                        }
                    } else {
                        this.splashes.add(Component.literal(parse(element.asString)))
                    }
                }
            }
        }
    }

    private fun parse(input: String): String {
        var text = input
        text = replaceVariable(text, "percent", "${Random.nextInt(101)}%")
        text = replaceVariable(text, "username", Minecraft.getInstance().user.name)
        return text
    }

    private fun replaceVariable(input: String, name: String, value: String): String {
        val variable = "%$name%"
        return input.replace(("(^|[^\\\\])$variable").toRegex(), "$1$value")
            .replace(("\\\\" + variable).toRegex(), variable)
    }
}