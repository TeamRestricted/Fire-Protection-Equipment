package restricted.fpe

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.*
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.Vec3
import net.minecraftforge.registries.ForgeRegistryEntry
import net.minecraftforge.registries.IForgeRegistryEntry
import restricted.fpe.FPE.apiImpl

internal typealias MinecraftItems = net.minecraft.world.item.Items
internal typealias MinecraftBlocks = net.minecraft.world.level.block.Blocks

internal val defaultItemProp = Item.Properties().tab(FPE.Tabs.Default)
internal val defaultSingleItemProp = Item.Properties().stacksTo(1).tab(FPE.Tabs.Default)

internal fun Block.generateBlockItem(properties: Item.Properties = defaultItemProp): BlockItem =
	BlockItem(this, properties)

internal fun buildItem(itemProp: Item.Properties = defaultItemProp): Item = Item(itemProp)
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

internal val Block.fireType get() = apiImpl.getFireType(this)

internal fun boundingBoxOfCenter(center: BlockPos, xOff: Int, yOff: Int = xOff, zOff: Int = xOff) =
	BoundingBox(center.x - xOff, center.y - yOff, center.z - zOff, center.x + xOff, center.y + yOff, center.z + zOff)

internal fun BoundingBox.forEach(block: (BlockPos) -> Unit) = BlockPos.betweenClosedStream(this).forEach(block)