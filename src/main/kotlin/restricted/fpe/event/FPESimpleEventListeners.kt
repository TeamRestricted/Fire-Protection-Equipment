@file:Mod.EventBusSubscriber

package restricted.fpe.event

import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.npc.VillagerTrades
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.trading.MerchantOffer
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.CampfireBlock
import net.minecraft.world.level.block.FireBlock
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDamageEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.village.VillagerTradesEvent
import net.minecraftforge.event.world.BiomeLoadingEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import restricted.fpe.*
import restricted.fpe.advancements.critereon.PlayerIgnitedTrigger
import restricted.fpe.enchant.HallowfireHeartEnchant
import restricted.fpe.enchant.HotheadEnchant
import restricted.fpe.item.FireExtinguisherItem
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
	val entity = e.entityLiving
	if(entity is Player) {
		if(e.source == DamageSource.IN_FIRE) {
			if((0..5).random() == 0) {
				boundingBoxOfCenter(entity.blockPosition(), 3).forEach {
					val level = entity.level
					val state = level.getBlockState(it)
					if(state.isFlammable(level, it, Direction.UP)) {
						level.setBlockAndUpdate(it.above(), MinecraftBlocks.FIRE.defaultBlockState())
						level.runOnRemote {
							PlayerIgnitedTrigger.trigger(entity as ServerPlayer)
						}
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
		val hotHeadLevel = EnchantmentHelper.getEnchantmentLevel(FPE.Enchants.Hothead, attacker)
		if(hotHeadLevel > 0) {
			if(attacker.isOnFire) {
				e.amount += HotheadEnchant.getDamageBonus(hotHeadLevel, e.entityLiving.mobType)
			}
		}
	}
}

@SubscribeEvent
fun onPlayerTick(e: TickEvent.PlayerTickEvent) {
	if(e.player.isOnFire) {
		HallowfireHeartEnchant.tryRepair(e.player)
	}
}

@SubscribeEvent
fun onBiomeLoading(e: BiomeLoadingEvent) {
	if(e.category == Biome.BiomeCategory.NETHER || e.category == Biome.BiomeCategory.THEEND)
		return
	e.generation.getFeatures(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
		.add(FPEConst.Placements.NaturalFireHydrant)
	// e.generation.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FPEConst.Placements.NaturalFireHydrant)
}

@SubscribeEvent
fun addCustomTrades(e: VillagerTradesEvent) {
	if(e.type == FPE.VillagerProfessions.Firefighter) {
		e.trades.get(1).add(VillagerTrades.ItemListing { _, _ ->
			MerchantOffer(ItemStack(MinecraftItems.EMERALD, 5), FireExtinguisherItem.FireSavior.copy(), 4, 12, 0.09F)
		})
	}
}