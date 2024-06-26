/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.util

object WMathHelper {

    @Suppress("FunctionName")
    @JvmStatic
    fun wrapAngleTo180_float(angle: Float): Float {
        var value = angle % 360.0f

        if (value >= 180.0f) {
            value -= 360.0f
        }

        if (value < -180.0f) {
            value += 360.0f
        }

        return value
    }

    @JvmStatic
    inline fun clamp_float(num: Float, min: Float, max: Float): Float {
        return if (num < min) min else if (num > max) max else num
    }

    @JvmStatic
    inline fun clamp_double(num: Double, min: Double, max: Double): Double {
        return if (num < min) min else if (num > max) max else num
    }

    @JvmStatic
    fun floor_double(value: Double): Int {
        val i = value.toInt()
        return if (value < i.toDouble()) i - 1 else i
    }
}