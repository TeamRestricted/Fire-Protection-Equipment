package restricted.fpe.util

import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import org.apache.commons.lang3.Validate
import java.lang.Double.*
import kotlin.math.abs
import kotlin.math.min

object ParticleUtils {

	fun getRoutingVec3s(v1: Vec3, v2: Vec3, distance: Double = 1.0): List<Vec3> {
		Validate.inclusiveBetween(0.0, abs(v1.distanceTo(v2)), distance)

		return buildList {
			val factor = -(distance / v1.distanceTo(v2))

			val deltaX = (v1.x - v2.x) * factor
			val deltaY = (v1.y - v2.y) * factor
			val deltaZ = (v1.z - v2.z) * factor

			var x = v1.x
			var y = v1.y
			var z = v1.z

			val rangeX = min(v1.x, v2.x)..max(v1.x, v2.x)
			val rangeY = min(v1.y, v2.y)..max(v1.y, v2.y)
			val rangeZ = min(v1.z, v2.z)..max(v1.z, v2.z)

			while(true) {
				add(Vec3(x, y, z))
				x += deltaX
				y += deltaY
				z += deltaZ

				if(x !in rangeX || y !in rangeY || z !in rangeZ) {
					break
				}
			}
		}
	}

	fun getFlexibleCircleVec3s(center: Vec3, distance: Double, generateCount: Int): List<Vec3> {
		return getCircleVec3s(center, distance, Mth.fastFloor(generateCount * distance))
	}

	fun getCircleVec3s(center: Vec3, distance: Double, count: Int): List<Vec3> {
		val angleForEach = 360F / count

		return buildList {
			var currentAngle = 0F

			while(true) {
				val deltaX = Mth.sin(currentAngle) * distance
				val deltaZ = Mth.cos(currentAngle) * distance

				add(Vec3(center.x + deltaX, center.y, center.z + deltaZ))

				currentAngle += angleForEach

				if(currentAngle >= 360F) {
					break
				}
			}
		}
	}

}