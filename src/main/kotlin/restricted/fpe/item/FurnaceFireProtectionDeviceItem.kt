package restricted.fpe.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.entity.BlockEntityType
import restricted.fpe.FPEConst.ItemConst.DefaultNonStackableItemProp
import restricted.fpe.MinecraftBlocks

object FurnaceFireProtectionDeviceItem: Item(DefaultNonStackableItemProp) {

	override fun useOn(ctx: UseOnContext): InteractionResult {
		val level = ctx.level
		val blockPos = ctx.clickedPos
		val state = level.getBlockState(blockPos)
		if(state.block == MinecraftBlocks.FURNACE) {
			val furnaceEntity = level.getBlockEntity(blockPos, BlockEntityType.FURNACE)
			if(furnaceEntity.isPresent) {
				return InteractionResult.sidedSuccess(level.isClientSide)
			}
		}
		return super.useOn(ctx)
	}

}