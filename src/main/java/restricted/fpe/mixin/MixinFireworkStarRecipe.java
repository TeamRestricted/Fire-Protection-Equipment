package restricted.fpe.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import restricted.fpe.FPE;
import restricted.fpe.extinguish.ExtinguishType;

@SuppressWarnings("ALL") // Minecraft Development 闭嘴
@Mixin(FireworkStarRecipe.class)
public class MixinFireworkStarRecipe {

	private static final Ingredient WATER_INGREDIENT = Ingredient.of(Items.WATER_BUCKET);
	private static final Ingredient DRY_CHEMICAL_INGREDIENT = Ingredient.of(FPE.Items.INSTANCE.getDryChemicalPowder());

	@Inject(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
	protected void injectAssemble(CraftingContainer pInv, CallbackInfoReturnable<ItemStack> cir) {
		CompoundTag tag = cir.getReturnValue().getOrCreateTagElement("Explosion");
		for(int i = 0; i < pInv.getContainerSize(); ++i) {
			ItemStack stack = pInv.getItem(i);
			if(!stack.isEmpty()) {
				if(WATER_INGREDIENT.test(stack)) {
					tag.putString("Extinguish", ExtinguishType.WATER.getValue());
					return;
				}
				if(DRY_CHEMICAL_INGREDIENT.test(stack)) {
					tag.putString("Extinguish", ExtinguishType.DRY_CHEMICAL.getValue());
					return;
				}
			}
		}
	}

	// 别看这里是红的，但是确确实实可以跑的起来。
	@Redirect(
			method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z",
			at = @At(value = "CONSTANT", args = "classValue=net/minecraft/world/item/DyeItem")
	)
	protected boolean redirectInstanceOf(Object itemstack, Class<?> cls) {
		return true; // 先把原版的限制给去掉，但是会导致无论什么物品都可以加入合成
	}

	@Inject(
			method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z",
			at = @At(value = "CONSTANT", args = "classValue=net/minecraft/world/item/DyeItem", shift = At.Shift.AFTER),
			locals = LocalCapture.CAPTURE_FAILSOFT,
			cancellable = true)
	protected void injectInstanceOf(CraftingContainer pInv, Level pLevel, CallbackInfoReturnable<Boolean> cir, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4, int i, ItemStack itemstack) {
		if(!WATER_INGREDIENT.test(itemstack) && !DRY_CHEMICAL_INGREDIENT.test(itemstack) && !(itemstack.getItem() instanceof DyeItem)) {
			cir.setReturnValue(false); // 添加限制，只允许水桶和干粉以及颜料
		}
	}

}
