package com.drinkstars.composesketch

import glm_.glm.PIf

val TWO_PI = 2f * PIf
val HALF_PI = PIf / 2f

fun norm(value: Float, min: Float, max: Float): Float {
    return (value - min) / (max - min)
}

fun lerp(norm: Float, min: Float, max: Float): Float {
    return (max - min) * norm + min
}

fun map(
    value: Float,
    sourceMin: Float,
    sourceMax: Float,
    destMin: Float,
    destMax: Float
): Float {
    return lerp(
        norm = norm(
            value = value,
            min = sourceMin,
            max = sourceMax
        ),
        min = destMin,
        max = destMax
    )
}

fun Int.map(sourceMin: Float, sourceMax: Float, destMin: Float, destMax: Float): Float {
    return lerp(norm(this.toFloat(), sourceMin, sourceMax), destMin, destMax)
}
