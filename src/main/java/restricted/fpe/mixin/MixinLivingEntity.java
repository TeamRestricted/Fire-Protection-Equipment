package restricted.fpe.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import restricted.fpe.FPE;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	// 检查玩家脚底是不是火
	private boolean inFireBlock() {
		return this.level.getBlockState(new BlockPos(this.position().x, this.getBoundingBox().minY + 0.5000001D, this.position().z)).is(Blocks.FIRE);
	}

	// 如果是火就修改数值；speed = 1 + level * factor(default=0.2) // TODO: 添加速度因数配置
	@Inject(method = "getBlockSpeedFactor()F", at = @At("RETURN"), cancellable = true)
	private void getBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
		var enchantLevel = EnchantmentHelper.getEnchantmentLevel(FPE.Enchants.INSTANCE.getFireHaste(), (LivingEntity) (Object) this);
		if(inFireBlock() && enchantLevel > 0) {
			cir.setReturnValue(1.0F + enchantLevel * 0.2F);
		}
	}
}
