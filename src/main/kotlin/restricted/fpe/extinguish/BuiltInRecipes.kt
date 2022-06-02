package restricted.fpe.extinguish

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.MagmaCube
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.registries.ForgeRegistries
import restricted.fpe.*
import restricted.fpe.extinguish.ExtinguishRecipe.MiniBlockState.Builder.Companion.buildMiniState

object BuiltInRecipes {

	fun register() {
		logger.debug("Register Extinguish Recipes")

		fun Block.ofLit(lit: Boolean) = buildMiniState {
			with(BlockStateProperties.LIT, lit)
		}

		ExtinguishRecipe.registerDirectlyExtinguish(MinecraftBlocks.FIRE.buildMiniState())
		ExtinguishRecipe.registerDirectlyExtinguish(MinecraftBlocks.SOUL_FIRE.buildMiniState())
		ExtinguishRecipe.registerReplaceBlock(
			MinecraftBlocks.CAMPFIRE.ofLit(true),
			MinecraftBlocks.CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, false)
		)
		ExtinguishRecipe.registerReplaceBlock(
			MinecraftBlocks.SOUL_CAMPFIRE.ofLit(true),
			MinecraftBlocks.SOUL_CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, false)
		)

		ExtinguishRecipe.registerDirectlyExtinguish(MinecraftBlocks.NETHER_PORTAL.buildMiniState())

		ExtinguishRecipe.builder(EntityType.BLAZE) {
			dryIce { ctx, entity ->
				ctx.level.runOnRemote {
					if(entity.isAlive) {
						entity.hurt(FPEConst.DamageSourceConst.Extinguish, 100.0F)
						val itemEntity = ItemEntity(
							this, entity.x, entity.y, entity.z, ItemStack(MinecraftItems.BLAZE_ROD, (6..8).random())
						)
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

		// 岩浆怪 => 史莱姆
		// FIXME: 设置史莱姆与岩浆怪相同大小
		ExtinguishRecipe.builder(EntityType.MAGMA_CUBE) {
			water { ctx, magma ->
				ctx.level.runOnRemote {
					if(magma is MagmaCube) {
						playSound(null, magma, SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F)
						sendParticles(ParticleTypes.EXPLOSION, magma.eyePosition, 20, 0.2)
						magma.tags
						val slime = EntityType.SLIME.create(
							this, null, null, ctx.player, magma.onPos, MobSpawnType.TRIGGERED, true, true
						)!!
						slime.yBodyRot = magma.yBodyRot
						magma.discard()
						addFreshEntity(slime)
					}
				}
			}
		}

		ForgeRegistries.ENTITIES.values.forEach {
			ExtinguishRecipe.registerEntity(it, ExtinguishType.WATER) { _, entity ->
				entity.clearFire()
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