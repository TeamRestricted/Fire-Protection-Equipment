package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity

/**
 * 将 [BlockEntity] 标记为家用火灾探测器
 */
interface IHomeFireDevice {

	val onFire: Boolean

	var boundToLocation: BlockPos?

}