package restricted.fpe.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import restricted.fpe.MinecraftItems

object FireworkHelper {

	fun hasExtinguishingStar(stack: ItemStack): Boolean {
		return stack.item == MinecraftItems.FIREWORK_ROCKET &&
				stack.getOrCreateTagElement("Fireworks").getList("Explosions", 10)
					.any { (it as CompoundTag).getString("Extinguish").isNotEmpty() }
	}

}