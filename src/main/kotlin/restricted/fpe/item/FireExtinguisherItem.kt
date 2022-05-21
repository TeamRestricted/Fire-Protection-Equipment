package restricted.fpe.item

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType
import restricted.fpe.*
import restricted.fpe.extinguish.*

object FireExtinguisherItem : Item(FPEConst.ItemConst.FireExtinguisherProp) {

	const val TAG_EXTINGUISHER = "$ModId:extinguisher"
	const val TAG_EXTINGUISHER_TYPE = "type"
	const val TAG_EXTINGUISHER_LEVEL = "level"

	val DRY_ICE_INEXTINGUISHABLE = arrayOf(
		MinecraftBlocks.BEDROCK,
		MinecraftBlocks.NETHERRACK
	)

	val ItemStack.extinguisherTag: CompoundTag get() = getOrCreateTagElement(TAG_EXTINGUISHER)

	var CompoundTag.extinguisherType: ExtinguishType
		get() = enumValueOrNull<ExtinguishType>(getString(TAG_EXTINGUISHER_TYPE)) ?: ExtinguishType.NONE
		set(value) = putString(TAG_EXTINGUISHER_TYPE, value.toString())

	var CompoundTag.extinguishingLevel: Int
		get() = getInt(TAG_EXTINGUISHER_LEVEL) ifZero 3
		set(value) = putInt(TAG_EXTINGUISHER_LEVEL, value)

	val DryChemicalTyped: ItemStack = defaultInstance.copy().apply {
		getOrCreateTagElement(TAG_EXTINGUISHER).also {
			it.extinguisherType = ExtinguishType.DRY_CHEMICAL
		}
	}

	val FoamsTyped: ItemStack = defaultInstance.copy().apply {
		getOrCreateTagElement(TAG_EXTINGUISHER).also {
			it.extinguisherType = ExtinguishType.FOAMS
		}
	}

	val WaterTyped: ItemStack = defaultInstance.copy().apply {
		getOrCreateTagElement(TAG_EXTINGUISHER).also {
			it.extinguisherType = ExtinguishType.WATER
		}
	}

	val DryIceTyped: ItemStack = defaultInstance.copy().apply {
		getOrCreateTagElement(TAG_EXTINGUISHER).also {
			it.extinguisherType = ExtinguishType.DRY_ICE
		}
	}

	override fun fillItemCategory(pCategory: CreativeModeTab, items: NonNullList<ItemStack>) {
		if(allowdedIn(pCategory)) {
			items += DryChemicalTyped.copy()
			items += FoamsTyped.copy()
			items += WaterTyped.copy()
			items += DryIceTyped.copy()
		}
	}

	private fun getExtinguisherLevel(world: Level, stack: ItemStack): Int {
		val type = getExtinguisherType(stack)
		if(type == ExtinguishType.FOAMS && world.dimensionType() == DimensionType.DEFAULT_NETHER) {
			return 0
		}
		return stack.extinguisherTag.extinguishingLevel
	}

	private fun getExtinguisherType(stack: ItemStack): ExtinguishType = stack.extinguisherTag.extinguisherType

	/**
	 * @see ExtinguishRecipe.DIRECTLY_EXTINGUISH
	 */
	fun canExtinguishFire(ctx: ExtinguishContext, pos: BlockPos): Boolean {
		if(ctx.itemstack?.extinguisherTag?.extinguisherType == ExtinguishType.DRY_ICE) {
			return ctx.level.getBlockState(pos.below()).block !in DRY_ICE_INEXTINGUISHABLE
		}
		return true
	}

	override fun getDescriptionId(itemstack: ItemStack): String {
		return "${descriptionId}.${itemstack.extinguisherTag.extinguisherType.value.lowercase()}"
	}

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
			val ctx =
				ExtinguishContext(world, extinguishLoc, extinguishLevel, getExtinguisherType(stack), player = entity, itemstack = stack)
			FPE.extinguishFire(ctx)
		}
		super.releaseUsing(stack, world, entity, chargedTime)
	}

}