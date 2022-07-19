package restricted.fpe.item

import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import restricted.fpe.*
import restricted.fpe.extinguish.ExtinguishContext
import restricted.fpe.extinguish.ExtinguishType
import restricted.fpe.util.ExtinguishRayTracer

object HoseNozzleItem : Item(FPEConst.ItemConst.DefaultNonStackableItemProp) {

	override fun getUseDuration(pStack: ItemStack): Int = 7200

	override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		pPlayer.startUsingItem(pUsedHand)
		return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand))
	}

	override fun onUsingTick(stack: ItemStack, player: LivingEntity, count: Int) {
		player.level.runOnRemote {
			val tracer = ExtinguishRayTracer(player)
			when(count % 2) {
				0 -> handleHit(player, tracer)
				else -> addParticles(this, player, tracer, FPE.ParticleTypes.WaterFluid)
			}
		}
	}

	private fun addParticles(level: ServerLevel, player: LivingEntity, tracer: ExtinguishRayTracer, particle: ParticleOptions) {
		val offX = player.getUpVector(1.0F).cross(player.lookAngle)
		val offFactor = if(player.usedItemHand == InteractionHand.MAIN_HAND) -0.4 else 0.4
		val discretized = tracer.discretized(64)
		val discretizedSize = discretized.size
		for(index in 0 until discretizedSize) {
			val pos = discretized[index].add(offX.scale(index * offFactor / discretizedSize))
			level.sendParticles(particle, pos, 1, 0.0)
		}
	}

	private fun handleHit(player: LivingEntity, tracer: ExtinguishRayTracer) {
		val hit = tracer.hit
		if(hit is EntityHitResult) {
			hit.entity.hurt(DamageSource.FLY_INTO_WALL, 1.0F)
		}
		if(hit is BlockHitResult) {
			val ctx = ExtinguishContext(player.level, hit.location, 2, ExtinguishType.WATER, player as? Player, null)
			FPE.extinguishFire(ctx)
		}
	}

}