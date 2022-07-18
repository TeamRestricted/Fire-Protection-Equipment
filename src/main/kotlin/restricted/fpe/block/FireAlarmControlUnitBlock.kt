@file:Suppress("UseExpressionBody", "OVERRIDE_DEPRECATION", "DEPRECATION")

package restricted.fpe.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import restricted.fpe.FPE
import restricted.fpe.FPEConst
import restricted.fpe.block.entity.HomeFireStationBlockEntity
import restricted.fpe.block.entity.IFireProtectBlockEntity.Companion.tickSync

object FireAlarmControlUnitBlock : BaseEntityBlock(FPEConst.BlockConst.HomeFireStationProp) {

	override fun onRemove(
		pState: BlockState,
		pLevel: Level,
		pPos: BlockPos,
		pNewState: BlockState,
		pIsMoving: Boolean
	) {
		val be = pLevel.getBlockEntity(pPos)
		if(be is HomeFireStationBlockEntity) {
			be.connectedDevices.forEach {
				it.unbind(be)
			}
		}
		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving)
	}

	@Suppress("UNUSED_PARAMETER")
	fun onTick(level: Level, pos: BlockPos, state: BlockState, be: HomeFireStationBlockEntity) {
		be.tickSync()
		if(!level.isClientSide) {
			if(!be.refreshedDeviceEntities) {
				be.refreshedDeviceEntities = true
				be.refreshDeviceEntitiesWhenWorldIsReady()
			}
		}
	}

	override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity {
		return HomeFireStationBlockEntity(pPos, pState)
	}

	override fun <T : BlockEntity?> getTicker(
		pLevel: Level,
		pState: BlockState,
		pBlockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? {
		return createTickerHelper(pBlockEntityType, FPE.BlockEntityTypes.HomeFireStation, ::onTick)
	}

	val shape: VoxelShape = box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0)

	override fun getShape(
		pState: BlockState,
		pLevel: BlockGetter,
		pPos: BlockPos,
		pContext: CollisionContext
	): VoxelShape {
		return shape
	}

	override fun getRenderShape(pState: BlockState): RenderShape {
		return RenderShape.MODEL
	}

}