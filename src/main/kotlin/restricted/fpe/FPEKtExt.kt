package restricted.fpe

import com.google.common.collect.Table
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.Vec3
import net.minecraftforge.registries.ForgeRegistryEntry
import net.minecraftforge.registries.IForgeRegistryEntry
import restricted.fpe.FPEConst.ItemConst.DefaultItemProp

internal typealias MinecraftItems = Items
internal typealias MinecraftBlocks = net.minecraft.world.level.block.Blocks

internal fun Block.generateBlockItem(properties: Item.Properties = DefaultItemProp): BlockItem =
	BlockItem(this, properties)

internal fun buildItem(itemProp: Item.Properties = DefaultItemProp): Item = Item(itemProp)
internal fun buildItem(block: Item.Properties.() -> Unit): Item = buildItem(Item.Properties().apply(block))

internal val <V : IForgeRegistryEntry<V>> ForgeRegistryEntry<V>.registryPath get() = registryName?.path ?: error("")

internal fun defaultBlockProperties(material: Material) = BlockBehaviour.Properties.of(material)

internal fun buildBlock(prop: BlockBehaviour.Properties): Block = Block(prop)
internal fun buildBlock(material: Material): Block = Block(defaultBlockProperties(material))

internal fun BlockState.toBlockStateTag() = CompoundTag().also {
	this.values.forEach { (key, value) -> it.putString(key.name, value.toString()) }
}

internal fun createItemStackForBlockState(state: BlockState): ItemStack =
	createItemStackForBlockStateTag(state.block, state.toBlockStateTag())

internal fun createItemStackForBlockStateTag(block: Block, blockState: CompoundTag): ItemStack =
	ItemStack(block.asItem()).apply {
		addTagElement("BlockStateTag", blockState)
	}

internal val Vec3.pos get() = BlockPos(x, y, z)
internal val BlockPos.vec3 get() = Vec3(x + 0.5, y + 0.5, z + 0.5)

internal fun boundingBoxOfCenter(center: BlockPos, xOff: Int, yOff: Int = xOff, zOff: Int = xOff) =
	BoundingBox(center.x - xOff, center.y - yOff, center.z - zOff, center.x + xOff, center.y + yOff, center.z + zOff)

internal fun BoundingBox.forEach(block: (BlockPos) -> Unit) = BlockPos.betweenClosedStream(this).forEach(block)

internal val BoundingBox.AABB get() = net.minecraft.world.phys.AABB.of(this)

internal fun buildSetBlockFlag(
	updateBlock: Boolean = false,
	sendToClient: Boolean = false,
	preventReRender: Boolean = false,
	reRenderOnMainThread: Boolean = false,
	preventObserversSeeing: Boolean = false
): Int {
	var i = 0
	if(updateBlock) {
		i = i or 1
	}
	if(sendToClient) {
		i = i or 2
	}
	if(preventReRender) {
		i = i or 4
	}
	if(reRenderOnMainThread) {
		i = i or 8
	}
	if(preventObserversSeeing) {
		i = i or 16
	}
	return i
}

internal operator fun <R, C, V> Table.Cell<R, C, V>.component1() = this.rowKey
internal operator fun <R, C, V> Table.Cell<R, C, V>.component2() = this.columnKey
internal operator fun <R, C, V> Table.Cell<R, C, V>.component3() = this.value

internal fun <R> Level.runOnRemote(block: ServerLevel.() -> R): R? {
	return if(this is ServerLevel) {
		block(this)
	} else {
		null
	}
}

internal fun <R> Level.letOnRemote(block: (ServerLevel) -> R): R? {
	return if(this is ServerLevel) {
		block(this)
	} else {
		null
	}
}

internal fun ServerLevel.sendParticles(particleOptions: ParticleOptions, pos: Vec3, count: Int, speed: Double, offset: Vec3 = Vec3.ZERO) =
	sendParticles(particleOptions, pos.x, pos.y, pos.z, count, offset.x, offset.y, offset.z, speed)

internal fun buildCompoundTag(block: CompoundTag.() -> Unit) = CompoundTag().apply(block)

internal inline fun <reified T: Enum<T>> enumValueOrNull(name: String) = enumValues<T>().find { it.name == name }

infix fun Int.ifZero(nonZeroValue: Int): Int = if(this == 0) { nonZeroValue } else { this }