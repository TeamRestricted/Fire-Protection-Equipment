package restricted.fpe.event

import net.minecraftforge.eventbus.api.Cancelable
import net.minecraftforge.eventbus.api.Event
import restricted.fpe.extinguish.ExtinguishContext

@Cancelable
class FireExtinguishEvent(
	val context: ExtinguishContext
) : Event() {
}