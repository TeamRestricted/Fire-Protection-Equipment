package restricted.fpe.api

import net.minecraft.world.level.block.Block
import restricted.fpe.logger
import restricted.fpe.registryPath

object FireProtectionApiImpl : FireProtectionApi {

	private val blockToFireType = mutableMapOf<Block, FireType>()

	override fun getFireBlocks(): MutableMap<FireType, MutableSet<Block>> =
		mutableMapOf<FireType, MutableSet<Block>>().apply {
			blockToFireType.forEach { (block, type) -> this.computeIfAbsent(type) { mutableSetOf() }.add(block) }
		}

	override fun registerFireBlock(block: Block, type: FireType) {
		blockToFireType.putIfAbsent(block, type).let {
			if(it != null) {
				logger.info("Registered ${block.registryName} as ${type.registryName}")
			} else {
				logger.warn("Unable to register ${block.registryPath} as ${type.registryName}")
			}
		}
	}

	override fun getFireType(block: Block): FireType? {
		return blockToFireType[block]
	}
}