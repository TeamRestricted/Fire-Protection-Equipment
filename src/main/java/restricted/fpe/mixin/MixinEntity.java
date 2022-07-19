package restricted.fpe.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import restricted.fpe.util.ArmorHelper;

@Mixin(Entity.class)
public class MixinEntity {

	@Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
	void fireImmune(CallbackInfoReturnable<Boolean> cir) {
		Entity $this = ((Entity)(Object) this);
		if(ArmorHelper.getEntityArmorCount($this, ArmorHelper.getFirefightersSuits()) >= 4) {
			cir.setReturnValue(true);
		}
	}

}
