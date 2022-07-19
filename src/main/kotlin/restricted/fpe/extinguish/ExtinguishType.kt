package restricted.fpe.extinguish

enum class ExtinguishType {
	DRY_CHEMICAL, // 干粉
	FOAMS, // 泡沫
	WATER, // 水
	DRY_ICE, // 干冰

	FIRE_SAVIOR, // 救火器类型

	NONE;

	val value: String get() = name.lowercase()

	companion object {
		val ALL = arrayOf(DRY_CHEMICAL, FOAMS, WATER, DRY_ICE)

		@JvmStatic
		@JvmName("fromString")
		fun valueOf(str: String): ExtinguishType? {
			return when(str) {
				"dry_chemical" -> DRY_CHEMICAL
				"foams" -> FOAMS
				"water" -> WATER
				"dry_ice" -> DRY_ICE
				else -> null
			}
		}
	}
}