package restricted.fpe.item

import net.minecraft.world.item.Item
import restricted.fpe.FPEConst

object FireItem: Item(FPEConst.ItemConst.DefaultItemProp) {

	// prevent being fired
	override fun isFireResistant(): Boolean = true
}