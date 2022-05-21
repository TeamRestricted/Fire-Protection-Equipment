package restricted.fpe.datagen

import net.minecraft.data.DataGenerator
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.LanguageProvider
import restricted.fpe.ModId

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
			provider.add(it.key, it.langEnglish ?: it.key)
		}
	}

	fun buildChinese(provider: FPELanguageProvider) {
		entries.forEach {
			provider.add(it.key, it.langChinese ?: it.key)
		}
	}

	fun buildTraditionalChinese(provider: FPELanguageProvider) {
		entries.forEach {
			provider.add(it.key, it.langChineseTradition ?: it.key)
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

	internal var langEnglish: String? = null
		private set
	internal var langChinese: String? = null
		private set
	internal var langChineseTradition: String? = null
		private set

	fun en(english: String) = apply { this.langEnglish = english }

	fun zh(chinese: String) = apply {
		langChinese = chinese
		if(langChineseTradition == null) langChineseTradition = chinese
	}

	fun tw(traditionalChinese: String) = apply { langChineseTradition = traditionalChinese }
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