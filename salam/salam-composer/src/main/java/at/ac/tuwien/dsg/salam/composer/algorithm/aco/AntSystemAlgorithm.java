package at.ac.tuwien.dsg.salam.composer.algorithm.aco;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;

import at.ac.tuwien.dsg.salam.common.model.Service;
import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.RelationEdge;
import at.ac.tuwien.dsg.salam.composer.model.Solution;
import at.ac.tuwien.dsg.salam.composer.model.SolutionComponent;
import at.ac.tuwien.dsg.salam.util.Util;

public class AntSystemAlgorithm implements ACOVariantInterface {

    protected static final int SEED_SELECTOR = 2001;
    protected UniformRealDistribution distSelector = null;
    protected ConstructionGraph constructionGraph;
    protected Composer composer;

    protected Solution currentBestSolution;
    protected double currentBestScore = 9999;

    // heuristic factors weight
    protected double connectednessWeight;
    protected double costWeight;

    protected double pheromoneWeight;
    protected double heuristicWeight;
    protected double evaporation;

    private double multiplier;

    @Override
    public void init(String configFile, ConstructionGraph cons, Composer composer) {
        this.constructionGraph = cons;
        this.composer = composer;
        this.distSelector = new UniformRealDistribution(new MersenneTwister(SEED_SELECTOR), 0, 1);

        // init param
        connectednessWeight = Double.parseDouble(Util.getProperty(configFile, "connectedness_weight"));
        costWeight = Double.parseDouble(Util.getProperty(configFile, "cost_weight"));
        pheromoneWeight = Double.parseDouble(Util.getProperty(configFile, "pheromone_weight"));
        heuristicWeight = Double.parseDouble(Util.getProperty(configFile, "heuristic_weight"));
        evaporation = Double.parseDouble(Util.getProperty(configFile, "pheromone_evaporation"));
        multiplier = Double.parseDouble(Util.getProperty(configFile, "objective_multiplier"));

    }

    @Override
    public Solution findOneSolution() {

        // init
        Hashtable<Long, Service> selectedServices = new Hashtable<Long, Service>();
        Solution solution = new Solution();
        SolutionComponent current = constructionGraph.getInitialComponent();

        // loop till we get the final comp
        boolean stop = false;
        while (!stop && current!=constructionGraph.getFinalComponent()) {

            // get options on this level
            Set<RelationEdge> edges = constructionGraph.getGraph().outgoingEdgesOf(current);

            // init to evaluate all options 
            ArrayList<SolutionComponent> options = new ArrayList<SolutionComponent>();

            // iterate to obtain allowed options, i.e., workers that are not yet selected 
            for (RelationEdge edge: edges) {
                SolutionComponent option = constructionGraph.getGraph().getEdgeTarget(edge);
                // we don't allow same worker selected twice
                if (option.getAssignee()!=null && 
                        !selectedServices.containsKey(option.getAssignee().getProvider().getId())) {
                    // one possible option
                    options.add(option);
                }
            }

            if (options.size()>0) {

                // measure heuristic factors
                Hashtable<SolutionComponent,Double> heuristicScores = composer.calculateHeuristicScores(options, solution);
                //System.out.print("heuscore: ");
                //System.out.println(heuristicScores);

                // select one feasible option
                current = selectComponent(options, heuristicScores);      
                solution.getList().add(current);
                selectedServices.put(current.getAssignee().getProvider().getId(), 
                        current.getAssignee());

            } else {
                // no option available, can be a final component or the construction graph is not well constructed
                stop = true;
            }

        }

        return solution;
    }

    protected SolutionComponent selectComponent(ArrayList<SolutionComponent> options, Hashtable<SolutionComponent,Double> heuristicScores) {

        // measure score (based on pheromone & heuristic factor)
        int i = 0;
        double totalProbability = 0;
        ArrayList<Double> probabilities = new ArrayList<Double>();

        while (i<options.size()) {

            // get heuristc score
            double heuristicScore = heuristicScores.get(options.get(i));

            // get pheromone
            double pheromone = options.get(i).getPheromone(); 

            // calculate probability, we dont have to normalize to 1 here
            double probability = Math.pow(pheromone, pheromoneWeight) * Math.pow(heuristicScore, heuristicWeight);

            totalProbability += probability; 
            probabilities.add(probability); 
            i++;
        }

        // randomize
        double val = distSelector.sample() * totalProbability;
        double sum = 0;
        int count = 0;
        while (sum<val && count<probabilities.size()) {
            sum += probabilities.get(count);
            if (sum<val) count++;
        }

        return options.get(count);
    }

    @Override
    public void updatePheromene(ArrayList<Solution> solutions, Hashtable<Solution, Double> objectiveValues) {

        if (solutions.size()==0) return;

        evaporatePheromones();

        // update for each solution
        for (Solution solution: solutions) {

            /*
      // save iteration best score
      if (currentBestScore>solution.getAggregateScore()) {
        currentBestScore = solution.getAggregateScore();
        currentBestSolution = solution;
      }
             */      
            // update pheromone for each edge used in every solution
            double objectivityCost = objectiveValues.get(solution);  
            for (SolutionComponent comp: solution.getList()) {
                double pheromone = comp.getPheromone();
                pheromone += multiplier / objectivityCost;
                comp.setPheromone(pheromone);
                constructionGraph.updateMinMaxPheromone(pheromone);
            }

        }

    }

    @Override
    public Solution getCurrentBestSolution() {
        return currentBestSolution;
    }

    protected void evaporatePheromones() {
        // evaporate current pheromone
        for (SolutionComponent comp: constructionGraph.getGraph().vertexSet()) {
            double pheromone = comp.getPheromone();
            pheromone = (1-evaporation) * pheromone;
            comp.setPheromone(pheromone);
            constructionGraph.updateMinMaxPheromone(pheromone);
        }
    }

    public void setBestSolution(Solution best) {
        this.currentBestSolution = best;
        this.currentBestScore = best.getAggregateScore();
    }
}
