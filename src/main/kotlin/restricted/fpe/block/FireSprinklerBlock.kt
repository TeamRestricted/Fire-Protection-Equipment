@file:Suppress("OVERRIDE_DEPRECATION")

package restricted.fpe.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import restricted.fpe.*
import restricted.fpe.block.entity.FireSprinklerBlockEntity
import restricted.fpe.extinguish.ExtinguishContext
import restricted.fpe.extinguish.ExtinguishContext.Companion.process
import restricted.fpe.extinguish.ExtinguishType

object FireSprinklerBlock : BaseEntityBlock(FPEConst.BlockConst.FireSprinklerProp) {

	override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity? {
		return FPE.BlockEntityTypes.FireSprinkler.create(pPos, pState)
	}

	override fun <T : BlockEntity?> getTicker(
		pLevel: Level,
		pState: BlockState,
		pBlockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? {
		return createTickerHelper(pBlockEntityType, FPE.BlockEntityTypes.FireSprinkler, ::onTick)
	}

	@Suppress("UNUSED_PARAMETER")
	fun onTick(level: Level, pos: BlockPos, state: BlockState, be: FireSprinklerBlockEntity) {
		level.runOnRemote {
			if(be.waterSprinkling) {
				be.ticks += 5
				if(be.ticks >= 900) {
					be.waterSprinkling = false
					be.overloaded = true
				}
			} else {
				if(be.ticks > 0) {
					be.ticks -= 3
				}
				if(be.ticks < 50) {
					be.overloaded = false
				}
			}

			// 重新设置模式
			val charged = level.hasNeighborSignal(pos)
			if(!be.overloaded && charged) { // 未过载 + 激活 => 喷淋模式
				be.waterSprinkling = true
			}
			if(be.waterSprinkling && be.ticks >= 300 && !charged) { // 喷淋模式 + 未激活 => 冷却模式
				be.waterSprinkling = false
			}

			if(be.waterSprinkling) {
				ExtinguishContext(level, pos, 3, ExtinguishType.WATER).process()
				sendParticles(ParticleTypes.RAIN, pos.vec3.add(0.0, -2.5, 0.0), 100, 0.2, offset = Vec3(4.0, 2.5, 4.0))
			}
			if(be.overloaded) {
				sendParticles(ParticleTypes.SMOKE, pos.vec3.add(0.0, 0.15, 0.0), 5, 0.2)
			}
		}
	}

	override fun getRenderShape(pState: BlockState): RenderShape {
		return RenderShape.MODEL
	}

	val shape: VoxelShape = box(5.5, 10.0, 5.5, 10.5, 16.0, 10.5)

	override fun getShape(
		pState: BlockState,
		pLevel: BlockGetter,
		pPos: BlockPos,
		pContext: CollisionContext
	): VoxelShape {
		return shape.getFaceShape(Direction.DOWN)
	}
}