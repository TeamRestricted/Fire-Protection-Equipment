package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import restricted.fpe.FPE
import restricted.fpe.buildCompoundTag

class FireDetectorBlockEntity(pos: BlockPos, state: BlockState) :
	BlockEntity(FPE.BlockEntityTypes.FireDetector, pos, state), IFireProtectBlockEntity {

	override var needSync: Boolean = false

	var fireDetected: Boolean = false

	override fun getUpdateTag(): CompoundTag {
		return buildCompoundTag {
			putBoolean("fire", fireDetected)
		}
	}

	override fun saveAdditional(pTag: CompoundTag) {
		super.saveAdditional(pTag)
		pTag.putBoolean("fire", fireDetected)
	}

	override fun load(pTag: CompoundTag) {
		super.load(pTag)
		fireDetected = pTag.getBoolean("fire")
	}
}