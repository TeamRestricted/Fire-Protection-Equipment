package restricted.fpe.api;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record FireType(ResourceLocation registryName) {

	private static final List<FireType> VALUES = new ArrayList<>();

	public static List<FireType> values() {
		return new ArrayList<>(VALUES);
	}

	public FireType {
		VALUES.add(this);
	}

	public static final FireType NORMAL_FIRE = new FireType(new ResourceLocation("fire_protection_equipment", "normal_fire"));
	public static final FireType ELECTRICAL_FIRE = new FireType(new ResourceLocation("fire_protection_equipment", "electrical_fire"));

}
