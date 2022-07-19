package restricted.fpe.extinguish

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.MagmaCube
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.registries.ForgeRegistries
import restricted.fpe.*
import restricted.fpe.extinguish.ExtinguishRecipe.MiniBlockState.Builder.Companion.buildMiniState
import restricted.fpe.util.ParticleUtils

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

		ForgeRegistries.ENTITIES.values.forEach {
			// 普通对生物灭火
			ExtinguishRecipe.registerEntity(it, ExtinguishType.WATER) { _, entity ->
				entity.clearFire()
			}
			// “救”火器
			ExtinguishRecipe.registerEntity(it, ExtinguishType.FIRE_SAVIOR) { _, entity ->
				entity.remainingFireTicks += 200
			}
		}

		// 烈焰人消失术
		ExtinguishRecipe.builder(EntityType.BLAZE) {
			// 被干冰灭火器击杀
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

			// 烈焰人免疫灭火伤害
			otherwise { ctx, entity ->
				ctx.level.runOnRemote {
					sendParticles(ParticleTypes.SMOKE, entity.eyePosition, 20, 0.2)
				}
			}
		}

		ExtinguishRecipe.builder(EntityType.MAGMA_CUBE) {
			// 岩浆怪变史莱姆
			water { ctx, magma ->
				ctx.level.runOnRemote {
					if(magma is MagmaCube) {
						playSound(null, magma, SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F)
						sendParticles(ParticleTypes.EXPLOSION, magma.eyePosition, 20, 0.2)
						val magmaSize = magma.size
						val slime = EntityType.SLIME.create(
							this, null, null, ctx.player, magma.onPos, MobSpawnType.TRIGGERED, true, true
						)!!
						slime.yBodyRot = magma.yBodyRot
						slime.setSize(magmaSize, true)
						magma.discard()
						addFreshEntity(slime)
					}
				}
			}
			// 对岩浆怪造成伤害
			otherwise { ctx, entity ->
				val damage = 10.0F - entity.position().distanceToSqr(ctx.centerPos).toFloat() * 0.5F
				entity.hurt(FPEConst.DamageSourceConst.Extinguish, damage)
			}
		}

		// 对炽足兽造成伤害
		ExtinguishRecipe.registerEntity(EntityType.STRIDER) { ctx, entity ->
			val damage = 10.0F - entity.position().distanceToSqr(ctx.centerPos).toFloat() * 0.5F
			entity.hurt(FPEConst.DamageSourceConst.Extinguish, damage)
		}

		// 僵尸变溺尸 TODO: 改成被喷死
		ExtinguishRecipe.builder(EntityType.ZOMBIE) {
			water { ctx, zombie ->
				if(zombie.level.random.nextFloat() > 0.95F && zombie is Zombie) {
					zombie.level.runOnRemote {
						playSound(null, zombie, SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F)
						sendParticles(ParticleTypes.EXPLOSION, zombie.eyePosition, 20, 0.2)
						val drowned = EntityType.DROWNED.create(this, null, null, ctx.player, zombie.onPos, MobSpawnType.TRIGGERED, true, true)!!
						drowned.yBodyRot = zombie.yBodyRot
						zombie.discard()
						addFreshEntity(drowned)
					}
				}
			}
		}

		ExtinguishRecipe.recipes.cellSet().forEach { (state, type, func) ->
			logger.debug("[BlockExtinguishRecipe] $state & $type => ${func.hashCode()}")
		}

		ExtinguishRecipe.recipesEntity.cellSet().forEach { (entityType, type, func) ->
			logger.debug("[EntityExtinguishRecipe] ${entityType.registryName} & $type => ${func.hashCode()}")
		}

		ExtinguishRecipe.registerAnimation(ExtinguishType.FIRE_SAVIOR) { ctx ->
			ParticleUtils.getFlexibleCircleVec3s(ctx.centerPos.add(0.0, 0.5, 0.0), ctx.size.toDouble(), 20).forEach {
				ctx.level.runOnRemote {
					sendParticles(ParticleTypes.FLAME, it, 1, 0.0)
				}
			}
		}

		ExtinguishRecipe.registerAnimation(ExtinguishType.WATER) { ctx ->
			ParticleUtils.getFlexibleCircleVec3s(ctx.centerPos.add(0.0, 0.5, 0.0), ctx.size.toDouble(), 20).forEach {
				ctx.level.runOnRemote {
					sendParticles(FPE.ParticleTypes.WaterFluid, it, 1, 0.0)
				}
			}
		}

		// TODO: 改粒子效果
		ExtinguishRecipe.registerAnimation(ExtinguishType.DRY_ICE) { ctx ->
			ParticleUtils.getFlexibleCircleVec3s(ctx.centerPos.add(0.0, 0.5, 0.0), ctx.size.toDouble(), 20).forEach {
				ctx.level.runOnRemote {
					sendParticles(FPE.ParticleTypes.WaterFluid, it, 1, 0.0)
				}
			}
		}

		// TODO: 改粒子效果
		ExtinguishRecipe.registerAnimation(ExtinguishType.DRY_CHEMICAL) { ctx ->
			ParticleUtils.getFlexibleCircleVec3s(ctx.centerPos.add(0.0, 0.5, 0.0), ctx.size.toDouble(), 20).forEach {
				ctx.level.runOnRemote {
					sendParticles(FPE.ParticleTypes.WaterFluid, it, 1, 0.0)
				}
			}
		}

		// TODO: 改粒子效果
		ExtinguishRecipe.registerAnimation(ExtinguishType.FOAMS) { ctx ->
			ParticleUtils.getFlexibleCircleVec3s(ctx.centerPos.add(0.0, 0.5, 0.0), ctx.size.toDouble(), 20).forEach {
				ctx.level.runOnRemote {
					sendParticles(FPE.ParticleTypes.WaterFluid, it, 1, 0.0)
				}
			}
		}

		logger.debug("Done register Extinguish Recipes")
	}

}