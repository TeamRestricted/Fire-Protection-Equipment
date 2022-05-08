package restricted.fpe.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.*
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import restricted.fpe.*
import restricted.fpe.extinguish.ExtinguishContext
import restricted.fpe.extinguish.ExtinguishContext.Companion.process
import restricted.fpe.extinguish.ExtinguishType
import restricted.fpe.runOnRemote

@Suppress("OVERRIDE_DEPRECATION")
object FireExtinguishingBombBlock : Block(FPEConst.BlockConst.FireExtinguishingBombProp) {

	fun onExtinguish(
		level: Level,
		pos: BlockPos,
		ignitePlayer: Player? = null
	) {
		level.removeBlock(pos, false)
		ExtinguishContext(level, pos, 8, ExtinguishType.DRY_ICE, player = ignitePlayer).process()
		level.runOnRemote {
			sendParticles(ParticleTypes.EXPLOSION, pos.vec3, 10, 0.2)
		}
	}

	// 300 = 被火焰燃烧时立刻烧毁（触发 #onCaughtFire）
	override fun getFlammability(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Int =
		300

	// 被火焰烧毁时
	override fun onCaughtFire(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		direction: Direction?,
		igniter: LivingEntity?
	) {
		onExtinguish(level, pos, igniter as? Player)
	}

	// 被炸毁时
	override fun onBlockExploded(state: BlockState, level: Level, pos: BlockPos, explosion: Explosion) {
		onExtinguish(level, pos, explosion.sourceMob as? Player)
	}

	// 被打火石使用时
	override fun use(
		state: BlockState,
		world: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hit: BlockHitResult
	): InteractionResult {
		val itemstack = player.getItemInHand(hand)
		if(itemstack.item == Items.FLINT_AND_STEEL) {
			onExtinguish(world, pos, player)
			return InteractionResult.sidedSuccess(world.isClientSide)
		}
		return InteractionResult.PASS
	}

	// 不能因爆炸掉落
	override fun canDropFromExplosion(
		state: BlockState?,
		level: BlockGetter?,
		pos: BlockPos?,
		explosion: Explosion?
	): Boolean = false

	// 当被红石触发时
	override fun neighborChanged(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		block: Block,
		fromPos: BlockPos,
		isMoving: Boolean
	) {
		if(level.hasNeighborSignal(pos)) {
			onExtinguish(level, pos)
		}
	}
}