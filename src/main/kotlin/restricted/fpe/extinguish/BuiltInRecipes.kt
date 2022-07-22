package restricted.fpe.extinguish

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Fox
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.*
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
						val slime = magma.convertTo(EntityType.SLIME, true)
//						val magmaSize = magma.size
//						val slime = EntityType.SLIME.create(
//							this, null, null, ctx.player, magma.onPos, MobSpawnType.TRIGGERED, true, true
//						)!!
//						slime.yBodyRot = magma.yBodyRot
//						slime.setSize(magmaSize, true)
//						magma.discard()
//						addFreshEntity(slime)
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
					ctx.level.runOnRemote {
						playSound(null, zombie, SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F)
						sendParticles(ParticleTypes.EXPLOSION, zombie.eyePosition, 20, 0.2)
						zombie.convertTo(EntityType.DROWNED, true)
//						val drowned = EntityType.DROWNED.create(this, null, null, ctx.player, zombie.onPos, MobSpawnType.TRIGGERED, true, true)!!
//						drowned.yBodyRot = zombie.yBodyRot
//						zombie.discard()
//						addFreshEntity(drowned)
					}
				}
			}
		}

		// 史莱姆变岩浆怪
		ExtinguishRecipe.registerEntity(EntityType.SLIME, ExtinguishType.FIRE_SAVIOR) { ctx, slime ->
			ctx.level.runOnRemote {
				if(slime is Slime) {
					playSound(null, slime, SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F)
					sendParticles(ParticleTypes.EXPLOSION, slime.eyePosition, 20, 0.2)
					slime.convertTo(EntityType.MAGMA_CUBE, true)
//					val slimeSize = slime.size
//					val magma = EntityType.MAGMA_CUBE.create(
//						this, null, null, ctx.player, slime.onPos, MobSpawnType.TRIGGERED, true, true
//					)!!
//					magma.yBodyRot = slime.yBodyRot
//					magma.setSize(slimeSize, true)
//					slime.discard()
//					addFreshEntity(magma)
				}
			}
		}

		// 骷髅变流浪者
		ExtinguishRecipe.registerEntity(EntityType.SKELETON, ExtinguishType.DRY_ICE) { ctx, skeleton ->
			ctx.level.runOnRemote {
				if(skeleton is Skeleton) {
					playSound(null, skeleton, SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F)
					sendParticles(ParticleTypes.EXPLOSION, skeleton.eyePosition, 20, 0.2)
					skeleton.convertTo(EntityType.STRAY, true)
				}
			}
		}

		// 红狐狸变雪狐狸
		// 狐狸真是太棒了，多来点！
		ExtinguishRecipe.builder(EntityType.FOX) {
			otherwise { ctx, fox ->
				ctx.level.runOnRemote {
					if(fox is Fox && fox.foxType == Fox.Type.RED) {
						playSound(null, fox, SoundEvents.WATER_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F)
						sendParticles(ParticleTypes.EXPLOSION, fox.eyePosition, 20, 0.2)
						fox.foxType = Fox.Type.SNOW
					}
				}
			}
		}

		// 干冰秒潜影贝
		ExtinguishRecipe.registerEntity(EntityType.SHULKER, ExtinguishType.DRY_ICE) { ctx, shulker ->
			if(shulker is Shulker) {
				ctx.level.runOnRemote {
					if(shulker.isAlive) {
						shulker.hurt(FPEConst.DamageSourceConst.Extinguish, 100.0F)
						val itemEntity = ItemEntity(
							this, shulker.x, shulker.y, shulker.z, ItemStack(MinecraftItems.SHULKER_SHELL, (2..3).random())
						)
						addFreshEntity(itemEntity)
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