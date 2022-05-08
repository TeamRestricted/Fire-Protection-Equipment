@file:Suppress("OVERRIDE_DEPRECATION")

package restricted.fpe.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import restricted.fpe.FPE
import restricted.fpe.FPEConst

object FireHydrantBlock : HorizontalDirectionalBlock(FPEConst.BlockConst.FireHydrantProp) {
	private val shape = Block.box(2.0, 0.0, 2.0, 14.0, 26.0, 14.0)

	init {
		registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH))
	}

	override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
		pBuilder.add(FACING)
	}

	override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
		defaultBlockState()
			.setValue(FACING, pContext.horizontalDirection.opposite)

	override fun getShape(
		pState: BlockState,
		pLevel: BlockGetter,
		pPos: BlockPos,
		pContext: CollisionContext
	): VoxelShape = shape

	override fun getDrops(pState: BlockState, pBuilder: LootContext.Builder): MutableList<ItemStack> {
		val tool = pBuilder.getParameter(LootContextParams.TOOL)
		val level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool)
		return if(level > 0) {
			mutableListOf(FPE.Items.FireHydrant.defaultInstance)
		} else {
			mutableListOf(FPE.Items.BrokenFireHydrant.defaultInstance)
		}
	}
}