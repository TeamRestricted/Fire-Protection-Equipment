package restricted.fpe.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import restricted.fpe.*
import restricted.fpe.buildCompoundTag
import restricted.fpe.util.BlockPosTag.Companion.blockPos
import restricted.fpe.util.BlockPosTag.Companion.toCompoundTag

class HomeFireStationBlockEntity(pos: BlockPos, state: BlockState) :
	BlockEntity(FPE.BlockEntityTypes.HomeFireStation, pos, state), IFireProtectBlockEntity {

	override var needSync: Boolean = false

	val connectedDevicesPos = mutableSetOf<Long>()

	var refreshedDeviceEntities = false
	val connectedDevices = mutableSetOf<AbstractHomeFireDevice<*>>()

	val onFire: Boolean get() = connectedDevices.any { it.onFire }

	fun bindDevice(entity: AbstractHomeFireDevice<*>) {
		connectedDevices += entity
		connectedDevicesPos += entity.blockPos.asLong()
		entity.boundTo = this
	}

	fun unbindDevice(entity: AbstractHomeFireDevice<*>) {
		connectedDevices -= entity
		connectedDevicesPos -= entity.blockPos.asLong()
		entity.boundTo = null
	}

	/**
	 * 发现 [load] 的时候这些 [BlockEntity] 还是空的，要等世界加载完之后再一次性填充进去。
	 * 不知道有没有什么其他的解决办法。
	 */
	fun refreshDeviceEntitiesWhenWorldIsReady() {
		val illegals = mutableSetOf<Long>()
		connectedDevicesPos.forEach {
			val entity = level!!.getBlockEntity(BlockPos.of(it))
			if(entity is AbstractHomeFireDevice<*>) {
				entity.boundTo = this
				connectedDevices += entity
			} else {
				logger.warn("Cannot cast $it(${it.javaClass.simpleName}) to AbstractHomeFireDevice, skipping and removing.")
				illegals += it
			}
		}
		connectedDevicesPos -= illegals
	}

	override fun getUpdateTag(): CompoundTag {
		return buildCompoundTag {
			put("bound_positions", ListTag().apply { addAll(connectedDevicesPos.map { BlockPos.of(it).toCompoundTag() }) }.apply { this.elementType.let(::println) })
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