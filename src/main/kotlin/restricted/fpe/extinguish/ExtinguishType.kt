package restricted.fpe.extinguish

enum class ExtinguishType {
	DRY_CHEMICAL, // 干粉
	FOAMS, // 泡沫
	WATER, // 水
	DRY_ICE, // 干冰

	NONE;

	companion object {
		val ALL = arrayOf(DRY_CHEMICAL, FOAMS, WATER, DRY_ICE)
	}
}