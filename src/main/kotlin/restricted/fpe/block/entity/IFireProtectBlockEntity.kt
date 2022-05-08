package restricted.fpe.block.entity

import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.entity.BlockEntity
import restricted.fpe.letOnRemote

interface IFireProtectBlockEntity {

	var needSync: Boolean

	fun tickSync(blockEntity: BlockEntity) {
		if(needSync) {
			executeSync(blockEntity)
			needSync = false
		}
	}

	private fun executeSync(blockEntity: BlockEntity) {
		blockEntity.level?.letOnRemote { serverLevel ->
			ClientboundBlockEntityDataPacket.create(blockEntity).let { packet ->
				serverLevel.chunkSource.chunkMap.getPlayers(ChunkPos(blockEntity.blockPos), false).forEach { player ->
					player.connection.send(packet)
				}
			}
		}
	}

	companion object {
		fun <T: BlockEntity> T.tickSync() {
			if(this is IFireProtectBlockEntity) { tickSync(this) }
		}
	}

}