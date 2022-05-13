@file:Suppress("DEPRECATION")

package restricted.fpe.advancements.critereon

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.level.block.Block

object CriteriaUtils {

	fun deserializeBlock(json: JsonObject): Block? {
		return if(json.has("block")) {
			val loc = ResourceLocation(GsonHelper.getAsString(json, "block"))
			Registry.BLOCK.getOptional(loc).orElseThrow {
				JsonSyntaxException("Unknown block type '$loc'")
			}
		} else {
			null
		}
	}

	fun registerCustomTriggers() {
		CriteriaTriggers.register(BlockExtinguishedTrigger)
	}

}