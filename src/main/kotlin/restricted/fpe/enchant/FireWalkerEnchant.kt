package restricted.fpe.enchant

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.enchantment.*

object FireWalkerEnchant: Enchantment(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, arrayOf(EquipmentSlot.FEET)) {

	override fun getMaxLevel(): Int = 3
	override fun getMinCost(pLevel: Int): Int = pLevel * 10
	override fun getMaxCost(pLevel: Int): Int = getMinCost(pLevel) + 15

	override fun checkCompatibility(other: Enchantment): Boolean {
		if(other != Enchantments.FROST_WALKER) return false
		return true
	}
}