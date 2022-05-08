package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import restricted.fpe.FPE
import restricted.fpe.buildCompoundTag

class FireSprinklerBlockEntity(pos: BlockPos, state: BlockState) :
	BlockEntity(FPE.BlockEntityTypes.FireSprinkler, pos, state), IFireProtectBlockEntity {

	override var needSync: Boolean = false

	// 模式 - TRUE 洒水模式 FALSE 冷却模式/等待模式
	var waterSprinkling = false

	// 计时器 - 洒水模式时向上涨，直到 300tick(15s) 切换模式
	//       - 冷却模式时向下降，直到 0tick
	var ticks = 0

	var overloaded = false

	val idle: Boolean get() = !waterSprinkling && ticks == 0

	override fun getUpdateTag(): CompoundTag {
		return buildCompoundTag {
			putBoolean("mode", waterSprinkling)
			putInt("value", ticks)
		}
	}

	override fun saveAdditional(pTag: CompoundTag) {
		super.saveAdditional(pTag)
		pTag.putBoolean("mode", waterSprinkling)
		pTag.putInt("value", ticks)
	}

	override fun load(pTag: CompoundTag) {
		super.load(pTag)
		waterSprinkling = pTag.getBoolean("mode")
		ticks = pTag.getInt("value")
	}
}