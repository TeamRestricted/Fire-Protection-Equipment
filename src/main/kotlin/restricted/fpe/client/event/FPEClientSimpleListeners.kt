@file:Mod.EventBusSubscriber(Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

package restricted.fpe.client.event

import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import restricted.fpe.*
import restricted.fpe.client.particle.FPEParticleHelper

@SubscribeEvent
fun onRegisterParticleProvider(e: ParticleFactoryRegisterEvent) {
	logger.info("PARTICLE")
	Minecraft.getInstance().particleEngine.register(
		FPE.ParticleTypes.WaterFluid,
		FPEParticleHelper.Factory(MinecraftBlocks.PRISMARINE_BRICKS.defaultBlockState())
	)
}