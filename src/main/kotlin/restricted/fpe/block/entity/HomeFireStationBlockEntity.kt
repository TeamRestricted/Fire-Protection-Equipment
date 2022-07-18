package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import restricted.fpe.*
import restricted.fpe.util.BlockPosTag.Companion.blockPos
import restricted.fpe.util.BlockPosTag.Companion.toCompoundTag

class HomeFireStationBlockEntity(pos: BlockPos, state: BlockState) :
	BlockEntity(FPE.BlockEntityTypes.HomeFireStation, pos, state), IFireProtectBlockEntity {

	override var needSync: Boolean = false

	val connectedDevicesPos = mutableSetOf<Long>()

	var refreshedDeviceEntities = false
	val connectedDevices = mutableSetOf<AbstractHomeFireDevice<*>>()

	val onFire: Boolean get() = connectedDevices.any { it.onFire }

	fun registerDevice(entity: AbstractHomeFireDevice<*>) {
		connectedDevices += entity
		connectedDevicesPos += entity.blockPos.asLong()
		setChanged()
	}

	fun unregisterDevice(entity: AbstractHomeFireDevice<*>) {
		connectedDevices -= entity
		connectedDevicesPos -= entity.blockPos.asLong()
		setChanged()
	}

	/**
	 * 发现 [load] 的时候这些 [BlockEntity] 还是空的，要等世界加载完之后再一次性填充进去。
	 * 不知道有没有什么其他的解决办法。
	 */
	fun refreshDeviceEntitiesWhenWorldIsReady() {
		val illegals = mutableSetOf<Long>()
		connectedDevicesPos.map(BlockPos::of).forEach {
			if(level!!.isLoaded(it)) {
				val entity = level!!.getBlockEntity(it)
				if(entity is AbstractHomeFireDevice<*>) {
					entity.bind(this)
					connectedDevices += entity
				} else {
					logger.warn("Cannot cast $it(${it.javaClass.simpleName}) to AbstractHomeFireDevice, skipping and removing.")
					illegals += it.asLong()
				}
			}
		}
		connectedDevicesPos -= illegals
	}

	override fun getUpdateTag(): CompoundTag {
		return buildCompoundTag {
			put("bound_positions", ListTag().apply { addAll(connectedDevicesPos.map { BlockPos.of(it).toCompoundTag() }) })
		}
	}

	override fun saveAdditional(pTag: CompoundTag) {
		super.saveAdditional(pTag)
		pTag.put("bound_positions", ListTag().apply { addAll(connectedDevicesPos.map { BlockPos.of(it).toCompoundTag() }) })
	}

	override fun load(pTag: CompoundTag) {
		super.load(pTag)
		pTag.getList("bound_positions", 10).forEach {
			val pos = (it as CompoundTag).blockPos
			connectedDevicesPos += pos.asLong()
		}
	}
}