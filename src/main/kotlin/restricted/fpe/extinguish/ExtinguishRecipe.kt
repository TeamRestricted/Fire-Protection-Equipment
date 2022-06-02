@file:Suppress("unused")

package restricted.fpe.extinguish

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import restricted.fpe.*
import restricted.fpe.extinguish.ExtinguishRecipe.MiniBlockState.Builder.Companion.buildMiniState
import restricted.fpe.item.FireExtinguisherItem
import restricted.fpe.util.FireworkHelper
import thedarkcolour.kotlinforforge.kotlin.enumMapOf
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * 将灭火视为合成，方块类型和灭火类型得到灭火逻辑。
 */
object ExtinguishRecipe {

	data class MiniBlockState(val block: Block, val states: Map<Property<*>, *>) {

		fun apply(blockState: BlockState): Boolean {
			if(block != blockState.block) {
				return false
			}
			if(states.any { (property, value) -> (blockState.getValue(property) != value) }) return false
			return true
		}

		class Builder(val block: Block) {
			private val states = mutableMapOf<Property<*>, Any?>()
			fun <T : Comparable<T>> with(property: Property<T>, value: T) = apply { states[property] = value }
			fun build(): MiniBlockState = MiniBlockState(block, states)

			companion object {
				fun Block.buildMiniState(block: Builder.() -> Unit = {}): MiniBlockState =
					Builder(this).apply(block).build()
			}
		}
	}

	internal val recipes: Table<MiniBlockState, ExtinguishType, ExtinguishBlockFunction> = HashBasedTable.create()
	internal val recipesEntity: Table<EntityType<*>, ExtinguishType, ExtinguishEntityFunction> = HashBasedTable.create()

	internal val invalidBlocks = mutableListOf<Block>(
		MinecraftBlocks.AIR
	)

	//////// REGISTER METHODS

	/**
	 * 注册方块浇灭合成
	 *
	 * @param blockState 被浇灭的简单方块状态
	 * @param extinguishTypes 浇灭类型
	 * @param func 浇灭执行的方法体
	 */
	fun registerBlock(
		blockState: MiniBlockState,
		extinguishTypes: Array<ExtinguishType> = ExtinguishType.ALL,
		func: ExtinguishBlockFunction
	) = extinguishTypes.forEach { registerBlock(blockState, it, func) }

	/**
	 * 注册方块浇灭合成
	 *
	 * @param blockState 被浇灭的简单方块状态
	 * @param extinguishType 浇灭类型
	 * @param func 浇灭执行的方法体
	 */
	fun registerBlock(blockState: MiniBlockState, extinguishType: ExtinguishType, func: ExtinguishBlockFunction) =
		recipes.put(blockState, extinguishType, func)

	/**
	 * 注册实体浇灭合成
	 *
	 * @param entityType 实体类型
	 * @param extinguishTypes 浇灭类型
	 * @param func 浇灭方法体
	 */
	fun registerEntity(
		entityType: EntityType<*>,
		extinguishTypes: Array<ExtinguishType> = ExtinguishType.ALL,
		func: ExtinguishEntityFunction
	) = extinguishTypes.forEach { registerEntity(entityType, it, func) }

	/**
	 * 注册实体浇灭合成
	 *
	 * @param entityType 实体类型
	 * @param extinguishType 浇灭类型
	 * @param func 浇灭方法体
	 */
	fun registerEntity(
		entityType: EntityType<*>, extinguishType: ExtinguishType, func: ExtinguishEntityFunction
	) = recipesEntity.put(entityType, extinguishType, func)

	operator fun get(blockState: BlockState, extinguishType: ExtinguishType): ExtinguishBlockFunction? {
		if(blockState.block in invalidBlocks) return null

		return recipes.column(extinguishType).firstNotNullOfOrNull { (miniState, value) ->
			if(miniState.apply(blockState)) value else null
		}
	}

	fun <T : Entity> getForEntity(
		entityType: EntityType<T>, extinguishType: ExtinguishType
	): ExtinguishEntityFunction? {
		return recipesEntity[entityType, extinguishType]
	}

	//////// FAST FUNCTIONS

	private fun canDefaultExtinguish(ctx: ExtinguishContext): Boolean =
		(ctx.itemstack == null) || (ctx.itemstack.item == FPE.Items.FireExtinguisher && FireExtinguisherItem.canExtinguishFire(
			ctx, ctx.centerPos
		)) || (ctx.itemstack.item == MinecraftItems.FIREWORK_ROCKET && FireworkHelper.hasExtinguishingStar(ctx.itemstack))

