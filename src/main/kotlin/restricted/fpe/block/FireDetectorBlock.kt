@file:Suppress("DEPRECATION")

package restricted.fpe.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import restricted.fpe.*
import restricted.fpe.FPEConst.BlockConst.VERTICAL_FACING
import restricted.fpe.block.entity.FireDetectorBlockEntity

@Suppress("OVERRIDE_DEPRECATION")
object FireDetectorBlock : BaseEntityBlock(FPEConst.BlockConst.FireDetectorProp) {

	private val bottomShape = box(5.0, 0.0, 5.0, 11.0, 2.0, 11.0)
	private val topShape = box(5.0, 14.0, 5.0, 11.0, 16.0, 11.0)

	init {
		registerDefaultState(
			this.stateDefinition.any().setValue(VERTICAL_FACING, Direction.UP)
		)
	}

	fun hasFireAround(level: Level, blockPos: BlockPos): Boolean {
		return level.getBlockStates(boundingBoxOfCenter(blockPos.below(2), 3).AABB).anyMatch {
			it.`is`(BlockTags.FIRE)
		}
	}

	override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
		pBuilder.add(VERTICAL_FACING)
	}

	@Suppress("UNUSED_PARAMETER")
	fun onTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: FireDetectorBlockEntity) {
		val hasFire = hasFireAround(level, pos)
		if(hasFire != blockEntity.fireDetected) {
			blockEntity.fireDetected = hasFire
			level.updateNeighborsAt(pos, this)
		}
	}

	override fun getRenderShape(pState: BlockState): RenderShape {
		return RenderShape.MODEL
	}

	override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? {
		val direction = if(pContext.clickedFace == Direction.UP) Direction.UP else Direction.DOWN
		return defaultBlockState().setValue(VERTICAL_FACING, direction)
	}

	override fun getShape(
		pState: BlockState,
		pLevel: BlockGetter,
		pPos: BlockPos,
		pContext: CollisionContext
	): VoxelShape {
		return if(pState.getValue(VERTICAL_FACING) == Direction.UP) {
			bottomShape
		} else {
			topShape
		}
	}

	override fun isSignalSource(state: BlockState): Boolean {
		return true
	}

	override fun getSignal(state: BlockState, level: BlockGetter, pos: BlockPos, pDirection: Direction): Int {
		val blockEntity = level.getBlockEntity(pos, FPE.BlockEntityTypes.FireDetector)
		return if(blockEntity.isPresent && blockEntity.get().fireDetected) {
			15
		} else {
			0
		}
	}

	override fun onPlace(state: BlockState, level: Level, pos: BlockPos, pOldState: BlockState, pIsMoving: Boolean) {
		level.getBlockEntity(pos, FPE.BlockEntityTypes.FireDetector).ifPresent { fireDetectorEntity ->
			BlockPos.betweenClosedStream(boundingBoxOfCenter(pos, 15)).forEach { p ->
				val b = level.getBlockState(p)
				if(b.hasBlockEntity()) {
					level.getBlockEntity(p, FPE.BlockEntityTypes.HomeFireStation).ifPresent {
						it.bindDevice(fireDetectorEntity)
						level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F)
					}
				}
			}
		}
	}

	override fun onRemove(
		pState: BlockState,
		pLevel: Level,
		pPos: BlockPos,
		pNewState: BlockState,
		pIsMoving: Boolean
	) {
		pLevel.getBlockEntity(pPos, FPE.BlockEntityTypes.FireDetector).ifPresent {
			it.unbind()
		}
		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving)
	}

	override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity {
		return FireDetectorBlockEntity(pPos, pState)
	}

	override fun <T : BlockEntity?> getTicker(
		pLevel: Level,
		pState: BlockState,
		pBlockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? {
		return createTickerHelper(pBlockEntityType, FPE.BlockEntityTypes.FireDetector, ::onTick)
	}

	override fun canSurvive(pState: BlockState, pLevel: LevelReader, pPos: BlockPos): Boolean {
		return !pLevel.getBlockState(pPos.above()).isAir || !pLevel.getBlockState(pPos.below()).isAir
	}
}