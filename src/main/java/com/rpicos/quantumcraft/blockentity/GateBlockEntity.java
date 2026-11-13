package com.rpicos.quantumcraft.blockentity;

import com.rpicos.quantumcraft.ModBlockEntities;
import com.rpicos.quantumcraft.sim.Complex;
import com.rpicos.quantumcraft.sim.Gates;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** A single-qubit gate. One block, cycled by right-click through the six gates this mod offers -
 *  the same "one block, several presets" pattern CircuitSimCraft's Resistor or Diode use, rather
 *  than a separate block per gate, since these are different flavors of the same kind of thing
 *  (a 2x2 unitary applied to one qubit at this time-step) rather than different physical
 *  component types. */
public class GateBlockEntity extends BlockEntity {

	private record Preset(String name, Complex[][] matrix) {
	}

	private static final Preset[] PRESETS = {
			new Preset("X", Gates.X),
			new Preset("Y", Gates.Y),
			new Preset("Z", Gates.Z),
			new Preset("H", Gates.H),
			new Preset("S", Gates.S),
			new Preset("T", Gates.T),
	};

	private int presetIndex = 0;

	public GateBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GATE, pos, state);
	}

	public void cyclePreset() {
		presetIndex = (presetIndex + 1) % PRESETS.length;
	}

	public String gateName() {
		return PRESETS[presetIndex].name();
	}

	public Complex[][] matrix() {
		return PRESETS[presetIndex].matrix();
	}
}
