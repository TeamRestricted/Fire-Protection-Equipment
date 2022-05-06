package restricted.fpe.api;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import restricted.fpe.FPE;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FireProtectionApi {

	static FireProtectionApi getInstance() {
		return FPE.INSTANCE.getApiImpl();
	}

	/**
	 * 用 {@code FireType} 分类的火焰方块
	 */
	Map<FireType, Set<Block>> getFireBlocks();

	/**
	 * 将方块视为火焰
	 *
	 * @param block 火焰方块
	 */
	default void registerFireBlock(Block block) {
		registerFireBlock(block, FireType.NORMAL_FIRE);
	}

	/**
	 * 将方块视为火焰
	 *
	 * @param block 火焰方块
	 * @param type  火焰类型
	 */
	void registerFireBlock(Block block, FireType type);

	/**
	 * 返回方块的火焰类型
	 *
	 * @param block 查询的方块
	 * @return 火焰类型，若不可燃则为空
	 */
	@Nullable
	FireType getFireType(Block block);

}
