package restricted.fpe.extinguish

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.BoundingBox
import restricted.fpe.FPE
import restricted.fpe.boundingBoxOfCenter

data class ExtinguishContext(
	val level: Level,
	val centerPos: BlockPos,
	val size: Int,
	val type: ExtinguishType,

	val player: Player? = null,
	val itemstack: ItemStack? = null
) {
	val boundingBox: BoundingBox get() = boundingBoxOfCenter(centerPos, size, size, size)

	companion object {
		fun ExtinguishContext.process() = FPE.extinguishFire(this)
	}
}