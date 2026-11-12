# QuantumCraft

A Fabric mod that turns Minecraft into a quantum-circuit lab. Place single-qubit gate blocks
(Pauli X/Y/Z, Hadamard, S, T) and control/target marker blocks along up to three parallel qubit
"timeline" rows, then probe the result — either an exact readout of every basis state's
probability, or a genuine collapsing Born-rule measurement — all backed by a real
complex-amplitude statevector simulator, not a scripted approximation.

This is a teaching tool, a companion to [CircuitSimCraft](https://github.com/rpicos-uib/circuitsimcraft)
(the same idea applied to analog electronics): the goal is for every probability and measurement
outcome you see in-game to come from actually simulating the circuit you built, down to genuine
entanglement (a Bell state really has zero probability of reading `01` or `10`) and genuine
measurement collapse.

## Requirements

- **Minecraft 26.2** — Mojang's new `year.release` versioning, *not* the old `1.21.x` line.
- **Fabric Loader** >= 0.19.3
- **Fabric API** 0.155.2+26.2 (must match the Minecraft version)
- **Java 25**

## Installation

### Option A — prebuilt jar

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 26.2.
2. Download the **Fabric API** jar for 26.2 from [Modrinth](https://modrinth.com/mod/fabric-api)
   and drop it in your `mods` folder.
3. Grab `quantumcraft-<version>.jar` from this repo's [Releases](../../releases) page and drop it
   in the same `mods` folder.
4. Make sure the profile you launch with is on Java 25+.
5. Launch with the Fabric profile.

### Option B — build from source

```bash
git clone https://github.com/rpicos-uib/quantumcraft.git
cd quantumcraft
./gradlew build
```

The mod jar comes out at `build/libs/quantumcraft-<version>.jar`. You need a JDK 25 available;
either make it your default `java`, or point Gradle at it explicitly:

```bash
JAVA_HOME=/path/to/jdk-25 ./gradlew build
```

Useful dev tasks: `./gradlew runClient` (launches a dev client with the mod loaded) and
`./gradlew runServer` (headless dedicated server — handy for checking the mod loads without a GUI).

## The components

| | Block/Item | What it is |
|---|---|---|
| <img src="latex/figures/icons/qubit_line.png" width="32"> | **Qubit Line** | Marks the anchor of one qubit's timeline row. Up to three, placed touching each other, are found together by a single probe click; each one's own row extends virtually to the east, where gate and marker blocks are placed. No adjustable state. |
| <img src="latex/figures/icons/gate.png" width="32"> | **Quantum Gate** | A single-qubit gate. Right-click (empty hand) cycles through Pauli X → Y → Z → Hadamard → S → T → back to X. Place it a given number of steps east of a qubit line's anchor to apply it at that point in the circuit's time order. |
| <img src="latex/figures/icons/control_node.png" width="32"> | **Control Node** | Half of a multi-qubit gate. Paired with a Target Node at the same east-offset on a different qubit row: one control + one target makes a CNOT, two controls + one target makes a Toffoli (CCX). |
| <img src="latex/figures/icons/target_x_node.png" width="32"> | **Target Node** | The other half of a multi-qubit gate — see Control Node above. |
| <img src="latex/figures/icons/probe.png" width="32"> | **Quantum Probe** | Right-click a qubit line for an **ideal readout**: the exact probability of every basis state, computed with no randomness and no collapse. Shift-right-click instead performs a genuine **measurement**: the state is sampled once (Born rule) and collapsed, and only that one outcome is reported. The result HUD labels the two modes distinctly so they're never confused. |

### How a circuit is read

Right-clicking the probe against a qubit line flood-fills outward through any other qubit lines
directly touching it (six-face adjacency) to find every qubit in the same circuit, assigns qubit
indices by position, then scans east from each one's own anchor, one block at a time, recording
whatever gate or marker occupies each offset. Gates/markers sharing the same offset (the same
"time column") across different rows become one multi-qubit operation. An offset with nothing at
it is just a time-step doing nothing — not an error.

## Crafting recipes

All vanilla ingredients, no dependency on any other mod.

| Recipe | Notes |
|---|---|
| **Qubit Line ×4** | Glass + 2 redstone (shapeless). |
| **Quantum Gate ×2** | Amethyst shard / glass / amethyst shard, stacked vertically (shaped). |
| **Control Node ×2** | Redstone + iron nugget (shapeless). |
| **Target Node ×2** | Redstone + gold nugget (shapeless). |
| **Quantum Probe ×1** | Ender eye / amethyst shard / stick, stacked vertically (shaped). |

All items are also available in their own **QuantumCraft** creative-inventory tab.

## Worked experiments

Five complete circuits are built directly into the world at `latex/figures` (see
[`latex/sections/06b_experiments.tex`](latex/sections/06b_experiments.tex) for the full writeup
with exact gate placements and closed-form expected results): a single-qubit superposition, a
Bell state, a phase-kickback interference demo, a three-qubit GHZ state, and a Toffoli-gate
truth-table verification — laid out ten, twenty, thirty-four, forty-eight, and sixty-four blocks
east of spawn respectively.

## Architecture, for anyone extending this

```
src/main/java/com/rpicos/quantumcraft/
  sim/            Pure-Java statevector simulator — zero Minecraft dependency, unit-testable standalone
  block/          Block classes (placement, right-click preset cycling)
  blockentity/    BlockEntity classes (gate presets, marker/qubit-line state)
  network/        World-scan-to-circuit bridge + the client<->server probe result protocol
  item/           The probe item
src/client/java/com/rpicos/quantumcraft/client/   Result HUD rendering, client-side networking
```

### The simulator (`sim` package)

`QuantumRegister` holds a complex-amplitude statevector (`Complex[]`, little-endian qubit
indexing) and applies single-qubit gates, general controlled-gates, and a Toffoli gate directly to
it; `measure()` implements Born-rule sampling and collapse. `QuantumCircuit` is just an ordered
list of operations, run fresh from `|0...0>` every time. Because this package has no Minecraft
imports at all, it was verified standalone with a plain `javac`/`java` test program against nine
closed-form and statistical checks (Bell/GHZ states, Toffoli truth table, phase kickback,
20,000-trial measurement statistics, unitarity preservation) before being wired into the rest of
the mod.

### Adding a new gate

Single-qubit gates are presets on the one `GateBlockEntity` class (see `sim/Gates.java` for the
matrix definitions) — add a new `Complex[][]` there and a new `Preset` entry, no new block class
needed. A genuinely new multi-qubit gate would need a new marker-block pair and a corresponding
case in `network/QuantumScanner.java`'s column-processing logic.

### Known limitations (v0.1)

- **Fixed gate set, no continuous rotation angles** — right-click cycles a small fixed preset
  list rather than a numeric-entry interface.
- **Hard cap of three qubits** — chosen so the result HUD can show every basis state's probability
  at once; raising it would need the HUD redesigned.
- **No mid-circuit measurement or classical feedback** — a circuit always runs start to finish
  before any measurement occurs.

## Contributing

Issues and PRs welcome. If you're adding a gate or feature, a note on which part of the standard
quantum-computing formalism it implements is appreciated — the goal of this mod is that in-game
behavior is actually correct, not just plausible-looking.

## Citation

If you use QuantumCraft — in a classroom, a paper, a demo, a derivative mod, anywhere — please
credit the authors: Rodrigo Picos, Stavros G. Stavrinides, George Stavrinides, Ariadna Picos, and
Gerard Picos. A link back to this repository is enough for informal use; for academic work,
please cite it as:

```
Rodrigo Picos, Stavros G. Stavrinides, George Stavrinides, Ariadna Picos, and Gerard Picos.
QuantumCraft: a Fabric mod for teaching quantum circuits in Minecraft.
https://github.com/rpicos-uib/quantumcraft, 2026.
```

BibTeX:

```bibtex
@software{picos_quantumcraft,
  author = {Picos, Rodrigo and Stavrinides, Stavros G. and Stavrinides, George and Picos, Ariadna and Picos, Gerard},
  title  = {QuantumCraft: a Fabric mod for teaching quantum circuits in Minecraft},
  url    = {https://github.com/rpicos-uib/quantumcraft},
  year   = {2026}
}
```

See also [`CITATION.cff`](CITATION.cff), which GitHub reads automatically for its "Cite this
repository" button.

## License

MIT — see [LICENSE](LICENSE). The license permits reuse without attribution; the citation request
above is a courtesy ask on top of that, not an added legal restriction.
