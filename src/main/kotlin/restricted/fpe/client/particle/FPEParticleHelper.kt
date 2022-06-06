package restricted.fpe.client.particle

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.client.resources.model.ModelManager
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.model.data.EmptyModelData

object FPEParticleHelper {

	@OnlyIn(Dist.CLIENT)
	class Factory(val blockState: BlockState) : ParticleProvider<SimpleParticleType> {

		private val modelManager: ModelManager = Minecraft.getInstance().modelManager

		override fun createParticle(
			pType: SimpleParticleType,
			pLevel: ClientLevel,
			pX: Double,
			pY: Double,
			pZ: Double,
			pXSpeed: Double,
			pYSpeed: Double,
			pZSpeed: Double
		): Particle {
			return object : BreakingItemParticle(pLevel, pX, pY, pZ, ItemStack.EMPTY) {
				init {
					setSprite(
						modelManager.blockModelShaper.getBlockModel(blockState).getParticleIcon(EmptyModelData.INSTANCE)
					)
					xd = 0.01 * Math.random() - 0.01 * Math.random()
					yd = 0.01 * Math.random() - 0.01 * Math.random()
					zd = 0.01 * Math.random() - 0.01 * Math.random()
					age = (3.0 / (0.5 + Math.random())).toInt()
					quadSize *= 0.2F
					gravity = 0.01F
				}
			}
		}
	}

}