	private val DIRECTLY_EXTINGUISH: ExtinguishBlockFunction = { ctx, _, pos ->
		if(canDefaultExtinguish(ctx)) {
			ctx.level.removeBlock(pos, false)
		}
		ctx.level.runOnRemote {
			sendParticles(ParticleTypes.CLOUD, pos.vec3, (1..20).random(), 0.2)
		}
	}

	private fun directly(): ExtinguishBlockFunction = DIRECTLY_EXTINGUISH

	private fun replaceWithBlock(newState: BlockState): ExtinguishBlockFunction = { ctx, _, pos ->
		if(canDefaultExtinguish(ctx)) {
			ctx.level.setBlockAndUpdate(pos, newState)
		}
		ctx.level.runOnRemote {
			sendParticles(ParticleTypes.CLOUD, pos.vec3, (1..20).random(), 0.2)
		}
	}

	fun registerReplaceBlock(
		oldState: MiniBlockState, newState: BlockState, extinguishTypes: Array<ExtinguishType> = ExtinguishType.ALL
	) = registerBlock(oldState, extinguishTypes, replaceWithBlock(newState))

	fun registerDirectlyExtinguish(
		fireState: MiniBlockState, extinguishTypes: Array<ExtinguishType> = ExtinguishType.ALL
	) = registerBlock(fireState, extinguishTypes, directly())

	//////// BUILDER

	internal fun builder(block: Block, func: BuilderForBlock.() -> Unit) = BuilderForBlock(block).apply(func)
	internal fun <T : Entity> builder(entityType: EntityType<T>, func: BuilderForEntity<T>.() -> Unit) =
		BuilderForEntity(entityType).apply(func).build()

	internal class BuilderForBlock(val block: Block) {

		fun withState(func: BuilderForMiniBlockState.() -> Unit) {
			BuilderForMiniBlockState(block.buildMiniState()).apply(func).build()
		}

		fun <T : Comparable<T>> withState(
			vararg pairs: Pair<Property<T>, T>, func: BuilderForMiniBlockState.() -> Unit
		) {
			val miniState = block.buildMiniState {
				pairs.forEach {
					with(it.first, it.second)
				}
			}
			BuilderForMiniBlockState(miniState).apply(func).build()
		}

	}

	internal class BuilderForMiniBlockState(val miniBlockState: MiniBlockState) {

		private val conditions = enumMapOf<ExtinguishType, ExtinguishBlockFunction>()

		fun typed(type: ExtinguishType, func: ExtinguishBlockFunction) {
			conditions[type] = func
		}

		fun dryChemical(func: ExtinguishBlockFunction) = typed(ExtinguishType.DRY_CHEMICAL, func)
		fun foams(func: ExtinguishBlockFunction) = typed(ExtinguishType.FOAMS, func)
		fun water(func: ExtinguishBlockFunction) = typed(ExtinguishType.WATER, func)
		fun dryIce(func: ExtinguishBlockFunction) = typed(ExtinguishType.DRY_ICE, func)

		fun otherwise(func: ExtinguishBlockFunction) {
			ExtinguishType.ALL.forEach {
				conditions.putIfAbsent(it, func)
			}
		}

		fun build() {
			conditions.forEach { (type, func) ->
				registerBlock(miniBlockState, type, func)
			}
		}
	}

	internal class BuilderForEntity<T : Entity>(val entityType: EntityType<T>) {

		private val conditions = enumMapOf<ExtinguishType, ExtinguishEntityFunction>()

		fun typed(type: ExtinguishType, func: ExtinguishEntityFunction) {
			conditions[type] = func
		}

		fun dryChemical(func: ExtinguishEntityFunction) = typed(ExtinguishType.DRY_CHEMICAL, func)
		fun foams(func: ExtinguishEntityFunction) = typed(ExtinguishType.FOAMS, func)
		fun water(func: ExtinguishEntityFunction) = typed(ExtinguishType.WATER, func)
		fun dryIce(func: ExtinguishEntityFunction) = typed(ExtinguishType.DRY_ICE, func)

		fun otherwise(func: ExtinguishEntityFunction) {
			ExtinguishType.ALL.forEach {
				conditions.putIfAbsent(it, func)
			}
		}

		fun build() {
			conditions.forEach { (type, func) ->
				registerEntity(entityType, type, func)
			}
		}
	}

}

typealias ExtinguishBlockFunction = (ExtinguishContext, BlockState, BlockPos) -> Unit
typealias ExtinguishEntityFunction = (ExtinguishContext, Entity) -> Unit