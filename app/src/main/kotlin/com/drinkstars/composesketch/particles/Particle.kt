package com.drinkstars.composesketch.particles

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import glm_.glm.linearRand
import kotlin.math.floor

class Particle(
    val width: Float,
    val height: Float,
    val radius: Float = 10f,
    initPos: Offset = Offset(linearRand(0f, width), linearRand(0f, height)),
    initVel: Offset = Offset(linearRand(-0.5f, 0.5f), linearRand(-0.4f, 0.6f))
) {
    var pos = initPos
    var vel = initVel
    private var acc = Offset.Zero
    private val maxSpeed = 1
    val x get() = pos.x
    val y get() = pos.y

    fun update() {
        val newVel = vel + acc
        if (newVel.x < maxSpeed && newVel.y < maxSpeed) vel = newVel
        pos += vel
        acc *= 0f
    }

    fun applyForce(force: Offset) {
        acc += force
    }

    fun edges() {
        if (this.pos.x > width) {
            this.pos = this.pos.copy(x = 0f)
//            this.vel = this.vel.copy(x = -this.vel.x)
        }
        if (this.pos.x < 0f) {
            this.pos = this.pos.copy(x = width)
//            this.vel = this.vel.copy(x = -this.vel.x)
        }
        if (this.pos.y > height) {
            this.pos = this.pos.copy(y = 0f)
//            this.vel = this.vel.copy(y = -this.vel.y)
        }
        if (this.pos.y < 0f) {
            this.pos = this.pos.copy(y = height)
//            this.vel = this.vel.copy(y = -this.vel.y)
        }
    }
}

fun List<Particle>.drawAndFollow(
    color: Color = Color.White,
    drawScope: DrawScope,
    vectors: Array<Offset>,
    resolution: Float,
    count: Int
) {
    this.forEach {
        it.follow(vectors, resolution, count)
        it.update()
    }

    drawScope.drawPoints(
        points = this.map { it.pos },
        pointMode = PointMode.Points,
        color = color,
        strokeWidth = 2f,
        alpha = 0.1f,
        cap = StrokeCap.Round
    )
    this.forEach { it.edges() }
}

fun List<Particle>.draw(
    color: Color = Color.Black,
    drawScope: DrawScope
) {
    this.forEach {
        it.update()
    }

    drawScope.drawPoints(
        points = this.map { it.pos },
        pointMode = PointMode.Points,
        color = color,
        strokeWidth = 1f,
        cap = StrokeCap.Round
    )
    this.forEach { it.edges() }
}

// particle should follow the vector
private fun Particle.follow(vectors: Array<Offset>, resolution: Float, count: Int) {
    val xd = floor(this.pos.x / resolution).toInt()
    val yd = floor(this.pos.y / resolution).toInt()
    val index = (xd + yd * count)
    val vector = vectors[index]
    vel = (vector)
}
