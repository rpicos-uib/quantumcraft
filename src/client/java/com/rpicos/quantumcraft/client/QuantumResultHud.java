package com.rpicos.quantumcraft.client;

import com.rpicos.quantumcraft.item.QuantumProbeItem;
import com.rpicos.quantumcraft.network.QuantumResultPayload;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Shows the outcome of the last probe use: one row per basis state (up to 8, for 3 qubits), each
 * with its bit-string label and a bar proportional to its probability - the same "ideal readout"
 * a statevector simulator gives for free, which no physical quantum computer could ever show you
 * directly. When the last use was a measurement instead, only the single collapsed outcome row
 * is highlighted and labelled "MEASURED", to make the distinction between reading the exact
 * amplitudes and actually collapsing the state visually unmistakable. Any scan warnings (e.g. an
 * unpaired control/target column) are printed below the bars rather than silently dropped.
 */
public class QuantumResultHud implements HudElement {

	private static final int WIDTH = 118;
	private static final int MARGIN = 6;
	private static final int ROW_HEIGHT = 9;
	private static final int HEADER_HEIGHT = 10;
	private static final int BAR_MAX_WIDTH = 50;
	private static final int IDEAL_COLOR = 0xFF60C0E0;
	private static final int MEASURED_COLOR = 0xFFE0A060;
	private static final int WARNING_COLOR = 0xFFE06060;

	@Override
	public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		if (player == null || !isHoldingProbe(player)) {
			return;
		}

		QuantumResultPayload data = QuantumResultClientState.current();
		Font font = client.font;

		int rowCount = data == null ? 1 : data.probabilities().size();
		List<String> warnings = data == null ? List.of() : data.warnings();
		int height = HEADER_HEIGHT + rowCount * ROW_HEIGHT + warnings.size() * ROW_HEIGHT + 4;

		int x0 = MARGIN;
		int y0 = extractor.guiHeight() - height - MARGIN;

		extractor.fill(x0, y0, x0 + WIDTH, y0 + height, 0xC0101014);
		extractor.outline(x0, y0, WIDTH, height, 0xFF3A3A40);

		if (data == null) {
			extractor.text(font, "no reading yet", x0 + 6, y0 + height / 2 - 4, 0xFF808080, false);
			return;
		}

		int barColor = data.isMeasurement() ? MEASURED_COLOR : IDEAL_COLOR;
		String header = data.isMeasurement() ? "MEASURED" : "IDEAL READOUT";
		extractor.text(font, header, x0 + 4, y0 + 2, 0xFFDDDDDD, false);

		int numQubits = data.numQubits();
		List<Float> probabilities = data.probabilities();
		int rowY = y0 + HEADER_HEIGHT;
		for (int state = 0; state < probabilities.size(); state++) {
			boolean highlighted = data.isMeasurement() && state == data.measuredOutcome();
			float probability = probabilities.get(state);

			String label = toBitString(state, numQubits);
			extractor.text(font, label, x0 + 4, rowY + 1, highlighted ? MEASURED_COLOR : 0xFFAAAAAA, false);

			int barX0 = x0 + 4 + font.width("000") + 4;
			int barWidth = Math.round(probability * BAR_MAX_WIDTH);
			if (barWidth > 0) {
				extractor.fill(barX0, rowY, barX0 + barWidth, rowY + ROW_HEIGHT - 1,
						highlighted ? MEASURED_COLOR : barColor);
			}

			String percent = String.format("%.0f%%", probability * 100.0);
			extractor.text(font, percent, barX0 + BAR_MAX_WIDTH + 3, rowY + 1, 0xFFAAAAAA, false);

			rowY += ROW_HEIGHT;
		}

		for (String warning : warnings) {
			extractor.text(font, warning, x0 + 4, rowY + 1, WARNING_COLOR, false);
			rowY += ROW_HEIGHT;
		}
	}

	private static String toBitString(int value, int numQubits) {
		StringBuilder sb = new StringBuilder();
		for (int q = numQubits - 1; q >= 0; q--) {
			sb.append((value >> q) & 1);
		}
		return sb.toString();
	}

	private static boolean isHoldingProbe(Player player) {
		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		return main.getItem() instanceof QuantumProbeItem || off.getItem() instanceof QuantumProbeItem;
	}
}
