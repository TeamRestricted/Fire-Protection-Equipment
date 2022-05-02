package restricted.fpe

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.registries.ForgeRegistryEntry
import net.minecraftforge.registries.IForgeRegistryEntry

internal val defaultItemProp = Item.Properties().tab(FPE.Tabs.Default)
internal val defaultSingleItemProp = Item.Properties().stacksTo(1).tab(FPE.Tabs.Default)

internal fun Block.generateBlockItem(properties: Item.Properties = defaultItemProp): BlockItem = BlockItem(this, properties)

internal fun buildItem(itemProp: Item.Properties = defaultItemProp): Item = Item(itemProp)
internal fun buildItem(block: Item.Properties.() -> Unit): Item = buildItem(Item.Properties().apply(block))

internal val <V: IForgeRegistryEntry<V>> ForgeRegistryEntry<V>.registryPath get() = registryName?.path ?: error("")

internal fun defaultBlockProperties(material: Material) = BlockBehaviour.Properties.of(material)

internal fun buildBlock(prop: BlockBehaviour.Properties): Block = Block(prop)
internal fun buildBlock(material: Material): Block = Block(defaultBlockProperties(material))