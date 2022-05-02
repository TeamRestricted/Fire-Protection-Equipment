package restricted.fpe.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("ConstantConditions")
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinFurnaceTileEntity {

	private static final Random RANDOM = new Random();

	@Shadow
	protected abstract boolean isLit();

	@Inject(method = "serverTick", at = @At("HEAD"))
	private static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, AbstractFurnaceBlockEntity pBlockEntity, CallbackInfo ci) {
		if(pBlockEntity instanceof FurnaceBlockEntity) {
			var isLit = ((MixinFurnaceTileEntity)(Object) pBlockEntity).isLit();
			if(isLit) {
				if(RANDOM.nextInt(100) > 98) {
					var blockPosToFire = getAnyBlockPosCanFire(pLevel, pPos);
					if(blockPosToFire != null) {
						pLevel.setBlock(blockPosToFire, Blocks.FIRE.defaultBlockState(), 3);
					}
				}
			}
		}
	}

	/**
	 * 在熔炉六面中随机选择一面为空气方块的位置并返回，如果没有则返回 {@code null}。
	 */
	private static BlockPos getAnyBlockPosCanFire(Level level, BlockPos pos) {
		var flammable = Stream.of(pos.above(), pos.below(), pos.east(), pos.west(), pos.north(), pos.south()).filter((bp) -> level.getBlockState(bp).is(Blocks.AIR)).collect(Collectors.toList());
		if(flammable.size() == 0) {
			return null;
		} else {
			return flammable.get(RANDOM.nextInt(flammable.size()));
		}
	}
}
