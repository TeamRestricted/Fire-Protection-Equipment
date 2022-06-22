package restricted.fpe.util

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.*
import java.util.*
import kotlin.math.floor
import kotlin.math.max

/**
 * 这个类改写自土球老师的 WaterSprayer 的 SrayerRayTracer，
 * 依照开源协议 WTFPL v2 抄下来的。
 *
 * https://github.com/ustc-zzzz/WaterSprayer/blob/master/src/main/scala/com/github/ustc_zzzz/watersprayer/sprayer/SprayerRayTracer.scala
 */
class ExtinguishRayTracer(val entity: LivingEntity) {

	data class Traced(val motion: Vec3, val pos: Vec3)

	var hit: HitResult? = null

	val hitEntity: Entity? get() = (hit as? EntityHitResult)?.entity

	private val traced: LinkedList<Traced> = LinkedList<Traced>()

	init {
		val startPos = entity.position().add(0.0, entity.eyeHeight * 0.75, 0.0)
		val startMotion = entity.lookAngle.add(0.0, 0.2, 0.0).normalize()

		traced.add(Traced(startMotion, startPos))

		var continueRayTracing = true
		while(continueRayTracing) {
			val motion = traced[0].motion
			val pos = traced[0].pos

			val newMotion = motion.subtract(0.0, 0.1, 0.0).run {
				if(this.lengthSqr() <= 1) this else this.normalize()
			}
			var newPos = pos.add(motion).add(newMotion)

			val blockResult = entity.level.clip(ClipContext(pos,  newPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
			if(blockResult.type != HitResult.Type.MISS) { // on hit block
				hit = blockResult
				continueRayTracing = false
				newPos = blockResult.location
			}

			val bb1 = AABB(pos, newPos).inflate(1.0)
			val entityResult = ProjectileUtil.getEntityHitResult(entity.level, entity, pos, newPos, bb1) {
				!it.isSpectator && it.isPickable && it != entity
			}
			if(entityResult != null) { // on hit entity
				hit = entityResult
				continueRayTracing = false
				val bb2 = entityResult.entity.boundingBox.inflate(0.3)
				val bboxMaxSize = max(bb2.xsize, max(bb2.ysize, bb2.zsize))
				val endPos = pos.add(newPos.subtract(pos).run { this.normalize().scale(bboxMaxSize + this.length()) })
				newPos = bb2.clip(endPos, pos).orElse(newPos).add(bb2.clip(pos, endPos).orElse(newPos)).scale(0.5)
			}
			traced.push(Traced(newMotion, newPos))
			if(newPos.y < -100 || newPos.y > 400) continueRayTracing = false
		}
	}

	fun discretized(factor: Int): List<Vec3> {
		val tmpTraced = LinkedList(traced)
		val head = tmpTraced.removeFirst()
		var prevPos = head.pos
		var prevMotion = head.motion

		val bd = LinkedList<Vec3>()

		for(nextTraced in tmpTraced) {
			val nextPos = nextTraced.pos
			val nextMotion = nextTraced.motion
			val max = factor * nextPos.subtract(prevPos).length() / nextMotion.add(prevMotion).length()
			for(i in floor(max).toInt() downTo 1) {
				val alpha = i / max
				val beta = (max - i) / max
				val alphaPos = nextPos.add(nextMotion.scale(alpha * max / factor))
				val betaPos = prevPos.subtract(prevMotion.scale(beta * max / factor))
				bd += betaPos.scale(alpha).add(alphaPos.scale(beta))
			}
			prevPos = nextPos
			prevMotion = nextMotion
		}

		bd += prevPos
		return bd
	}

}