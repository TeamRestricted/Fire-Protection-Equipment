package restricted.fpe.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.FireworkStarItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import restricted.fpe.extinguish.ExtinguishType;

import java.util.List;

@Mixin(FireworkStarItem.class)
public class MixinFireworkStarItem {

	@Inject(method = "appendHoverText(Lnet/minecraft/nbt/CompoundTag;Ljava/util/List;)V", at = @At("TAIL"))
	private static void injectAppendHoverText(CompoundTag pCompound, List<Component> pTooltipComponents, CallbackInfo ci) {
		ExtinguishType type = ExtinguishType.fromString(pCompound.getString("Extinguish"));
		if(type != null) {
			pTooltipComponents.add(
					new TranslatableComponent("item.fire_protection_equipment.firework_star").append(" ")
							.append(new TranslatableComponent("fire_protection_equipment.extinguish."+type.getValue())).withStyle(ChatFormatting.GRAY));
		}
	}
}
