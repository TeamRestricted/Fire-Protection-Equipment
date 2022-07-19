package restricted.fpe.util

import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import restricted.fpe.FPE

object ArmorHelper {

	@JvmStatic
	val FirefightersSuits by lazy {
		listOf(
			FPE.Items.FirefightersHelmet,
			FPE.Items.FirefightersChestplate,
			FPE.Items.FirefightersLeggings,
			FPE.Items.FirefightersBoots
		)
	}

	@JvmStatic
	fun getEntityArmorCount(entity: Entity, validArmors: List<Item>): Int {
		return entity.armorSlots.count { it.item in validArmors }
	}

}