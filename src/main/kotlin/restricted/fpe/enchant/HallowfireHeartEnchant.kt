package restricted.fpe.enchant

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.enchantment.*
import restricted.fpe.FPE

object HallowfireHeartEnchant : Enchantment(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, arrayOf(EquipmentSlot.CHEST)) {

	override fun getMinCost(pLevel: Int): Int {
		return pLevel * 25
	}

	override fun getMaxCost(pLevel: Int): Int {
		return getMinCost(pLevel) + 50
	}

	override fun isTreasureOnly(): Boolean {
		return true
	}

	override fun getMaxLevel(): Int {
		return 1
	}

	override fun checkCompatibility(pOther: Enchantment): Boolean {
		if(pOther == Enchantments.MENDING) return false
		if(pOther == Enchantments.UNBREAKING) return false
		if(pOther == Enchantments.FIRE_PROTECTION) return false
		return true
	}

	fun tryRepair(player: Player) {
		val stack = EnchantmentHelper.getRandomItemWith(FPE.Enchants.HallowfireHeart, player)?.value
		if(stack != null) {
			stack.damageValue = stack.damageValue - 3
		}
	}

}