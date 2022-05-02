@file:Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

package restricted.fpe.datagen

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent

@SubscribeEvent
fun gatherData(e: GatherDataEvent) {
}