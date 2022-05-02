@file:Suppress("unused", "UNUSED_PARAMETER")

package restricted.fpe

import net.minecraft.world.item.*
import net.minecraft.world.level.block.Block
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import restricted.fpe.block.FireHydrantBlock
import restricted.fpe.item.FireHydrantItem
import restricted.fpe.item.FireItem
import thedarkcolour.kotlinforforge.forge.*

const val ModId = "fire_protection_equipment"

val logger: Logger = LogManager.getLogger(ModId)

@Mod(ModId)
object FPE {

	init {
		Blocks.registry.register(MOD_BUS)
		Items.registry.register(MOD_BUS)

		runForDist(
			clientTarget = { MOD_BUS.addListener(::clientSetup) },
			serverTarget = { MOD_BUS.addListener(::serverSetup) }
		)
	}

	private fun clientSetup(e: FMLClientSetupEvent) {
		e.enqueueWork {
		}
	}

	private fun serverSetup(e: FMLDedicatedServerSetupEvent) {
	}

	object Blocks {
		internal val registry: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModId)

		val FireHydrant by registry.registerObject("fire_hydrant") { FireHydrantBlock }
	}

	object Items {
		internal val registry: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ModId)

		// 模组图标
		val Fire by registry.registerObject("fire") { FireItem }

		val FireHydrant by registry.registerObject("fire_hydrant") { FireHydrantItem }
	}

	object Tabs {
		val Default = object : CreativeModeTab(ModId) {
			override fun makeIcon(): ItemStack = Items.Fire.defaultInstance
		}
	}
}