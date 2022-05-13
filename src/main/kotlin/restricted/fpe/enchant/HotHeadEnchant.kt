package restricted.fpe.enchant

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.MobType
import net.minecraft.world.item.enchantment.*

object HotHeadEnchant : Enchantment(Rarity.COMMON, EnchantmentCategory.ARMOR_HEAD, arrayOf(EquipmentSlot.HEAD)) {

	override fun checkCompatibility(pOther: Enchantment): Boolean {
		if(pOther == Enchantments.RESPIRATION) return false
		if(pOther == Enchantments.AQUA_AFFINITY) return false
		return true
	}

	override fun getMaxLevel(): Int {
		return 3
	}

	override fun getDamageBonus(pLevel: Int, pType: MobType): Float {
		return 3.0F * pLevel + 1.0F
	}
}