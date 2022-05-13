package restricted.fpe.extinguish

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.MagmaCube
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import restricted.fpe.*
import restricted.fpe.extinguish.ExtinguishRecipe.MiniBlockState.Builder.Companion.buildMiniState
import java.lang.reflect.Modifier

object BuiltInRecipes {

	fun register() {
		logger.debug("Register Extinguish Recipes")

		ExtinguishRecipe.registerBlock(MinecraftBlocks.FIRE.buildMiniState(), ExtinguishRecipe.directly())
		ExtinguishRecipe.registerBlock(MinecraftBlocks.SOUL_FIRE.buildMiniState(), ExtinguishRecipe.directly())
		ExtinguishRecipe.registerBlock(MinecraftBlocks.CAMPFIRE.buildMiniState { with(BlockStateProperties.LIT, true) },
			ExtinguishRecipe.replaceWithBlock(
				MinecraftBlocks.CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, false)
			)
		)
		ExtinguishRecipe.registerBlock(MinecraftBlocks.SOUL_CAMPFIRE.buildMiniState { with(BlockStateProperties.LIT, true) },
			ExtinguishRecipe.replaceWithBlock(
				MinecraftBlocks.SOUL_CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, false)
			)
		)
		ExtinguishRecipe.registerBlock(MinecraftBlocks.NETHER_PORTAL.buildMiniState(), ExtinguishRecipe.directly())

		ExtinguishRecipe.builder(EntityType.BLAZE) {
			dryIce { ctx, entity ->
				ctx.level.runOnRemote {
					if(entity.isAlive) {
						entity.hurt(FPEConst.DamageSourceConst.Extinguish, 100.0F)
						val itemEntity = ItemEntity(this, entity.x, entity.y, entity.z, ItemStack(MinecraftItems.BLAZE_ROD, (6..8).random()))
						addFreshEntity(itemEntity)
					}
				}
			}
			otherwise { ctx, entity ->
				ctx.level.runOnRemote {
					sendParticles(ParticleTypes.SMOKE, entity.eyePosition, 20, 0.2)
				}
			}
		}

		ExtinguishRecipe.builder(EntityType.MAGMA_CUBE) {
			water { ctx, magma ->
				ctx.level.runOnRemote {
					if(magma is MagmaCube) {
						playSound(null, magma, SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F)
						sendParticles(ParticleTypes.EXPLOSION, magma.eyePosition, 20, 0.2)
						magma.tags
						val slime = EntityType.SLIME.create(this, null, null, ctx.player, magma.onPos, MobSpawnType.TRIGGERED, true, true)!!
						slime.yBodyRot = magma.yBodyRot
						magma.discard()
						addFreshEntity(slime)
					}
				}
			}
		}

		EntityType::class.java.declaredFields.forEach {
			runCatching {
				it.trySetAccessible()
				if(Modifier.isStatic(it.modifiers) && Modifier.isFinal(it.modifiers) && it.type == EntityType::class.java) {
					ExtinguishRecipe.registerEntity(it.get(null) as EntityType<*>, ExtinguishType.WATER) { _, entity ->
						entity.clearFire()
					}
				}
			}.onFailure { error ->
				logger.warn("Unable to register the Water-Extinguish-Fire recipe for ${it.name}", error)
			}
		}

		ExtinguishRecipe.recipes.cellSet().forEach { (state, type, func) ->
			logger.debug("[BlockExtinguishRecipe] $state & $type => ${func.hashCode()}")
		}

		ExtinguishRecipe.recipesEntity.cellSet().forEach { (entityType, type, func) ->
			logger.debug("[EntityExtinguishRecipe] ${entityType.registryName} & $type => ${func.hashCode()}")
		}

		logger.debug("Done register Extinguish Recipes")
	}

}