package at.ac.tuwien.dsg.salam.composer.algorithm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;

import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.Tracer;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.Solution;
import at.ac.tuwien.dsg.salam.composer.model.SolutionComponent;
import at.ac.tuwien.dsg.salam.util.Util;

public class GreedyLocalSearch implements ComposerAlgorithmInterface {

    private static final int SEED_SELECTOR = 2001;
    private String configFile;
    private UniformRealDistribution distSelector = null;
    private Tracer rawTracer = null;
    private ConstructionGraph cons;
    private Composer composer;

    @Override
    public void init(String configFile, ConstructionGraph cons, Composer composer) {
        this.configFile = configFile;
        this.cons = cons;
        this.composer = composer;
        this.distSelector = new UniformRealDistribution(new MersenneTwister(SEED_SELECTOR), 0, 1);

        String traceFilePrefix = Util.getProperty(configFile, "trace_file_prefix");
        if (traceFilePrefix!=null && !traceFilePrefix.equals("")) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date now = new Date();
            String date = sdfDate.format(now);      
            initTracer(traceFilePrefix, date);
        }

    }

    private void initTracer(String prefix, String currentId) {
        //rawTracer = new Tracer(prefix + "raw-" + currentId + ".csv");
        //rawTracer.traceln("#,flag,solution_components,objective_value,cost,norm_cost,competency,connnectedness,mu_connnectedness,response_time,norm_response_time");
    }

    private void closeTracer() {
        if (rawTracer!=null) rawTracer.close();
    }

    @Override
    public Solution solve() {

        // init params
        int maxCycle = Integer.parseInt(Util.getProperty(configFile, "maximum_number_of_cycles"));

        Solution solution = new Solution();

        // get first solution
        for (int i=1; i<cons.getComponentList().size()-1; i++) {
            ArrayList<SolutionComponent> components = cons.getComponentList().get(i);
            double visibility = 0.0;
            Hashtable<SolutionComponent, Double> scores = composer.calculateHeuristicScores(components, solution);
            SolutionComponent bestSolutionComponent = null;
            for (SolutionComponent comp: components) {
                if (visibility < scores.get(comp)) {
                    visibility = scores.get(comp);
                    bestSolutionComponent = comp;
                }
            }
            solution.addSolutionComponent(bestSolutionComponent);
        }

        // measure objective values
        double currentScore = composer.calculateObjectiveValue(solution);

        // trace first solution
        String prefix = "0,";
        if (composer.isSolutionFeasible(solution)) prefix += "f,";
        if (rawTracer!=null) rawTracer.traceln(solution, prefix);

        // refine with hill climbing
        int c = 1; 
        int nLayer = cons.getComponentList().size() - 2;
        while (c < maxCycle) {
            // change one component at a time
            int layerSize = 0;
            int chosenLayer = 0;
            if (nLayer>1) {
                int retry = 0;
                do {
                    chosenLayer = (int)Math.round(distSelector.sample() * (nLayer-1) );
                    chosenLayer = chosenLayer + 1;
                    layerSize = cons.getComponentList().get(chosenLayer).size();
                } while (layerSize<=1 && retry++<10);
            } else {
                chosenLayer = 1;
                layerSize = cons.getComponentList().get(chosenLayer).size();
            }
            if (layerSize>1) {
                int retry = 0;
                SolutionComponent changedComp = solution.getList().get(chosenLayer-1);
                SolutionComponent newComp = null;
                int chosenComp = 0;
                do {
                    chosenComp = (int)Math.round(distSelector.sample() * (layerSize-1) );
                    newComp = cons.getComponentList().get(chosenLayer).get(chosenComp);
                } while (changedComp.equals(newComp) && retry++<10);
                if (!changedComp.equals(newComp)) {
                    // change to the new comp, if it is better
                    Solution newSolution = solution.replace(chosenLayer, newComp);
                    double newScore = composer.calculateObjectiveValue(newSolution);
                    if (currentScore > newScore) {
                        currentScore = newScore;
                        solution = newSolution;
                    }
                }

            } else {
                // there are no choice
                break;
            }

            prefix = c + ",";
            if (composer.isSolutionFeasible(solution)) prefix += "f,";
            if (rawTracer!=null) rawTracer.traceln(solution, prefix);

            c++;
        }
        
        closeTracer();

        return solution;
    }

}
