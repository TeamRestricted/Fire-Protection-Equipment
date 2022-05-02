@file:Mod.EventBusSubscriber

package restricted.fpe.event

import com.mojang.authlib.minecraft.client.MinecraftClient
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.level.block.CampfireBlock
import net.minecraft.world.level.block.FireBlock
import net.minecraft.world.phys.AABB
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import restricted.fpe.FPE
import kotlin.random.Random

@SubscribeEvent
fun onInteraction(e: PlayerInteractEvent.RightClickBlock) {
	// 用手扑灭火受到 21s 烧灼
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
	if(e.world.getBlockState(e.pos).block is FireBlock) {
		e.player.setSecondsOnFire(20)
	}
}