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

	item(FPE.Items.BrokenFireHydrant) {
		en("Broken Fire Hydrant")
		zh("损坏的消防栓")
		tw("損毀的消防栓")
	}

	enchant(FPE.Enchants.FireWalker) {
		en("Fire Walker")
		zh("十万火急")
		tw("十萬火急")
	}

	advancement("root") {
		title {
			en("Burst into Flame")
			zh("爆燃")
			tw("爆燃")
		}

		description {
			en("Don't play with fire!")
			zh("不要玩火！")
			tw("不要玩火！")
		}
	}

	advancement("volunteer_fire_fighter") {
		title {
			en("Volunteer Firefighter")
			zh("志愿消防员")
			tw("消防義工")
		}

		description {
			en("Extinguish a Fire")
			zh("使用灭火器或消防栓熄灭火源")
			tw("使用滅火器或消防栓熄滅火源")
		}
	}

	advancement("breaker") {
		title {
			en("Breaker")
			zh("断路器")
			tw("斷路器")
		}

		description {
			en("Extinguish a Campfire or a Soul Campfire")
			zh("使用灭火器或消防栓熄灭篝火或灵魂篝火")
			tw("使用滅火器或消防栓熄滅篝火或靈魂篝火")
		}
	}
}