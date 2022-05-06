package restricted.fpe.extinguish

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import restricted.fpe.component1
import restricted.fpe.component2
import restricted.fpe.component3

/**
 * 将灭火视为合成，方块类型和灭火类型得到灭火逻辑。
 */
object ExtinguishRecipe {

	data class MiniBlockState(val block: Block, val states: Map<Property<*>, *>) {

		fun apply(blockState: BlockState): Boolean {
			if(block != blockState.block) return false
			if(states.any { (property, value) -> blockState.getValue(property) != value }) return false
			return true
		}

		class Builder(val block: Block) {
			private val states = mutableMapOf<Property<*>, Any?>()
			fun <T: Comparable<T>> with(property: Property<T>, value: T) = apply { states[property] = value }
			fun build(): MiniBlockState = MiniBlockState(block, states)
			companion object {
				fun Block.buildMiniState(block: Builder.() -> Unit = {}): MiniBlockState =
					Builder(this).apply(block).build()
			}
		}
	}

	internal val recipes: Table<MiniBlockState, ExtinguishType, ExtinguishingFunction> = HashBasedTable.create()

	fun register(
		blockState: MiniBlockState,
		func: ExtinguishingFunction
	) = register(blockState, enumValues(), func)

	fun register(blockState: MiniBlockState, types: Array<ExtinguishType>, func: ExtinguishingFunction) =
		types.forEach { register(blockState, it, func) }

	fun register(
		blockState: MiniBlockState,
		extinguishType: ExtinguishType,
		block: ExtinguishingFunction
	) {
		recipes.put(blockState, extinguishType, block)
	}

	operator fun get(blockState: BlockState, extinguishType: ExtinguishType): ExtinguishingFunction? {
		return recipes.cellSet().firstOrNull { (miniState, type, _) -> miniState.apply(blockState) && type == extinguishType }?.value
	}

}

typealias ExtinguishingFunction = (ExtinguishContext, BlockState, BlockPos) -> Unit