import geneticAlg.GeneticAlg;

public class Main {

    public static void main(String[] args) {
        /*ReactorEntity re = new ReactorEntity("030C0D140D0D0C0D15150C0D0D0C0D0D030D150D030D0D030D0D0C0C0D0D0C0D0D0C0D150D030D0D030D0D030D150D0C150D0C150D0C");
        re.calculateFitness();
		System.out.printf("%f %f %f", re.fitness, re.avgEUoutput, re.leftoverHeat);*/
		GeneticAlg g = new GeneticAlg();
		g.run();
	}
}
