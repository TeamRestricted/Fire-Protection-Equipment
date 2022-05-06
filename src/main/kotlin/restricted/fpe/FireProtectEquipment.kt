@file:Suppress("unused", "UNUSED_PARAMETER")

package restricted.fpe

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.*
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import restricted.fpe.block.FireHydrantBlock
import restricted.fpe.enchant.FireWalkerEnchant
import restricted.fpe.enchant.SpreadingFireEnchant
import restricted.fpe.extinguish.ExtinguishContext
import restricted.fpe.extinguish.ExtinguishRecipe
import restricted.fpe.extinguish.ExtinguishRecipe.MiniBlockState.Builder.Companion.buildMiniState
import restricted.fpe.item.FireExtinguisherItem
import restricted.fpe.item.FireItem
import restricted.fpe.potion.SpreadingFireEffect
import thedarkcolour.kotlinforforge.forge.*
import kotlin.math.log

const val ModId = "fire_protection_equipment"

val logger: Logger = LogManager.getLogger(ModId)

@Mod(ModId)
object FPE {

	init {
		Enchants.registry.register(MOD_BUS)
		MobEffects.registry.register(MOD_BUS)
		Blocks.registry.register(MOD_BUS)
		Items.registry.register(MOD_BUS)

		runForDist(
			clientTarget = { MOD_BUS.addListener(::clientSetup) },
			serverTarget = { MOD_BUS.addListener(::serverSetup) }
		)

		registerFireRecipes()
	}

	private fun clientSetup(e: FMLClientSetupEvent) {
		e.enqueueWork {
		}
	}

	private fun serverSetup(e: FMLDedicatedServerSetupEvent) {
	}

	private fun registerFireRecipes() {
		logger.info("Registering Fire Recipes")

		ExtinguishRecipe.register(MinecraftBlocks.FIRE.buildMiniState()) { ctx, _, pos ->
			ctx.world.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), buildSetBlockFlag(updateBlock = true, sendToClient = true))
			ctx.world.addParticle(ParticleTypes.SMOKE, pos.vec3, 0.0, 0.0, 0.0)
			println("World = ${ctx.world is ServerLevel}")
			ctx.world.runOnRemote {
				val v3 = pos.vec3
				sendParticles(ParticleTypes.CLOUD, v3.x, v3.y, v3.z, (1..20).random(), 0.0, 0.0, 0.0, 0.2)
			}
		}

		logger.info("End register")
		ExtinguishRecipe.recipes.cellSet().forEach { (state, type, func) ->
			logger.info("$state & $type => $func")
		}// TODO: Removal
	}

	object Blocks {
		internal val registry: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModId)

		val FireHydrant by registry.registerObject("fire_hydrant") { FireHydrantBlock }
	}

	object Items {
		internal val registry: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ModId)

		// 模组图标
		val Fire by registry.registerObject("fire") { FireItem }

		val FireHydrant by registry.registerObject("fire_hydrant") { Blocks.FireHydrant.generateBlockItem() }
		val BrokenFireHydrant by registry.registerObject("broken_fire_hydrant") { buildItem() }

		val FireExtinguisher by registry.registerObject("fire_extinguisher") { FireExtinguisherItem }
	}

	object Tabs {
		val Default = object : CreativeModeTab(ModId) {
			override fun makeIcon(): ItemStack = Items.Fire.defaultInstance
		}
	}

	object Enchants {
		internal val registry: DeferredRegister<Enchantment> =
			DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ModId)

		val FireWalker by registry.registerObject("fire_walker") { FireWalkerEnchant }

		val SpreadingFire by registry.registerObject("spreading_fire") { SpreadingFireEnchant }
	}

	object MobEffects {
		internal val registry: DeferredRegister<MobEffect> = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModId)

		val SpreadingFire by registry.registerObject("spreading_fire") { SpreadingFireEffect }
	}

	fun extinguishFire(context: ExtinguishContext) {
		context.boundingBox.forEach {
			val state = context.world.getBlockState(it)
			val func = ExtinguishRecipe[state, context.type]
			if(func != null) {
				func(context, state, it)
				println("$state & ${context.type.name} = $func") // TODO: Removal
			}
		}
	}
}