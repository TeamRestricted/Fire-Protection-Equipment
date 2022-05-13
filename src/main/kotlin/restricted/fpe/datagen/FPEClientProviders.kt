package restricted.fpe.datagen

import net.minecraft.data.DataGenerator
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.LanguageProvider
import restricted.fpe.ModId

/*
import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper
import restricted.fpe.ModId

internal class SFBlockStateProvider(val g: DataGenerator, private val h: ExistingFileHelper) : BlockStateProvider(g, ModId, h) {
	override fun registerStatesAndModels() {
	}
}

internal class SFItemModelProvider(g: DataGenerator, h: ExistingFileHelper) : ItemModelProvider(g, ModId, h) {
	override fun registerModels() {
	}
}
*/

internal class FPELanguageProvider(dataGen: DataGenerator, language: String, val block: (FPELanguageProvider) -> Unit) :
	LanguageProvider(dataGen, ModId, language) {
	override fun addTranslations() {
		block(this)
	}
}

internal object FPELanguage {

	private val entries = mutableListOf<LanguageEntry>()

	fun buildEnglish(provider: FPELanguageProvider) {
		entries.forEach {
			provider.add(it.key, it._en ?: it.key)
		}
	}

	fun buildChinese(provider: FPELanguageProvider) {
		entries.forEach {
			provider.add(it.key, it._zh ?: it.key)
		}
	}

	fun buildTraditionalChinese(provider: FPELanguageProvider) {
		entries.forEach {
			provider.add(it.key, it._zhTw ?: it.key)
		}
	}

	fun add(key: String, func: LanguageEntry.() -> Unit) {
		entries += LanguageEntry(key).apply(func)
	}

	fun item(item: Item, func: LanguageEntry.() -> Unit) {
		entries += LanguageEntry(item.descriptionId).apply(func)
	}

	fun block(block: Block, func: LanguageEntry.() -> Unit) {
		entries += LanguageEntry(block.descriptionId).apply(func)
	}

	fun enchant(enchant: Enchantment, func: LanguageEntry.() -> Unit) {
		entries += LanguageEntry(enchant.descriptionId).apply(func)
	}

	fun advancement(advancementId: String, func: AdvancementEntry.() -> Unit) {
		entries += AdvancementEntry("advancements.$ModId.$advancementId").apply(func).langEntries
	}

}

internal class LanguageEntry(val key: String) {

	internal var _en: String? = null
		private set
	internal var _zh: String? = null
		private set
	internal var _zhTw: String? = null
		private set

	fun en(english: String) = apply { _en = english }

	fun zh(chinese: String) = apply {
		_zh = chinese
		if(_zhTw == null) _zhTw = chinese
	}

	fun tw(traditionalChinese: String) = apply { _zhTw = traditionalChinese }
}

internal class AdvancementEntry(advancementId: String) {

	private val title = LanguageEntry("$advancementId.title")
	private val description = LanguageEntry("$advancementId.description")

	fun title(func: LanguageEntry.() -> Unit) {
		title.apply(func)
	}

	fun description(func: LanguageEntry.() -> Unit) {
		description.apply(func)
	}

	val langEntries: Iterable<LanguageEntry> get() = listOf(title, description)

}