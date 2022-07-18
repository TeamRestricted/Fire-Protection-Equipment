package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import restricted.fpe.FPE
import restricted.fpe.buildCompoundTag

class FireDetectorBlockEntity(pos: BlockPos, state: BlockState) :
	AbstractHomeFireDevice<FireDetectorBlockEntity>(FPE.BlockEntityTypes.FireDetector, pos, state),
	IFireProtectBlockEntity {

	override var needSync: Boolean = false

	override val onFire: Boolean get() = fireDetected

	override var boundToLocation: BlockPos? = null

	var boundTo: HomeFireStationBlockEntity? = null

	var fireDetected: Boolean = false

	override fun bind(entity: HomeFireStationBlockEntity) {
		boundToLocation = entity.blockPos
		boundTo = entity
		entity.registerDevice(this)
	}

	override fun unbind(entity: HomeFireStationBlockEntity) {
		boundToLocation = null
		boundTo = null
		entity.unregisterDevice(this)
	}

	override fun getUpdateTag(): CompoundTag {
		return buildCompoundTag {
			putBoolean("fire", fireDetected)
			boundToLocation?.asLong()?.let { putLong("binding_pos", it) }
		}
	}

	override fun saveAdditional(tag: CompoundTag) {
		super.saveAdditional(tag)
		tag.putBoolean("fire", fireDetected)
		boundToLocation?.asLong()?.let { tag.putLong("binding_pos", it) }
	}

	override fun load(tag: CompoundTag) {
		super.load(tag)
		fireDetected = tag.getBoolean("fire")
		tag.getLong("binding_pos").let {
			if(it != 0L) { boundToLocation = BlockPos.of(it) }
		}

		if(boundToLocation != null) {
			val control = level?.getBlockEntity(boundToLocation!!)
			if(control is HomeFireStationBlockEntity) {
				if(blockPos.asLong() in control.connectedDevicesPos) {
					control.connectedDevices += this
				} else {
					boundToLocation = null
				}
			}
		}
	}
}