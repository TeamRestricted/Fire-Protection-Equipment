@file:Suppress("unused", "UNUSED_PARAMETER")

package restricted.fpe

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.*
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import restricted.fpe.block.*
import restricted.fpe.block.entity.FireDetectorBlockEntity
import restricted.fpe.block.entity.FireSprinklerBlockEntity
import restricted.fpe.enchant.FireWalkerEnchant
import restricted.fpe.enchant.SpreadingFireEnchant
import restricted.fpe.extinguish.*
import restricted.fpe.item.*
import restricted.fpe.potion.SpreadingFireEffect
import thedarkcolour.kotlinforforge.forge.*

const val ModId = "fire_protection_equipment"

val logger: Logger = LogManager.getLogger(ModId)

@Mod(ModId)
object FPE {

	init {
		Enchants.registry.register(MOD_BUS)
		MobEffects.registry.register(MOD_BUS)
		BlockEntityTypes.registry.register(MOD_BUS)
		Blocks.registry.register(MOD_BUS)
		Items.registry.register(MOD_BUS)

		runForDist(
			clientTarget = { MOD_BUS.addListener(::clientSetup) },
			serverTarget = { MOD_BUS.addListener(::serverSetup) }
		)

		BuiltInRecipes.register()
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
		val FireExtinguishingBomb by registry.registerObject("fire_extinguishing_bomb") { FireExtinguishingBombBlock }
		val FireDetector by registry.registerObject("fire_detector") { FireDetectorBlock }
		val FireSprinkler by registry.registerObject("fire_sprinkler") { FireSprinklerBlock }
	}

	object Items {
		internal val registry: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ModId)

		// 模组图标
		val Fire by registry.registerObject("fire") { FireItem }

		val BrokenFireHydrant by registry.registerObject("broken_fire_hydrant") { buildItem() }

		// BlockItems
		val FireHydrant by registry.registerObject("fire_hydrant") { Blocks.FireHydrant.generateBlockItem() }
		val FireExtinguishingBomb by registry.registerObject("fire_extinguishing_bomb") { Blocks.FireExtinguishingBomb.generateBlockItem() }
		val FireDetector by registry.registerObject("fire_detector") { Blocks.FireDetector.generateBlockItem() }
		val FireSprinkler by registry.registerObject("fire_sprinkler") { Blocks.FireSprinkler.generateBlockItem() }

		val FireExtinguisher by registry.registerObject("fire_extinguisher") { FireExtinguisherItem }
		val HoseNozzle by registry.registerObject("hose_nozzle") { HoseNozzleItem }

		val FurnaceFireProtectionDevice by registry.registerObject("furnace_fire_protection_device") { FurnaceFireProtectionDeviceItem }

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

	@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
	object BlockEntityTypes {
		internal val registry: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ModId)

		val FireDetector: BlockEntityType<FireDetectorBlockEntity> by registry.registerObject("fire_detector") { BlockEntityType.Builder.of(::FireDetectorBlockEntity, Blocks.FireDetector).build(null) }
		val FireSprinkler: BlockEntityType<FireSprinklerBlockEntity> by registry.registerObject("fire_sprinkler") { BlockEntityType.Builder.of(::FireSprinklerBlockEntity, Blocks.FireSprinkler).build(null) }
	}

	fun extinguishFire(context: ExtinguishContext) {
		val extinType = context.type
		context.boundingBox.forEach {
			val state = context.level.getBlockState(it)
			val func = ExtinguishRecipe[state, extinType]
			if(func != null) {
				func(context, state, it)
				logger.debug("Executed the ExtinguishRecipe for $state with type $extinType")
			}
		}
		context.level.getEntitiesOfClass(Entity::class.java, context.boundingBox.AABB) { true }.forEach {
			val func = ExtinguishRecipe.getForEntity(it.type, extinType)
			if(func != null) {
				func(context, it)
				logger.debug("Executed the Entity ExtinguishRecipe for $it with type $extinType")
			}
		}
	}
}