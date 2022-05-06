package restricted.fpe.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import restricted.fpe.FPE
import restricted.fpe.pos

private val prop = Item.Properties().rarity(Rarity.UNCOMMON).tab(FPE.Tabs.Default).stacksTo(1).durability(15000)

object FireExtinguisherItem: Item(prop) {

	private fun getExtinguisherLevel(world: Level, stack: ItemStack): Int = 3

	override fun getUseDuration(pStack: ItemStack): Int {
		return 10
	}

	override fun getUseAnimation(pStack: ItemStack): UseAnim = UseAnim.BLOCK

	override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if(hand == InteractionHand.MAIN_HAND) {
			if(stack.damageValue < stack.maxDamage) {
				if(getExtinguisherLevel(world, stack) > 0) {
					player.startUsingItem(hand)
					return InteractionResultHolder.consume(stack)
				}
			}
		}
		return InteractionResultHolder.fail(stack)
	}

	override fun releaseUsing(stack: ItemStack, world: Level, entity: LivingEntity, chargedTime: Int) {
		if(entity is Player) {
			val hit = entity.pick(10.0, 1.0F, false)
			val extinguishLoc = hit.location.pos
			val extinguishLevel = getExtinguisherLevel(world, stack)
			FPE.extinguishFire(world, extinguishLoc, extinguishLevel)
		}
		super.releaseUsing(stack, world, entity, chargedTime)
	}

}