package at.ac.tuwien.dsg.salam.composer.algorithm.aco;

import java.util.ArrayList;
import java.util.Hashtable;

import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.Solution;

public interface ACOVariantInterface {

  public void init(String configFile, ConstructionGraph cons, Composer composer);
  public Solution findOneSolution();
  void updatePheromene(ArrayList<Solution> solutions, 
          Hashtable<Solution, Double> objectiveValues);
  public Solution getCurrentBestSolution();
  public void setBestSolution(Solution best);
  
}
