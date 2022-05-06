package restricted.fpe.extinguish

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import restricted.fpe.api.FireType

data class ExtinguishContext(
	val world: Level,
	val centerPos: BlockPos,
	val extinguishLevel: Int,
	val extinguishType: FireType,

	val extinguishingBlock: BlockPos? = null,
	val extinguishingPlayer: Player? = null,
	val extinguishingItem: ItemStack? = null
)
