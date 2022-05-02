@file:Suppress("OVERRIDE_DEPRECATION")

package restricted.fpe.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.*
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import restricted.fpe.FPE

val propFireHydrantBlock: BlockBehaviour.Properties =
	BlockBehaviour.Properties.of(Material.HEAVY_METAL).strength(5.0F, 1200.0F).sound(SoundType.ANVIL).noOcclusion()
private val propertyBroken = BooleanProperty.create("broken")

object FireHydrantBlock : HorizontalDirectionalBlock(propFireHydrantBlock) {
	private val shape = Block.box(2.0, 0.0, 2.0, 14.0, 24.0, 14.0)

	val BlockState.broken: Boolean get() = getValue(propertyBroken)

	init {
		registerDefaultState(
			this.stateDefinition.any().setValue(propertyBroken, false).setValue(FACING, Direction.NORTH)
		)
	}

	override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
		pBuilder.add(FACING)
		pBuilder.add(propertyBroken)
	}

	override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
		defaultBlockState()
			.setValue(FACING, pContext.horizontalDirection.opposite)
			.setValue(propertyBroken, pContext.itemInHand.tag?.getBoolean("broken") ?: false)

	override fun getShape(
		pState: BlockState,
		pLevel: BlockGetter,
		pPos: BlockPos,
		pContext: CollisionContext
	): VoxelShape = shape

	override fun getDrops(pState: BlockState, pBuilder: LootContext.Builder): MutableList<ItemStack> = mutableListOf()
}