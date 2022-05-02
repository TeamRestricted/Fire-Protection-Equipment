package restricted.fpe.datagen

import net.minecraft.data.DataGenerator
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.LanguageProvider
import restricted.fpe.ModId
import java.util.function.Supplier

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
			provider.add(it.key, it.en ?: it.key)
		}
	}

	fun buildChinese(provider: FPELanguageProvider) {
		entries.forEach {
			provider.add(it.key, it.zh ?: it.key)
		}
	}

	fun buildTraditionalChinese(provider: FPELanguageProvider) {
		entries.forEach {
			provider.add(it.key, it.zhTw ?: it.key)
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

}

internal class LanguageEntry(val key: String) {

	internal var en: String? = null
		private set
	internal var zh: String? = null
		private set
	internal var zhTw: String? = null
		private set

	fun en(english: String) = apply { en = english }

	fun zh(chinese: String) = apply {
		zh = chinese
		if(zhTw == null) zhTw = chinese
	}

	fun tw(traditionalChinese: String) = apply { zhTw = traditionalChinese }
}