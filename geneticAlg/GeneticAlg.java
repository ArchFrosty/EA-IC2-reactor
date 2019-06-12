package geneticAlg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by igorf on 02.03.2016.
 */
public class GeneticAlg {

	public final int POPULATION_SIZE = 100;
	public final int TOURNAMENT_SIZE = 3;
	public final int GENERATIONS = 1000;
	public final int FREE_PASS = 1;
	public final int FRESH_BLOOD = 15;
	public final int MUTATION_CHANCE = 700000; // x in a 1 000 000

	codeHelper ch = new codeHelper();
	Random rnd = new Random();

	ReactorEntity best = new ReactorEntity(ch.getRandomCode());
	ArrayList<ReactorEntity> population = new ArrayList<>(POPULATION_SIZE);

	public GeneticAlg() {
		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(new ReactorEntity(ch.getRandomCode()));
		}
		best.calculateFitness();
	}

	public void run() {

		for (int k = 0; k < GENERATIONS; k++) {

			population.stream().parallel().forEach(ReactorEntity::calculateFitness);

			Collections.sort(population);

			if (population.get(0).fitness > best.fitness) {
				best = new ReactorEntity(population.get(0).reactor.getCode());
				best.calculateFitness();
				System.out.printf("Found new best! %f\n", best.fitness);
			}

			ArrayList<ReactorEntity> newPop = new ArrayList<>(POPULATION_SIZE);

			for (int i = 0; i < FREE_PASS; i++) {
				newPop.add(new ReactorEntity(population.get(i).reactor.getCode()));
			}

			for (int i = 0; i < FRESH_BLOOD; i++) {
				newPop.add(new ReactorEntity(ch.getRandomCode()));
			}

			for (int i = FREE_PASS + FRESH_BLOOD; i < POPULATION_SIZE; i++) {
				ArrayList<ReactorEntity> tournament1 = new ArrayList<>(TOURNAMENT_SIZE);
				ArrayList<ReactorEntity> tournament2 = new ArrayList<>(TOURNAMENT_SIZE);

				for (int j = 0; j < TOURNAMENT_SIZE; j++) {
					tournament1.add(population.get(rnd.nextInt(POPULATION_SIZE)));
					tournament2.add(population.get(rnd.nextInt(POPULATION_SIZE)));
				}
				Collections.sort(tournament1);
				Collections.sort(tournament2);

				String childCode = ch.onePointCrossover(tournament1.get(0).reactor.getCode(), tournament2.get(0).reactor.getCode());
				int proc = rnd.nextInt(1000000);
				if (proc < MUTATION_CHANCE) {
					childCode = ch.mutateGene(childCode);
				}

				newPop.add(new ReactorEntity(childCode));
			}
			System.out.printf("Just finished generation %d with best fitness of %f, code: %s\n", k, population.get(0).fitness, population.get(0).reactor.getCode());
			population = newPop;
		}

		System.out.printf("Best found(%f) reactor was: %s", best.fitness, best.reactor.getCode());
	}
}
