@file:Suppress("DEPRECATION")

package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractHomeFireDevice<T : BlockEntity?>(
	entityType: BlockEntityType<T>,
	pos: BlockPos,
	state: BlockState
) : BlockEntity(entityType, pos, state), IHomeFireDevice {

	abstract fun bind(entity: HomeFireStationBlockEntity)

	abstract fun unbind(entity: HomeFireStationBlockEntity)

	fun tryUnbind(level: Level) {
		if(boundToLocation != null) {
			val be = level.getBlockEntity(boundToLocation!!)
			if(be is HomeFireStationBlockEntity) {
				unbind(be)
			}
		}
	}

}