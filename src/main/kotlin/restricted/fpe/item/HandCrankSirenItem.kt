package restricted.fpe.item

import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import restricted.fpe.FPEConst
import restricted.fpe.runOnRemote

object HandCrankSirenItem : Item(FPEConst.ItemConst.DefaultNonStackableItemProp) {

	override fun use(pLevel: Level, player: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		// TODO: 音效
		player.level.runOnRemote {
			this.playSound(
				null,
				player.blockPosition(),
				SoundEvents.NOTE_BLOCK_COW_BELL,
				SoundSource.PLAYERS,
				1.0F,
				1.0F
			)
		}
		return InteractionResultHolder.success(player.getItemInHand(pUsedHand))
	}

}