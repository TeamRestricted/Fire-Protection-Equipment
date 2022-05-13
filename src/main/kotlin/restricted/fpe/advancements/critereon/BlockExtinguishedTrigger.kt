package restricted.fpe.advancements.critereon

import com.google.gson.JsonObject
import net.minecraft.advancements.critereon.*
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import restricted.fpe.FPEConst

object BlockExtinguishedTrigger : SimpleCriterionTrigger<BlockExtinguishedTrigger.TriggerInstance>() {

	override fun getId(): ResourceLocation {
		return FPEConst.CriteriaNames.BlockExtinguishedTriggerID
	}

	override fun createInstance(
		json: JsonObject,
		player: EntityPredicate.Composite,
		context: DeserializationContext
	): TriggerInstance {
		return TriggerInstance(player, ItemPredicate.fromJson(json.get("item")), BlockPredicate.fromJson(json.get("block")))
	}

	fun trigger(player: ServerPlayer, serverLevel: ServerLevel, blockPos: BlockPos, itemStack: ItemStack) {
		this.trigger(player) { it.matches(serverLevel, blockPos, itemStack) }
	}

	class TriggerInstance(
		player: EntityPredicate.Composite,
		val item: ItemPredicate,
		val block: BlockPredicate
	) : AbstractCriterionTriggerInstance(FPEConst.CriteriaNames.BlockExtinguishedTriggerID, player) {

		fun matches(serverLevel: ServerLevel, blockPos: BlockPos, itemStack: ItemStack): Boolean {
			return block.matches(serverLevel, blockPos) && item.matches(itemStack)
		}

		override fun serializeToJson(pConditions: SerializationContext): JsonObject {
			return super.serializeToJson(pConditions).apply {
				add("item", item.serializeToJson())
				add("block", block.serializeToJson())
			}
		}
	}

}