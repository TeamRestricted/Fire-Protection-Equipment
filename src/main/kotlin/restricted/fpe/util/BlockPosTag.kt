package restricted.fpe.util

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag

class BlockPosTag(): CompoundTag() {

	constructor(blockPos: BlockPos): this() {
		putBlockPos(blockPos)
	}

	constructor(x: Int, y: Int, z: Int): this() {
		putBlockPos(x, y, z)
	}

	fun putBlockPos(blockPos: BlockPos) {
		putBlockPos(blockPos.x, blockPos.y, blockPos.z)
	}

	fun putBlockPos(x: Int, y: Int, z: Int) {
		putInt("x", x)
		putInt("y", y)
		putInt("z", z)
	}

	val x: Int get() = getInt("x")
	val y: Int get() = getInt("y")
	val z: Int get() = getInt("z")

	val blockPos: BlockPos get() = BlockPos(x, y, z)

	companion object {
		fun BlockPos.toCompoundTag() = BlockPosTag(this)

		val CompoundTag.x get() = getInt("x")
		val CompoundTag.y get() = getInt("y")
		val CompoundTag.z get() = getInt("z")

		val CompoundTag.blockPos get() = BlockPos(x, y, z)
	}

}