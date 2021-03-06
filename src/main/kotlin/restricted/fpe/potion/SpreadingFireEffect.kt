package restricted.fpe.potion

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.monster.Blaze
import net.minecraft.world.phys.AABB
import restricted.fpe.*

object SpreadingFireEffect : MobEffect(MobEffectCategory.HARMFUL, 0x2524AB) {

	val excludedEntities = listOf(EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.STRIDER)

	private const val factorNextGenDuration = 8
	private const val sizeOfSpreading = 8.0

	fun instance(duration: Int, amplifier: Int) = MobEffectInstance(FPE.MobEffects.SpreadingFire, duration, amplifier)

	override fun isDurationEffectTick(pDuration: Int, pAmplifier: Int): Boolean = true

	override fun applyEffectTick(entity: LivingEntity, amp: Int) {
		if(entity.type in excludedEntities) return // 烈焰人等生物不受影响

		val ampFactor = amp + 1
		entity.hurt(FPEConst.DamageSourceConst.SpreadingFire, 1.0F + ampFactor * 0.75F)
		if(entity.health <= 0.0F) {
			// lv1=4; lv5=10
			val size = ampFactor * 0.8 + 4.0
			entity.level.getNearbyEntities(
				LivingEntity::class.java,
				TargetingConditions.DEFAULT,
				entity,
				AABB.ofSize(entity.position(), size, size, size)
			).forEach {
				it.addEffect(instance(ampFactor * factorNextGenDuration, amp - 1))
			}
		}

		entity.level.runOnRemote {
			sendParticles(ParticleTypes.FLAME, entity.position(), 5, 1.0)
		}
	}
}