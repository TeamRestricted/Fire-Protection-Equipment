package restricted.fpe.extinguish

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.Vec3
import restricted.fpe.*

data class ExtinguishContext(
	val level: Level,
	val centerPos: Vec3,
	val size: Int,
	val type: ExtinguishType,

	val player: Player? = null,
	val itemstack: ItemStack? = null
) {
	val boundingBox: BoundingBox get() = boundingBoxOfCenter(centerPos.pos, size, size, size)

	companion object {
		fun ExtinguishContext.process() = FPE.extinguishFire(this)
	}
}