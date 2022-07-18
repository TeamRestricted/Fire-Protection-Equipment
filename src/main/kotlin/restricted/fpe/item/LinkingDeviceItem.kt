package restricted.fpe.item

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.*
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.*
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import restricted.fpe.*
import restricted.fpe.block.entity.AbstractHomeFireDevice
import restricted.fpe.block.entity.HomeFireStationBlockEntity
import restricted.fpe.util.ParticleUtils

object LinkingDeviceItem : Item(FPEConst.ItemConst.DefaultNonStackableItemProp) {

	const val TranslationBinding = "message.$ModId.linking.binding" // done with binding action
	const val TranslationSelected = "message.$ModId.linking.selected" // done to put selection info
	const val TranslationBoundNothing = "message.$ModId.linking.no-bound" // output of binding nothing
	const val TranslationMissingTarget = "message.$ModId.linking.missing-target"
	const val TranslationTooFarAway = "message.$ModId.linking.too_far_away"

	fun putSelectedControlPos(stack: ItemStack, pos: BlockPos?) {
		if(pos == null) {
			stack.orCreateTag.remove("linking_device_selected")
		} else {
			stack.orCreateTag.putLong("linking_device_selected", pos.asLong())
		}
	}

	fun getSelectedControlPos(stack: ItemStack): BlockPos? {
		return if(stack.tag?.contains("linking_device_selected") == true) {
			val posLong = stack.tag?.getLong("linking_device_selected")
			if(posLong == null) null else BlockPos.of(posLong)
		} else {
			null
		}
	}

	override fun useOn(ctx: UseOnContext): InteractionResult {
		if(ctx.level.isClientSide) return InteractionResult.CONSUME

		val level = ctx.level
		val pos = ctx.clickedPos
		val thisBe = ctx.level.getBlockEntity(pos)
		val stack = ctx.itemInHand

		if(ctx.isSecondaryUseActive) { // binding mode
			if(thisBe is AbstractHomeFireDevice<*>) { // device
				val selectedPos = getSelectedControlPos(stack)
				if(selectedPos == null) {
					ctx.player?.sendMessage(TranslatableComponent(TranslationMissingTarget), Util.NIL_UUID)
				} else {
					val selectedBe = level.getBlockEntity(selectedPos)
					if(selectedBe !is HomeFireStationBlockEntity) {
						ctx.player?.sendMessage(TextComponent("Invalid Target!"), Util.NIL_UUID)
					} else {
						if(pos.closerThan(selectedPos, 16.0)) {
							thisBe.bind(selectedBe)
							level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F)
							ParticleUtils.getRoutingVec3s(thisBe.blockPos.vec3, selectedBe.blockPos.vec3, 0.5).forEach {
								level.runOnRemote {
									sendParticles(ParticleTypes.FLAME, it, 10, 0.0)
								}
							}
						} else {
							// too far away
							ctx.player?.sendMessage(TranslatableComponent(TranslationTooFarAway), Util.NIL_UUID)
							level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F)
						}
					}
				}
			} else if(thisBe is HomeFireStationBlockEntity) {
				putSelectedControlPos(stack, thisBe.blockPos)
			} else {
				putSelectedControlPos(stack, null)
			}
			return InteractionResult.CONSUME
		} else { // query
			when(thisBe) {
				is AbstractHomeFireDevice<*> -> { // device
					val boundToLocation = thisBe.boundToLocation
					if(boundToLocation != null) {
						val boundBe = level.getBlockEntity(boundToLocation)
						if(boundBe != null) {
							ParticleUtils.getRoutingVec3s(thisBe.blockPos.vec3, boundBe.blockPos.vec3, 0.5).forEach {
								level.runOnRemote {
									sendParticles(ParticleTypes.FLAME, it, 10, 0.0)
								}
							}
						} else {
							putSelectedControlPos(stack, null)
							ctx.player?.sendMessage(TranslatableComponent(TranslationBoundNothing), Util.NIL_UUID)
						}
					} else {
						ctx.player?.sendMessage(TranslatableComponent(TranslationBoundNothing), Util.NIL_UUID)
					}
					return InteractionResult.CONSUME
				}
				is HomeFireStationBlockEntity -> {
					val devices = thisBe.connectedDevicesPos.map(BlockPos::of)
					if(devices.isEmpty()) {
						ctx.player?.sendMessage(TranslatableComponent(TranslationBoundNothing), Util.NIL_UUID)
					} else {
						devices.forEach {
							ParticleUtils.getRoutingVec3s(thisBe.blockPos.vec3, it.vec3, 0.5).forEach {
								level.runOnRemote {
									sendParticles(ParticleTypes.FLAME, it, 10, 0.0)
								}
							}
						}
					}
					return InteractionResult.CONSUME
				}
				else -> {
					return InteractionResult.PASS
				}
			}
		}
	}

	override fun appendHoverText(
		stack: ItemStack,
		level: Level?,
		tooltipComponents: MutableList<Component>,
		isAdvanced: TooltipFlag
	) {
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
		val posMessage = kotlin.run {
			val pos = getSelectedControlPos(stack)
			if(pos == null) {
				TextComponent("Unbound")
			} else {
				if(level != null) {
					val blockNameComp = level.getBlockState(pos).block.name
					TextComponent("${pos.x}/${pos.y}/${pos.z}(").append(blockNameComp).append(")")
				} else {
					TextComponent("${pos.x}/${pos.y}/${pos.z}")
				}
			}
		}
		tooltipComponents += posMessage
	}

	override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
		val pos = getSelectedControlPos(pStack)
		if(pos != null) {
			val be = pLevel.getBlockEntity(pos)
			if(be !is HomeFireStationBlockEntity) {
				putSelectedControlPos(pStack, null)
			}
		}
		super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected)
	}

}