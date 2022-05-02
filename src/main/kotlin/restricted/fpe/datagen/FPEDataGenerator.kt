@file:Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

package restricted.fpe.datagen

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent
import restricted.fpe.FPE
import restricted.fpe.ModId

@SubscribeEvent
fun gatherData(e: GatherDataEvent) {
	if(e.includeClient()) {
		val generator = e.generator

		registerLanguages()

		generator.addProvider(FPELanguageProvider(generator, "en_us", FPELanguage::buildEnglish))
		generator.addProvider(FPELanguageProvider(generator, "zh_cn", FPELanguage::buildChinese))
		generator.addProvider(FPELanguageProvider(generator, "zh_tw", FPELanguage::buildTraditionalChinese))
	}
}

private fun registerLanguages() = FPELanguage.apply {

	add("itemGroup.$ModId") {
		en("Fire Protection Equipment")
		zh("想不出来")
		tw("想不出來")
		// TODO: 把[想不出来]给换了
	}

	block(FPE.Blocks.FireHydrant) {
		en("Fire Hydrant")
		zh("消防栓")
		tw("消防栓")
	}

	item(FPE.Items.Fire) {
		en("FPE")
		zh("消防设备")
		tw("消防設施")
	}
}