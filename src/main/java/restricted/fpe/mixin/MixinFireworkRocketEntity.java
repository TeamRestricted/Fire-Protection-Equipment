package restricted.fpe.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import restricted.fpe.FPE;
import restricted.fpe.extinguish.ExtinguishContext;
import restricted.fpe.extinguish.ExtinguishType;

@Mixin(FireworkRocketEntity.class)
public abstract class MixinFireworkRocketEntity {

	@Final
	@Shadow
	private static EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM;

	@Inject(method = "explode", at = @At("HEAD"))
	protected void injectExplode(CallbackInfo ci) {
		dealExtinguish();
	}

	protected void dealExtinguish() {
		FireworkRocketEntity this$fw = ((FireworkRocketEntity) (Object) this);
		ItemStack stack = this$fw.getEntityData().get(DATA_ID_FIREWORKS_ITEM);
		CompoundTag tag = stack.isEmpty() ? null : stack.getOrCreateTagElement("Fireworks");
		if(tag != null) {
			ListTag explosionsTag = tag.getList("Explosions", 10);
			explosionsTag.forEach(explosionTag -> {
				ExtinguishType type = ExtinguishType.fromString(((CompoundTag) explosionTag).getString("Extinguish"));
				if(type != null) {
					Entity owner = this$fw.getOwner();
					ExtinguishContext ctx = new ExtinguishContext(this$fw.level, this$fw.blockPosition(),
							Mth.ceil(5.0F + explosionsTag.size() * 0.85F), type,
							owner instanceof Player ? (Player) owner : null, stack);
					FPE.extinguishFire(ctx);
				}
			});
		}
	}

}
