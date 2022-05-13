@file:Suppress("DEPRECATION")

package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractHomeFireDevice<T : BlockEntity?>(
	entityType: BlockEntityType<T>,
	pos: BlockPos,
	state: BlockState
) : BlockEntity(entityType, pos, state), IHomeFireDevice {

	fun bind(entity: HomeFireStationBlockEntity) {
		if(boundTo == null) {
			entity.bindDevice(this)
		}
	}

	fun unbind() = boundTo?.unbindDevice(this)

	fun bindForce(entity: HomeFireStationBlockEntity) {
		unbind()
		bind(entity)
	}
}