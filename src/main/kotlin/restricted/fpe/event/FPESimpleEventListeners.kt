@file:Mod.EventBusSubscriber

package restricted.fpe.event

import net.minecraft.core.Direction
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.block.CampfireBlock
import net.minecraft.world.level.block.FireBlock
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import restricted.fpe.*
import restricted.fpe.enchant.HotHeadEnchant
import kotlin.random.Random

@SubscribeEvent
fun onInteraction(e: PlayerInteractEvent.RightClickBlock) {
	// 铲子灭火燃 1s
	if(e.itemStack.item is ShovelItem) {
		val state = e.world.getBlockState(e.pos)
		if(state.block is CampfireBlock) {
			if(state.getValue(CampfireBlock.LIT)) {
				if(Random.nextInt(100) > 90) {
					e.player.setSecondsOnFire(1)
				}
			}
		}
	}
}

@SubscribeEvent
fun onBlockBreak(e: BlockEvent.BreakEvent) {
	// 用手扑灭火受到 21s 烧灼
	if(e.world.getBlockState(e.pos).block is FireBlock) {
		e.player.setSecondsOnFire(20)
	}
}

@SubscribeEvent
fun onPlayerHurt(e: LivingHurtEvent) {
	val entity = e.entity
	if(entity is Player) {
		if(e.source == DamageSource.ON_FIRE) {
			if((0..5).random() == 0) { // 概率：1/6
				boundingBoxOfCenter(entity.blockPosition(), 3).forEach {
					val level = entity.level
					val state = level.getBlockState(it)
					if(state.isFlammable(level, it, Direction.UP)) {
						level.setBlockAndUpdate(it.above(), MinecraftBlocks.FIRE.defaultBlockState())
					}
				}
			}
		}
	}
}

@SubscribeEvent
fun onPlayerHurtEntity(e: LivingDamageEvent) {
	val attacker = e.source.entity
	if(attacker is Player) {
		val hotHeadLevel = EnchantmentHelper.getEnchantmentLevel(FPE.Enchants.HotHead, attacker)
		if(hotHeadLevel > 0) {
			if(attacker.isOnFire) {
				e.amount += HotHeadEnchant.getDamageBonus(hotHeadLevel, e.entityLiving.mobType)
			}
		}
	}
}