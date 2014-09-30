package at.ac.tuwien.dsg.salam.composer.algorithm.aco;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;

import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.Solution;
import at.ac.tuwien.dsg.salam.composer.model.SolutionComponent;
import at.ac.tuwien.dsg.salam.util.Util;

public class AntColonySystemAlgorithm extends MinMaxAntSystemAlgorithm {

    private static final int SEED_Q = 2002;
    private double q0;
    private double pheromoneDecay;
    private double initialPheromone;
    private UniformRealDistribution distQ = null;

    @Override
    public void init(String configFile, ConstructionGraph cons, Composer composer) {
        super.init(configFile, cons, composer);
        this.distQ = new UniformRealDistribution(new MersenneTwister(SEED_Q), 0, 1);
        // init params
        q0 = Double.parseDouble(Util.getProperty(configFile, "q_0"));
        pheromoneDecay = Double.parseDouble(Util.getProperty(configFile, "pheromone_decay"));
        initialPheromone = Double.parseDouble(Util.getProperty(configFile, "initial_pheromone"));
    }

    @Override
    protected SolutionComponent selectComponent(ArrayList<SolutionComponent> options, 
            Hashtable<SolutionComponent,Double> heuristicScores) {

        // measure score (based on pheromone & heuristic factor)
        int i = 0;
        double totalProbability = 0;
        ArrayList<Double> probabilities = new ArrayList<Double>();
        double bestScore = 0;
        SolutionComponent bestComp = null;

        while (i<options.size()) {

            // get heuristc score
            double heuristicScore = heuristicScores.get(options.get(i));
            if (bestScore<heuristicScore) {
                bestScore = heuristicScore;
                bestComp = options.get(i);
            }

            // get pheromone
            double pheromone = options.get(i).getPheromone(); 

            // calculate probability, we dont have to normalize to 1 here
            double probability = Math.pow(pheromone, pheromoneWeight) * Math.pow(heuristicScore, heuristicWeight);

            totalProbability += probability; 
            probabilities.add(probability); 
            i++;
        }

        SolutionComponent comp = null;
        double q = distQ.sample();

        if (q<=q0) {
            // exploitation factor
            comp = bestComp;
        } else {
            // exploration factor
            // randomly select one feasible option
            // randomize
            double val = distSelector.sample() * totalProbability;
            double sum = 0;
            int count = 0;
            while (sum<val && count<probabilities.size()) {
                sum += probabilities.get(count);
                if (sum<val) count++;
            }
            comp = options.get(count);
        }

        // local pheromone update for this selected component
        double pheromone = comp.getPheromone();
        pheromone = ((1-pheromoneDecay) * pheromone) + (pheromoneDecay * initialPheromone);
        comp.setPheromone(pheromone);
        constructionGraph.updateMinMaxPheromone(pheromone);

        return comp;
    }

    @Override
    public void updatePheromene(ArrayList<Solution> solutions, Hashtable<Solution, Double> objectiveValues) {

        if (solutions.size()==0) return;

        evaporatePheromones();

        /*    
    // find best solution
    for (Solution solution: solutions) {
      if (currentBestScore>solution.getAggregateScore()) {
        currentBestScore = solution.getAggregateScore();
        currentBestSolution = solution;
      }
    }
         */

        // update pheromone for best solution only
        double objectivityCost = objectiveValues.get(currentBestSolution);  
        for (SolutionComponent comp: currentBestSolution.getList()) {
            double pheromone = comp.getPheromone();
            pheromone += 1 / objectivityCost;
            // update
            comp.setPheromone(pheromone);
            constructionGraph.updateMinMaxPheromone(pheromone);
        }

    }
}
