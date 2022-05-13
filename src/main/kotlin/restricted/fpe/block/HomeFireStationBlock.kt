@file:Suppress("UseExpressionBody", "OVERRIDE_DEPRECATION")

package restricted.fpe.block

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import restricted.fpe.*
import restricted.fpe.block.entity.AbstractHomeFireDevice
import restricted.fpe.block.entity.HomeFireStationBlockEntity
import restricted.fpe.block.entity.IFireProtectBlockEntity.Companion.tickSync
import restricted.fpe.item.HomeFireTerminalItem
import java.util.*

object HomeFireStationBlock : BaseEntityBlock(FPEConst.BlockConst.HomeFireStationProp) {

	val uuid: UUID = UUID.fromString("2ea0cb7c-c0e3-4010-ab71-67d2eeca66a9")

	override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
		level.getBlockEntity(pos, FPE.BlockEntityTypes.HomeFireStation).ifPresent { fireStationEntity ->
			// 遍历周围 15 格内的带有 BlockEntity 和 IHomeFireDevice 的方块，绑定到当前工作站上
			BlockPos.betweenClosedStream(boundingBoxOfCenter(pos, 15)).forEach { p ->
				val b = level.getBlockState(p)
				if(b.hasBlockEntity()) {
					val be = level.getBlockEntity(p)
					if(be != null && be is AbstractHomeFireDevice<*>) {
						be.bind(fireStationEntity)
					}
				}
			}

			if(fireStationEntity.connectedDevices.isNotEmpty()) {
				level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F)
			} else {
				level.playSound(null, pos, SoundEvents.NOTE_BLOCK_XYLOPHONE, SoundSource.BLOCKS, 1.0F, 1.0F)
			}
		}
	}

	override fun use(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hit: BlockHitResult
	): InteractionResult {
		level.runOnRemote {
			val stack = player.getItemInHand(hand)
			if(stack.item == FPE.Items.HomeFireTerminal) { // 绑定
				HomeFireTerminalItem.bindTo(stack, level, pos)
			} else { // 查询
				level.getBlockEntity(pos, FPE.BlockEntityTypes.HomeFireStation).ifPresent { fireStationEntity ->
					if(fireStationEntity.connectedDevices.isNotEmpty()) {
						// TODO: 做点帅的
						fireStationEntity.connectedDevices.forEach { device ->
							val entity = device as BlockEntity
							player.sendMessage(TextComponent("# ${entity.blockPos} -> ${device.onFire}"), uuid)
						}
					} else {
						player.sendMessage(TranslatableComponent("block.fire_protection_equipment.home_fire_station.no_bound_device"), uuid)
					}
				}
			}
		}
		return InteractionResult.CONSUME
	}

	@Suppress("UNUSED_PARAMETER")
	fun onTick(level: Level, pos: BlockPos, state: BlockState, be: HomeFireStationBlockEntity) {
		be.tickSync()
		if(!be.refreshedDeviceEntities) {
			be.refreshedDeviceEntities = true
			be.refreshDeviceEntitiesWhenWorldIsReady()
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