package restricted.fpe.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import restricted.fpe.*

object FireAlarmControlTerminalItem : Item(FPEConst.ItemConst.DefaultNonStackableItemProp) {

	const val TAG_FIRE_STATION_POS = "binding_position"
	const val TAG_STATUS = "status"

	const val ON_FIRE: Byte = 2
	const val NOT_ON_FIRE: Byte = 1
	const val UNKNOWN: Byte = 0

	override fun getDescriptionId(pStack: ItemStack): String {
		return when(pStack.getBindingTag().getByte(TAG_STATUS)) {
			ON_FIRE -> super.getDescriptionId(pStack) + "_on_fire"
			NOT_ON_FIRE -> super.getDescriptionId(pStack) + "_not_on_fire"
			else -> super.getDescriptionId(pStack)
		}
	}

	override fun inventoryTick(pStack: ItemStack, level: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
		level.runOnRemote {
			val tag = pStack.getBindingTag()
			val bindLevel = server.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, ResourceLocation(tag.dim)))
			if(bindLevel != null) {
				bindLevel.getBlockEntity(BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), FPE.BlockEntityTypes.HomeFireStation).let { optFireStationEntity ->
					if(optFireStationEntity.isPresent) {
						tag.putByte(TAG_STATUS, if(optFireStationEntity.get().onFire) ON_FIRE else NOT_ON_FIRE)
					} else {
						tag.putByte(TAG_STATUS, UNKNOWN)
					}
				}
			} else {
				tag.putByte(TAG_STATUS, UNKNOWN)
			}
		}
	}

	fun ItemStack.getBindingTag(): CompoundTag = getOrCreateTagElement(TAG_FIRE_STATION_POS)

	private val CompoundTag.dim get() = getString("dimension")

	fun bindTo(stack: ItemStack, level: Level, pos: BlockPos) {
		stack.getBindingTag().apply {
			putString("dimension", level.dimension().location().path)
			putInt("x", pos.x)
			putInt("y", pos.y)
			putInt("z", pos.z)
		}
	}
}