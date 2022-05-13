package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import restricted.fpe.FPE
import restricted.fpe.buildCompoundTag
import restricted.fpe.util.BlockPosTag.Companion.blockPos
import restricted.fpe.util.BlockPosTag.Companion.toCompoundTag

class FireDetectorBlockEntity(pos: BlockPos, state: BlockState) :
	AbstractHomeFireDevice<FireDetectorBlockEntity>(FPE.BlockEntityTypes.FireDetector, pos, state),
	IFireProtectBlockEntity {

	override var needSync: Boolean = false

	override val onFire: Boolean get() = fireDetected
	override var boundTo: HomeFireStationBlockEntity?
		get() {
			if(bindingDevicePos == null) return null
			if(level == null) return null
			return (level!!.getBlockEntity(bindingDevicePos!!).apply(::println) as? HomeFireStationBlockEntity).apply(::println)
		}
		set(value) { bindingDevicePos = value?.blockPos }

	var fireDetected: Boolean = false
	var bindingDevicePos: BlockPos? = null

	override fun getUpdateTag(): CompoundTag {
		return buildCompoundTag {
			putBoolean("fire", fireDetected)
			bindingDevicePos?.asLong()?.let { putLong("binding_pos", it) }
		}
	}

	override fun saveAdditional(tag: CompoundTag) {
		super.saveAdditional(tag)
		tag.putBoolean("fire", fireDetected)
		bindingDevicePos?.asLong()?.let { tag.putLong("binding_pos", it) }
	}

	override fun load(tag: CompoundTag) {
		super.load(tag)
		fireDetected = tag.getBoolean("fire")
		tag.getLong("binding_pos").let {
			if(it != 0L) { bindingDevicePos = BlockPos.of(it) }
		}
	}
}