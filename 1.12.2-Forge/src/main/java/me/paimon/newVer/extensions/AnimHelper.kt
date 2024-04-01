package me.paimon.newVer.extensions


import dragline.utils.render.AnimationUtils
import dragline.utils.render.RenderUtils
import me.paimon.ui.NewGUI
import me.paimon.utils.AnimationUtils2

fun Float.animSmooth(target: Float, speed: Float) = if (NewGUI.fastRenderValue.get()) target else AnimationUtils2.animate(target, this, speed * RenderUtils.deltaTime * 0.025F)
fun Float.animLinear(speed: Float, min: Float, max: Float) = if (NewGUI.fastRenderValue.get()) { if (speed < 0F) min else max } else (this + speed).coerceIn(min, max)