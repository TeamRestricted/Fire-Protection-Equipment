package restricted.fpe.advancements.critereon

import com.google.gson.JsonObject
import net.minecraft.advancements.critereon.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import restricted.fpe.FPEConst

/**
 * 玩家燃烧时点燃周围可燃物时触发
 */
object PlayerIgnitedTrigger : SimpleCriterionTrigger<PlayerIgnitedTrigger.TriggerInstance>() {

	override fun getId(): ResourceLocation {
		return FPEConst.CriteriaNames.PlayerIgnitedTriggerID
	}

	override fun createInstance(
		json: JsonObject,
		player: EntityPredicate.Composite,
		context: DeserializationContext
	): TriggerInstance {
		return TriggerInstance(player)
	}

	fun trigger(player: ServerPlayer) {
		this.trigger(player) { true }
	}

	class TriggerInstance(
		player: EntityPredicate.Composite
	) : AbstractCriterionTriggerInstance(FPEConst.CriteriaNames.PlayerIgnitedTriggerID, player) {

		override fun serializeToJson(pConditions: SerializationContext): JsonObject {
			return super.serializeToJson(pConditions)
		}
	}

}