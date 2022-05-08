package restricted.fpe.enchant

import net.minecraft.world.entity.*
import net.minecraft.world.item.BowItem
import net.minecraft.world.item.CrossbowItem
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentCategory
import restricted.fpe.FPEConst.EnchantCategory.BowAndCrossbowCategory
import restricted.fpe.potion.SpreadingFireEffect

object SpreadingFireEnchant: Enchantment(Rarity.VERY_RARE, BowAndCrossbowCategory, arrayOf(EquipmentSlot.MAINHAND)) {

	override fun getMaxLevel(): Int = 5

	override fun getMinCost(pLevel: Int): Int = 5 + (pLevel - 1) * 5

	override fun getMaxCost(pLevel: Int): Int = getMinCost(pLevel) + 5

	override fun doPostAttack(pAttacker: LivingEntity, pTarget: Entity, pLevel: Int) {
		if(pTarget is LivingEntity) {
			pTarget.addEffect(SpreadingFireEffect.instance(10 + pLevel * 5, pLevel))
		}
	}
}