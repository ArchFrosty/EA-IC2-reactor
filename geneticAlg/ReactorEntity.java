package geneticAlg;

import simulator.Reactor;
import simulator.ReactorComponent;

public class ReactorEntity implements Comparable {

	public final Reactor reactor = new Reactor();

	public final boolean[][] alreadyBroken = new boolean[6][9];

	public final boolean[][] needsCooldown = new boolean[6][9];

	public final int initialHeat = 0;

	public double minEUoutput = Double.MAX_VALUE;

	public double maxEUoutput = 0.0;

	public double minHeatOutput = Double.MAX_VALUE;

	public double maxHeatOutput = 0.0;

	public int brokenComponents = 0;

	public double leftoverHeat = 0;

	public double avgEfficiency = 0;

	public double avgEUoutput = 0;

	public double fitness = 0;

	int cooldownTicks = 0;

	public String code;

	boolean reachedBurn = false;
	boolean reachedEvaporate = false;
	boolean reachedHurt = false;
	boolean reachedLava = false;
	boolean reachedExplode = false;


	public ReactorEntity(String code){
		reactor.setCode(code);
		this.code = code;
	}
	public void calculateFitness(){

		runSimulation();

		fitness += avgEUoutput*6;
		fitness += avgEfficiency*10;
		if(leftoverHeat > 0){
			fitness -= leftoverHeat;
			fitness -= 500;
		}
		fitness -= brokenComponents *100;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 9; j++) {
				if(needsCooldown[i][j]){
					fitness -= 5;//dont know bout this
				}
			}
		}

		fitness -= cooldownTicks*10;

		if(reachedBurn){
			fitness -= 150;
			fitness -= avgEUoutput;
		}

		if(reachedEvaporate){
			fitness -= 300;
			fitness -= avgEUoutput*2;
		}

		if(reachedHurt){
			fitness -= 500;
			fitness -= avgEUoutput*3;
		}

		if(reachedLava){
			fitness -= 800;
			fitness -= avgEUoutput*4;
		}

		if(reachedExplode){
			fitness -= 1000;
			fitness -= avgEUoutput*5;
		}

	}


	private void runSimulation() {
		long startTime = System.nanoTime();
		int reactorTicks = 0;
		int totalRodCount = 0;
		try {
//            publish("");
//            publish("Simulation started.\n");
			reactor.setCurrentHeat(initialHeat);
			reactor.clearVentedHeat();
			double minReactorHeat = initialHeat;
			double maxReactorHeat = initialHeat;
			for (int row = 0; row < 6; row++) {
				for (int col = 0; col < 9; col++) {
					ReactorComponent component = reactor.getComponentAt(row, col);
					if (component != null) {
						component.clearCurrentHeat();
						component.clearDamage();
						totalRodCount += component.getRodCount();
					}
//                    publish(String.format("R%dC%d:0xC0C0C0", row, col));
				}
			}
			double lastEUoutput = 0.0;
			double totalEUoutput = 0.0;
			double lastHeatOutput = 0.0;
			double totalHeatOutput = 0.0;
			double maxGeneratedHeat = 0.0;
			do {
				reactor.clearEUOutput();
				reactor.clearVentedHeat();
				for (int row = 0; row < 6; row++) {
					for (int col = 0; col < 9; col++) {
						ReactorComponent component = reactor.getComponentAt(row, col);
						if (component != null) {
							component.preReactorTick();
						}
					}
				}
				double generatedHeat = 0.0;
				for (int row = 0; row < 6; row++) {
					for (int col = 0; col < 9; col++) {
						ReactorComponent component = reactor.getComponentAt(row, col);
						if (component != null && !component.isBroken()) {
							generatedHeat += component.generateHeat();
							maxReactorHeat = Math.max(reactor.getCurrentHeat(), maxReactorHeat);
							minReactorHeat = Math.min(reactor.getCurrentHeat(), minReactorHeat);
							component.dissipate();
							maxReactorHeat = Math.max(reactor.getCurrentHeat(), maxReactorHeat);
							minReactorHeat = Math.min(reactor.getCurrentHeat(), minReactorHeat);
							component.transfer();
							maxReactorHeat = Math.max(reactor.getCurrentHeat(), maxReactorHeat);
							minReactorHeat = Math.min(reactor.getCurrentHeat(), minReactorHeat);
						}
						if (maxReactorHeat >= 0.4 * reactor.getMaxHeat() && !reachedBurn) {
//                            publish(String.format("Reactor will reach \"Burn\" temperature at %d seconds.\n", reactorTicks));
							reachedBurn = true;
						}
						if (maxReactorHeat >= 0.5 * reactor.getMaxHeat() && !reachedEvaporate) {
//                            publish(String.format("Reactor will reach \"Evaporate\" temperature at %d seconds.\n", reactorTicks));
							reachedEvaporate = true;
						}
						if (maxReactorHeat >= 0.7 * reactor.getMaxHeat() && !reachedHurt) {
							//                           publish(String.format("Reactor will reach \"Hurt\" temperature at %d seconds.\n", reactorTicks));
							reachedHurt = true;
						}
						if (maxReactorHeat >= 0.85 * reactor.getMaxHeat() && !reachedLava) {
//                            publish(String.format("Reactor will reach \"Lava\" temperature at %d seconds.\n", reactorTicks));
							reachedLava = true;
						}
						if (maxReactorHeat >= reactor.getMaxHeat() && !reachedExplode) {
//                            publish(String.format("Reactor will explode at %d seconds.\n", reactorTicks));
							reachedExplode = true;
						}
					}
				}
				maxGeneratedHeat = Math.max(generatedHeat, maxGeneratedHeat);
				for (int row = 0; row < 6; row++) {
					for (int col = 0; col < 9; col++) {
						ReactorComponent component = reactor.getComponentAt(row, col);
						if (component != null && !component.isBroken()) {
							component.generateEnergy();
						}
					}
				}
				lastEUoutput = reactor.getCurrentEUoutput();
				totalEUoutput += lastEUoutput;
				lastHeatOutput = reactor.getVentedHeat();
				totalHeatOutput += lastHeatOutput;
				if (reactor.getCurrentHeat() <= reactor.getMaxHeat() && lastEUoutput > 0.0) {
					reactorTicks++;
					minEUoutput = Math.min(lastEUoutput, minEUoutput);
					maxEUoutput = Math.max(lastEUoutput, maxEUoutput);
					minHeatOutput = Math.min(lastHeatOutput, minHeatOutput);
					maxHeatOutput = Math.max(lastHeatOutput, maxHeatOutput);
				}
				for (int row = 0; row < 6; row++) {
					for (int col = 0; col < 9; col++) {
						ReactorComponent component = reactor.getComponentAt(row, col);
						if (component != null && component.isBroken() && !alreadyBroken[row][col] && !component.getClass().getName().contains("FuelRod")) {
//                            publish(String.format("R%dC%d:0xFF0000", row, col));
							alreadyBroken[row][col] = true;
//                           publish(String.format("R%dC%d:+Broke after %,d seconds.", row, col, reactorTicks));
							brokenComponents++;
						}
					}
				}
			} while (reactor.getCurrentHeat() <= reactor.getMaxHeat() && lastEUoutput > 0.0);
//            publish(String.format("Reactor minimum temperature: %,.2f\n", minReactorHeat));
			//           publish(String.format("Reactor maximum temperature: %,.2f\n", maxReactorHeat));
			avgEUoutput = totalEUoutput / (reactorTicks * 20);
			if (reactor.getCurrentHeat() <= reactor.getMaxHeat()) {
//                publish(String.format("Fuel rods (if any) stopped after %,d seconds.\n", reactorTicks));
				if (reactorTicks > 0) {
					if (reactor.isFluid()) {
//                       publish(String.format("Total heat output: %,.0f\nAverage heat output before fuel rods stopped: %.2f Hu/s\nMinimum heat output: %.2f Hu/s\nMaximum heat output: %.2f Hu/s\n", 2 * totalHeatOutput, 2 * totalHeatOutput / reactorTicks, 2 * minHeatOutput, 2 * maxHeatOutput));
						if (totalRodCount > 0) {
//                           publish(String.format("Efficiency: %.2f average, %.2f minimum, %.2f maximum\n", totalHeatOutput / reactorTicks / 4 / totalRodCount, minHeatOutput / 4 / totalRodCount, maxHeatOutput / 4 / totalRodCount));
						}
					} else {
//                      publish(String.format("Total EU output: %,.0f (%.2f EU/t min, %.2f EU/t max, %.2f EU/t average)\n", totalEUoutput, minEUoutput / 20.0, maxEUoutput / 20.0, totalEUoutput / (reactorTicks * 20)));
						if (totalRodCount > 0) {
//                            publish(String.format("Efficiency: %.2f average, %.2f minimum, %.2f maximum\n", totalEUoutput / reactorTicks / 100 / totalRodCount, minEUoutput / 100 / totalRodCount, maxEUoutput / 100 / totalRodCount));
							avgEfficiency = totalEUoutput / reactorTicks / 100 / totalRodCount;
						}
					}
				}
				leftoverHeat = reactor.getCurrentHeat();
				lastHeatOutput = 0.0;
				totalHeatOutput = 0.0;
				double prevReactorHeat = reactor.getCurrentHeat();
				double prevTotalComponentHeat = 0.0;
				for (int row = 0; row < 6; row++) {
					for (int col = 0; col < 9; col++) {
						ReactorComponent component = reactor.getComponentAt(row, col);
						if (component != null && !component.isBroken()) {
							prevTotalComponentHeat += component.getCurrentHeat();
							if (component.getCurrentHeat() > 0.0) {
								//                               publish(String.format("R%dC%d:0xFFFF00", row, col));
//                                publish(String.format("R%dC%d:+Had %,.2f heat left when reactor stopped.", row, col, component.getCurrentHeat()));
								needsCooldown[row][col] = true;
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			if (cooldownTicks == 0) {
//                publish(String.format("Error at reactor tick %d\n", reactorTicks));
			} else {
				//               publish(String.format("Error at cooldown tick %d\n", cooldownTicks));
			}
//            publish(e.toString(), " ", Arrays.toString(e.getStackTrace()));
		}
		long endTime = System.nanoTime();
		//       publish(String.format("Simulation took %.2f seconds.\n", (endTime - startTime) / 1e9));

	}



	@Override
	public int compareTo(Object o) {
		ReactorEntity e = (ReactorEntity)o;
		return (int)(e.fitness - this.fitness);
	}
}
