package restricted.fpe.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import restricted.fpe.FPE

private val prop = Item.Properties().rarity(Rarity.COMMON).tab(FPE.Tabs.Default)

object FireItem: Item(prop) {

	// prevent being fired
	override fun isFireResistant(): Boolean = true
}