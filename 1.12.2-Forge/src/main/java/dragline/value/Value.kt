/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.value

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import dragline.DragLine
import dragline.api.minecraft.client.gui.IFontRenderer
import dragline.ui.font.Fonts
import dragline.utils.ClientUtils
import java.util.*

abstract class Value<T>(val name: String, var value: T) {
    val default = value
    var textHovered: Boolean = false

    private var displayableFunc: () -> Boolean = { true }

    fun displayable(func: () -> Boolean): Value<T> {
        displayableFunc = func
        return this
    }

    val displayable: Boolean
        get() = displayableFunc()

    val displayableFunction: () -> Boolean
        get() = displayableFunc



    var isSupported = true

    fun set(newValue: T) {
        if (newValue == value)
            return

        val oldValue = get()

        try {
            onChange(oldValue, newValue)
            changeValue(newValue)
            onChanged(oldValue, newValue)
            DragLine.fileManager.saveConfig(DragLine.fileManager.valuesConfig)
        } catch (e: Exception) {
            ClientUtils.getLogger().error("[ValueSystem ($name)]: ${e.javaClass.name} (${e.message}) [$oldValue >> $newValue]")
        }
    }

    fun get() = value

    open fun changeValue(value: T) {
        this.value = value
    }

    abstract fun toJson(): JsonElement?
    abstract fun fromJson(element: JsonElement)

    protected open fun onChange(oldValue: T, newValue: T) {}
    protected open fun onChanged(oldValue: T, newValue: T) {}

}

/**
 * Bool value represents a value with a boolean
 */
open class BoolValue(name: String, value: Boolean) : Value<Boolean>(name, value) {

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asBoolean || element.asString.equals("true", ignoreCase = true)
        }
    }
    open fun toggle(){
        this.value = !this.value
    }

}


/**
 * Integer value represents a value with a integer
 */
open class IntegerValue(name: String, value: Int, val minimum: Int = 0, val maximum: Int = Integer.MAX_VALUE, val suffix: String, displayable: () -> Boolean)
    : Value<Int>(name, value) {

    constructor(name: String, value: Int, minimum: Int, maximum: Int, displayable: () -> Boolean): this(name, value, minimum, maximum, "", displayable)
    constructor(name: String, value: Int, minimum: Int, maximum: Int, suffix: String): this(name, value, minimum, maximum, suffix, { true } )
    constructor(name: String, value: Int, minimum: Int, maximum: Int): this(name, value, minimum, maximum, { true } )

    fun set(newValue: Number) {
        set(newValue.toInt())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asInt
        }
    }
}

/**
 * Float value represents a value with a float
 */
open class FloatValue(name: String, value: Float, val minimum: Float = 0F, val maximum: Float = Float.MAX_VALUE)
    : Value<Float>(name, value) {

    fun set(newValue: Number) {
        set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asFloat
    }

}

/**
 * Text value represents a value with a string
 */
open class TextValue(name: String, value: String) : Value<String>(name, value) {
    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asString
        }

    }

    fun append(o: Any): TextValue {
        set(get() + o)
        return this
    }

}

/**
 * Font value represents a value with a font
 */
class FontValue(valueName: String, value: IFontRenderer) : Value<IFontRenderer>(valueName, value) {

    override fun toJson(): JsonElement {
        val fontDetails = Fonts.getFontDetails(value)
        val valueObject = JsonObject()
        valueObject.addProperty("fontName", fontDetails.name)
        valueObject.addProperty("fontSize", fontDetails.fontSize)
        return valueObject
    }

    override fun fromJson(element: JsonElement) {
        if (!element.isJsonObject) return
        val valueObject = element.asJsonObject
        value = Fonts.getFontRenderer(valueObject["fontName"].asString, valueObject["fontSize"].asInt)
    }

    fun set(name: String): Boolean {
        if (name.equals("Minecraft", true)) {
            set(Fonts.minecraftFont)
            return true
        } else if (name.contains(" - ")) {
            val spiced = name.split(" - ")
            set(Fonts.getFontRenderer(spiced[0], spiced[1].toInt()) ?: return false)
            return true
        }
        return false
    }
}

/**
 * Block value represents a value with a block
 */
class BlockValue(name: String, value: Int) : IntegerValue(name, value, 1, 197)

/**
 * List value represents a selectable list of values
 */
open class ListValue(name: String, val values: Array<String>, value: String) : Value<String>(name, value) {
    @JvmField
    var openList = false

    @JvmField
    var open=true;

    init {
        this.value = value
    }

    operator fun contains(string: String?): Boolean {
        return Arrays.stream(values).anyMatch { s: String -> s.equals(string, ignoreCase = true) }
    }

    override fun changeValue(value: String) {
        for (element in values) {
            if (element.equals(value, ignoreCase = true)) {
                this.value = element
                break
            }
        }
    }


    fun indexOf(mode: String): Int {
        for (i in values.indices) {
            if (values[i].equals(mode, true)) return i
        }
        return 0
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) changeValue(element.asString)
    }
}