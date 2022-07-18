@file:Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

package restricted.fpe.datagen

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent
import restricted.fpe.FPE
import restricted.fpe.ModId
import restricted.fpe.item.LinkingDeviceItem

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
		zh("消防与灭火")
		tw("消防設施")
	}

	block(FPE.Blocks.FireHydrant) {
		en("Fire Hydrant")
		zh("消防栓")
		tw("消防栓")
	}

	block(FPE.Blocks.FireExtinguishingBomb) {
		en("Fire Extinguishing Bomb")
		zh("抑燃炸弹")
		tw("抑燃炸彈")
	}

	block(FPE.Blocks.FireDetector) {
		en("Fire Detector")
		zh("火灾探测器")
		tw("火災探測器")
	}

	block(FPE.Blocks.FireSprinkler) {
		en("Fire Sprinkler")
		zh("消防喷淋")
		tw("消防噴淋")
	}

	block(FPE.Blocks.FireAlarmControlUnit) {
		en("Fire Alarm Control Unit")
		zh("火警控制单元")
		tw("消防控制單元")
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

	item(FPE.Items.FireExtinguisher) {
		en("Fire Extinguisher")
		zh("灭火器")
		tw("滅火器")
	}

	// extinguisher variants
	add("item.fire_protection_equipment.fire_extinguisher.dry_chemical") {
		en("Dry Chemical Extinguisher")
		zh("干粉灭火器")
		tw("乾粉滅火器")
	}

	add("item.fire_protection_equipment.fire_extinguisher.water") {
		en("Water Extinguisher")
		zh("水灭火器")
		tw("水滅火器")
	}

	add("item.fire_protection_equipment.fire_extinguisher.dry_ice") {
		en("Dry Ice Extinguisher")
		zh("干冰灭火器")
		tw("二氧化碳滅火器")
	}

	add("item.fire_protection_equipment.fire_extinguisher.foams") {
		en("Foams Extinguisher")
		zh("泡沫灭火器")
		tw("泡沫滅火器")
	}

	add("item.fire_protection_equipment.fire_extinguisher.none") {
		en("Empty Extinguisher")
		zh("空灭火器")
		tw("空滅火器")
	}
	// extinguisher variants end

	item(FPE.Items.HoseNozzle) {
		en("Hose Nozzle")
		zh("消防软管喷头")
		tw("消防軟管噴頭")
	}

	item(FPE.Items.FireAlarmControlTerminal) {
		en("Fire Terminal")
		zh("消防终端")
		tw("消防終端")
	}

	item(FPE.Items.FurnaceFireProtectionDevice) {
		en("Furnace Fire Protection Device")
		zh("熔炉防火设备")
		tw("熔爐防火設備")
	}

	item(FPE.Items.DryChemicalPowder) {
		en("Monoammonium Phosphate Powder")
		zh("磷酸二氢铵粉末")
		tw("磷酸二氫銨粉末")
	}

	// firefighter suits
	item(FPE.Items.FirefightersHelmet) {
		en("Firefighter's Helmet")
		zh("消防服头盔")
		tw("消防服頭盔")
	}

	item(FPE.Items.FirefightersChestplate) {
		en("Firefighter's Chestplate")
		zh("消防服胸甲")
		tw("消防服胸甲")
	}

	item(FPE.Items.FirefightersLeggings) {
		en("Firefighter's Leggings")
		zh("消防服护腿")
		tw("消防服護腿")
	}

	item(FPE.Items.FirefightersBoots) {
		en("Firefighter's Boots")
		zh("消防靴")
		tw("消防靴")
	}

	item(FPE.Items.LinkingDevice) {
		en("Linking Device")
		zh("连接设备")
		tw("連接設備")
	}

	enchant(FPE.Enchants.FireHaste) {
		en("Fire Walker")
		zh("十万火急")
		tw("十萬火急")
	}

	enchant(FPE.Enchants.Hothead) {
		en("Hothead")
		zh("热血上头")
		tw("魯莽分子")
	}

	enchant(FPE.Enchants.Blaze) {
		en("Blaze")
		zh("爆燃")
		tw("爆燃")
	}

	enchant(FPE.Enchants.HallowfireHeart) {
		en("Hallowfire Heart")
		zh("空洞火焰之心")
		tw("空洞火焰之心")
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

	advancement("campsite_safety_regulations") {
		title {
			en("Campsite Safety Regulations")
			zh("露营地安全守则")
			tw("露營地安全守則")
		}

		description {
			en("Extinguish a Campfire or a Soul Campfire")
			zh("使用灭火器或消防栓熄灭篝火或灵魂篝火")
			tw("使用滅火器或消防栓熄滅篝火或靈魂篝火")
		}
	}

	advancement("cold_blooded") {
		title {
			en("Cold Blooded")
			zh("心如止水")
			tw("心如止水")
		}

		description {
			en("BY FIRE BE PURGED!")
			zh("让火焰净化一切！")
			tw("讓火焰淨化一切！")
		}
	}

	advancement("went_into_fire") {
		title {
			en("Went into Fire!")
			zh("比博燃！")
			tw("媽媽，我燃起來了！")
		}

		description {
			en("Don't go to your barn with fire")
			zh("不要燃着进木屋啦！")
			tw("不要燃著進木屋！")
		}
	}

	advancement("pinpointed") {
		title {
			en("Pinpointed")
			zh("消防水枪")
			tw("消防水槍")
		}

		description {
			en("Extinguish fire with Extinguishing Star")
			zh("使用灭火之星熄灭火焰")
			tw("使用滅火之星熄滅火焰")
		}
	}

	add("item.fire_protection_equipment.firework_star") {
		en("Extinguish Star")
		zh("灭火之星")
		tw("滅火之星")
	}

	add("fire_protection_equipment.extinguish.dry_chemical") {
		en("Dry Chemical")
		zh("干粉")
		tw("乾粉")
	}

	add("fire_protection_equipment.extinguish.foams") {
		en("Foams")
		zh("泡沫")
		tw("泡沫")
	}

	add("fire_protection_equipment.extinguish.water") {
		en("Water")
		zh("水")
		tw("水")
	}

	add("fire_protection_equipment.extinguish.dry_ice") {
		en("Dry Ice")
		zh("干冰")
		tw("二氧化碳")
	}

	add(LinkingDeviceItem.TranslationBinding) {
		en("Device bound to {0}.")
		zh("设备绑定到 {0}")
		tw("設備綁定到 {0}")
	}

	add(LinkingDeviceItem.TranslationSelected) {
		en("Selected Device at {0}.")
		zh("选择设备 {0}")
		tw("選擇設備 {0}")
	}

	add(LinkingDeviceItem.TranslationMissingTarget) {
		en("Linking Device has not set the Target Controller")
		zh("连接设备尚未设定目标控制器。")
		tw("設備尚未設定目標控制器")
	}

	add(LinkingDeviceItem.TranslationTooFarAway) {
		en("Binding device too far away from Controller")
		zh("连接设备距离控制器距离过远")
		tw("設備距離控制器過遠")
	}

	add(LinkingDeviceItem.TranslationBoundNothing) {
		en("Device is not bound.")
		zh("设备尚未绑定")
		tw("設備尚未綁定")
	}
}