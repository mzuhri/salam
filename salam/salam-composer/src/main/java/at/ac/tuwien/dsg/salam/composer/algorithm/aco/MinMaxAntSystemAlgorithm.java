package at.ac.tuwien.dsg.salam.composer.algorithm.aco;

import java.util.ArrayList;
import java.util.Hashtable;

import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.Solution;
import at.ac.tuwien.dsg.salam.composer.model.SolutionComponent;
import at.ac.tuwien.dsg.salam.util.Util;

public class MinMaxAntSystemAlgorithm extends AntSystemAlgorithm {

    private double minPheromone;
    private double maxPheromone;

    @Override
    public void init(String configFile, ConstructionGraph cons, Composer composer) { 
        super.init(configFile, cons, composer); 

        // init params
        minPheromone = Double.parseDouble(Util.getProperty(configFile, "min_pheromone"));
        maxPheromone = Double.parseDouble(Util.getProperty(configFile, "max_pheromone"));
    }

    @Override
    public void updatePheromene(ArrayList<Solution> solutions, 
            Hashtable<Solution, Double> objectiveValues) {

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
            // check min max
            if (pheromone>maxPheromone) pheromone = maxPheromone;
            else if (pheromone<minPheromone)  pheromone = minPheromone;
            // update
            comp.setPheromone(pheromone);
            constructionGraph.updateMinMaxPheromone(pheromone);
        }

    }

    protected void evaporatePheromones() {
        // evaporate current pheromone
        for (SolutionComponent comp: constructionGraph.getGraph().vertexSet()) {
            double pheromone = comp.getPheromone();
            pheromone = (1-evaporation) * pheromone;
            // check min max
            if (pheromone>maxPheromone) pheromone = maxPheromone;
            else if (pheromone<minPheromone) 
                pheromone = minPheromone;
            comp.setPheromone(pheromone);
            constructionGraph.updateMinMaxPheromone(pheromone);
        }
    }
}
