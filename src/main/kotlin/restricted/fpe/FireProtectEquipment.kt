@file:Suppress("unused", "UNUSED_PARAMETER")

package restricted.fpe

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.item.*
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import restricted.fpe.api.FireProtectionApiImpl
import restricted.fpe.api.FireType
import restricted.fpe.block.FireHydrantBlock
import restricted.fpe.enchant.FireWalkerEnchant
import restricted.fpe.enchant.SpreadingFireEnchant
import restricted.fpe.extinguish.ExtinguishContext
import restricted.fpe.item.FireExtinguisherItem
import restricted.fpe.item.FireItem
import restricted.fpe.potion.SpreadingFireEffect
import thedarkcolour.kotlinforforge.forge.*

const val ModId = "fire_protection_equipment"

val logger: Logger = LogManager.getLogger(ModId)

@Mod(ModId)
object FPE {

	val apiImpl = FireProtectionApiImpl

	init {
		Enchants.registry.register(MOD_BUS)
		MobEffects.registry.register(MOD_BUS)
		Blocks.registry.register(MOD_BUS)
		Items.registry.register(MOD_BUS)

		runForDist(
			clientTarget = { MOD_BUS.addListener(::clientSetup) },
			serverTarget = { MOD_BUS.addListener(::serverSetup) }
		)

		apiImpl.registerFireBlock(MinecraftBlocks.FIRE, FireType.NORMAL_FIRE)
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

	@Deprecated(
		"", ReplaceWith(
			"extinguishFire(ExtinguishContext(world, loc, level, extinguishType))",
			"restricted.fpe.FPE.extinguishFire",
			"restricted.fpe.extinguish.ExtinguishContext"
		)
	)
	fun extinguishFire(
		world: Level,
		loc: BlockPos,
		level: Int,
		extinguishType: FireType = FireType.NORMAL_FIRE
	) {
		extinguishFire(ExtinguishContext(world, loc, level, extinguishType))
	}

	fun extinguishFire(context: ExtinguishContext) {
		boundingBoxOfCenter(context.centerPos, context.extinguishLevel).forEach {
			val state = context.world.getBlockState(it)
			val block = state.block
			val fireType = block.fireType
			if(fireType != null) {
				if(fireType == context.extinguishType) {
					context.world.setBlock(it, MinecraftBlocks.AIR.defaultBlockState(), 3)
				} else {
					context.world.addParticle(
						ParticleTypes.SMOKE,
						it.x.toDouble(),
						it.y.toDouble(),
						it.z.toDouble(),
						0.0,
						0.0,
						0.0
					)
				}
			}
		}
	}
}