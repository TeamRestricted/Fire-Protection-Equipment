@file:Suppress("unused", "UNUSED_PARAMETER")

package restricted.fpe

import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.*
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.placement.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import restricted.fpe.advancements.critereon.BlockExtinguishedTrigger
import restricted.fpe.advancements.critereon.CriteriaUtils
import restricted.fpe.block.*
import restricted.fpe.block.entity.*
import restricted.fpe.enchant.*
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
		ParticleTypes.registry.register(MOD_BUS)
		BlockEntityTypes.registry.register(MOD_BUS)
		Blocks.registry.register(MOD_BUS)
		Items.registry.register(MOD_BUS)

		runForDist(
			clientTarget = { MOD_BUS.addListener(::clientSetup) },
			serverTarget = { MOD_BUS.addListener(::serverSetup) }
		)

		MOD_BUS.addListener<FMLCommonSetupEvent> {
			 registerFeatures()
		}

		BuiltInRecipes.register()
		CriteriaUtils.registerCustomTriggers()
	}

	private fun clientSetup(e: FMLClientSetupEvent) {
		e.enqueueWork {
		}
	}

    private fun serverSetup(e: FMLDedicatedServerSetupEvent) {
        e.enqueueWork {
        }
    }

    private fun registerFeatures() {
        FPEConst.Features.NaturalFireHydrant = FeatureUtils.register(
            "$ModId:fire_hydrant",
            Feature.FLOWER,
            RandomPatchConfiguration(
                96,
                0,
                0,
                PlacementUtils.onlyWhenEmpty(
                    Feature.SIMPLE_BLOCK,
                    SimpleBlockConfiguration(
                        BlockStateProvider.simple(
                            Blocks.FireHydrant
                        )
                    )
                )
            )
        )

        FPEConst.Placements.NaturalFireHydrant =
            PlacementUtils.register(
                "$ModId:fire_hydrant",
                FPEConst.Features.NaturalFireHydrant,
                RarityFilter.onAverageOnceEvery(32),//决定稀有度，值越大越稀有
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                InSquarePlacement.spread(),
                BiomeFilter.biome()
            )
    }

	object Blocks {
		internal val registry: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModId)

		val FireHydrant by registry.registerObject("fire_hydrant") { FireHydrantBlock }
		val FireExtinguishingBomb by registry.registerObject("fire_extinguishing_bomb") { FireExtinguishingBombBlock }
		val FireDetector by registry.registerObject("fire_detector") { FireDetectorBlock }
		val FireSprinkler by registry.registerObject("fire_sprinkler") { FireSprinklerBlock }
		val FireAlarmControlUnit by registry.registerObject("fire_alarm_control_unit") { FireAlarmControlUnitBlock }
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
		val FireAlarmControlUnit by registry.registerObject("fire_alarm_control_unit") { Blocks.FireAlarmControlUnit.generateBlockItem() }

		val FireExtinguisher by registry.registerObject("fire_extinguisher") { FireExtinguisherItem }
		val HoseNozzle by registry.registerObject("hose_nozzle") { HoseNozzleItem }
		val FireAlarmControlTerminal by registry.registerObject("fire_alarm_control_terminal") { FireAlarmControlTerminalItem }
		val HandCrankSiren by registry.registerObject("hand_crank_siren") { HandCrankSirenItem }
		val LinkingDevice by registry.registerObject("linking_device") { LinkingDeviceItem }

		val FurnaceFireProtectionDevice by registry.registerObject("furnace_fire_protection_device") { FurnaceFireProtectionDeviceItem }

		val DryChemicalPowder by registry.registerObject("dry_chemical_powder") { buildItem() }

		// suit
		val FirefightersHelmet by registry.registerObject("firefighters_helmet") {
			ArmorItem(
				FPEConst.FirefighterSuitMaterials,
				EquipmentSlot.HEAD,
				FPEConst.ItemConst.DefaultNonStackableItemProp
			)
		}
		val FirefightersChestplate by registry.registerObject("firefighters_chestplate") {
			ArmorItem(
				FPEConst.FirefighterSuitMaterials,
				EquipmentSlot.CHEST,
				FPEConst.ItemConst.DefaultNonStackableItemProp
			)
		}
		val FirefightersLeggings by registry.registerObject("firefighters_leggings") {
			ArmorItem(
				FPEConst.FirefighterSuitMaterials,
				EquipmentSlot.LEGS,
				FPEConst.ItemConst.DefaultNonStackableItemProp
			)
		}
		val FirefightersBoots by registry.registerObject("firefighters_boots") {
			ArmorItem(
				FPEConst.FirefighterSuitMaterials,
				EquipmentSlot.FEET,
				FPEConst.ItemConst.DefaultNonStackableItemProp
			)
		}
	}

	object Tabs {
		val Default = object : CreativeModeTab(ModId) {
			override fun makeIcon(): ItemStack = Items.Fire.defaultInstance
		}
	}

	object Enchants {
		internal val registry: DeferredRegister<Enchantment> =
			DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ModId)

		val FireHaste by registry.registerObject("fire_haste") { FireHasteEnchant }
		val Blaze by registry.registerObject("blaze") { BlazeEnchant }
		val Hothead by registry.registerObject("hothead") { HotheadEnchant }
		val HallowfireHeart by registry.registerObject("hallowfire_heart") { HallowfireHeartEnchant }
	}

	object MobEffects {
		internal val registry: DeferredRegister<MobEffect> = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModId)

		val SpreadingFire by registry.registerObject("spreading_fire") { SpreadingFireEffect }
	}

	@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
	object BlockEntityTypes {
		internal val registry: DeferredRegister<BlockEntityType<*>> =
			DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ModId)

		val FireDetector: BlockEntityType<FireDetectorBlockEntity> by registry.registerObject("fire_detector") {
			BlockEntityType.Builder.of(
				::FireDetectorBlockEntity,
				Blocks.FireDetector
			).build(null)
		}
		val FireSprinkler: BlockEntityType<FireSprinklerBlockEntity> by registry.registerObject("fire_sprinkler") {
			BlockEntityType.Builder.of(
				::FireSprinklerBlockEntity,
				Blocks.FireSprinkler
			).build(null)
		}
		val HomeFireStation: BlockEntityType<HomeFireStationBlockEntity> by registry.registerObject("home_fire_station") {
			BlockEntityType.Builder.of(
				::HomeFireStationBlockEntity,
				Blocks.FireAlarmControlUnit
			).build(null)
		}
	}

	object ParticleTypes {
		internal val registry = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ModId)

		val WaterFluid by registry.registerObject("water_fluid") { SimpleParticleType(false) }
	}

	@JvmStatic
	fun extinguishFire(context: ExtinguishContext) {
		val extinType = context.type
		context.boundingBox.forEach {
			val state = context.level.getBlockState(it)
			val func = ExtinguishRecipe[state, extinType]
			if(func != null) {
				context.level.runOnRemote {
					if(context.player != null) {
						BlockExtinguishedTrigger.trigger(
							context.player as ServerPlayer,
							context.level as ServerLevel,
							it,
							context.itemstack ?: ItemStack.EMPTY
						)
					}
				}
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
		// play animation
		ExtinguishRecipe.getForAnimation(extinType)(context)
	}
}