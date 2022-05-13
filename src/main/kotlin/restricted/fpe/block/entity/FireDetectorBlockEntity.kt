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
	override var boundTo: HomeFireStationBlockEntity? = null

	var fireDetected: Boolean = false

	override fun getUpdateTag(): CompoundTag {
		return buildCompoundTag {
			putBoolean("fire", fireDetected)
		}
	}

	override fun saveAdditional(tag: CompoundTag) {
		super.saveAdditional(tag)
		tag.putBoolean("fire", fireDetected)
	}

	override fun load(tag: CompoundTag) {
		super.load(tag)
		fireDetected = tag.getBoolean("fire")
	}
}