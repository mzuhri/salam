package at.ac.tuwien.dsg.salam.composer.algorithm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.Summary;
import at.ac.tuwien.dsg.salam.composer.Tracer;
import at.ac.tuwien.dsg.salam.composer.algorithm.aco.ACOVariantInterface;
import at.ac.tuwien.dsg.salam.composer.algorithm.aco.CrossSummary;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.Solution;
import at.ac.tuwien.dsg.salam.composer.model.SolutionComponent;
import at.ac.tuwien.dsg.salam.util.Util;

public class ACOAlgorithm implements ComposerAlgorithmInterface {

    private ConstructionGraph cons;
    private Composer composer;
    private ACOVariantInterface acoVariant;
    private String configFile;

    private Tracer rawTracer = null;
    private Tracer summaryTracer = null;
    Summary summary = new Summary();

    int crossSummaryLimit = 0;
    double epsilonPheromone = 0.0; 

    public ACOAlgorithm() {
    }

    public void init(String configFile, ConstructionGraph cons, Composer composer) {

        // init algo
        this.configFile = configFile;
        String acoVariantName = Util.getProperty(configFile, "aco_variant");
        System.out.println("ACO Variant: " + acoVariantName + ".");
        try {
            acoVariant = (ACOVariantInterface)Class.forName(
                    "scu.composer.algorithm.aco." + acoVariantName).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        acoVariant.init(configFile, cons, composer);

        this.cons = cons;
        this.composer = composer;

        String traceFilePrefix = Util.getProperty(configFile, "trace_file_prefix");
        if (traceFilePrefix!=null && !traceFilePrefix.equals("")) {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
            Date now = new Date();
            String date = sdfDate.format(now);      
            initTracer(traceFilePrefix, date);
        }

        crossSummaryLimit = Integer.parseInt(Util.getProperty(configFile, "cross_summary_limit"));
        epsilonPheromone = Double.parseDouble(Util.getProperty(configFile, "epsilon_pheromone"));    
    }

    private void initTracer(String prefix, String currentId) {
        // enabling the following lines may cause a big size raw data, and may slow down the simulation
        //rawTracer = new Tracer(prefix + "raw-" + currentId + ".csv");
        //rawTracer.traceln("flag,solution_components,objective_value,cost,norm_cost,competency,connnectedness,mu_connnectedness,response_time,norm_response_time");

        //summaryTracer = new Tracer(prefix + "summary-" + currentId + ".csv");
        //summaryTracer.traceln("iteration,average_branching,branching_pct,min_pheromone,max_pheromone,best_objective_value," + Summary.header());
    }

    private void closeTracer() {
        if (rawTracer!=null) rawTracer.close();
        if (summaryTracer!=null) summaryTracer.close();
    }

    public Solution solve() {

        // init params
        int maxCycle = Integer.parseInt(Util.getProperty(configFile, "maximum_number_of_cycles"));
        int stagnantLimit = Integer.parseInt(Util.getProperty(configFile, "stagnant_limit"));
        //int nAnts = cons.getMaxNumberOfWorker();
        int nAnts = Integer.parseInt(Util.getProperty(configFile, "number_of_ants"));

        // init pheromone
        // TODO: initial pheromone = 1/|E| ?
        double initialPheromone = Double.parseDouble(Util.getProperty(configFile, "initial_pheromone"));
        for (SolutionComponent comp: cons.getGraph().vertexSet()) {
            comp.setPheromone(initialPheromone);
        }

        // init vars
        int c = 0;
        ArrayList<Solution> antSolutions = null;
        ArrayList<Solution> feasibleSolutions = null;
        Hashtable<Solution, Double> objectiveValues = null;
        Solution currentBestSolution = null;
        double currentBestScore = 9999;
        Solution perfectSolution = null;
        int stagnantCounter = 0;
        boolean isAllSame = false;

        double initialBranching = cons.getAverageNodeBranching(); 
        if (summaryTracer!=null) summaryTracer.traceln(
                "initial_brancing," + initialBranching + 
                ",layer_size," + cons.getLayersSize() + 
                ",space_size," + cons.getSpaceSize() + 
                ",task," + composer.getTask());

        while (c<maxCycle && perfectSolution==null && stagnantCounter<stagnantLimit && !isAllSame) {
            //while (c<maxCycle && perfectSolution==null) {
            //while (c<maxCycle) {

            summary = new Summary();

            // obtain solution for each ants
            antSolutions = new ArrayList<Solution>();
            for (int a=0; a<nAnts; a++) {
                //System.out.println("Finding solution for ant #" + a);
                Solution oneSolution = acoVariant.findOneSolution();
                antSolutions.add(oneSolution);
            }

            // calculate objective values and get feasible solutions
            objectiveValues = composer.calculateObjectiveValues(antSolutions);
            Solution best = findBestSolution(antSolutions);
            feasibleSolutions = composer.filterFeasibleSolutions(antSolutions);

            // update pheromone
            //System.out.print("objvalue: ");
            //System.out.println(objectiveValues);
            acoVariant.setBestSolution(best);
            acoVariant.updatePheromene(feasibleSolutions, objectiveValues);

            // check best solution and stagnancy
            // count stagnant counter
            if (currentBestSolution==null || !currentBestSolution.equals(best)) {
                if (currentBestScore>best.getAggregateScore()) {
                    currentBestSolution = best;
                    currentBestScore = best.getAggregateScore();
                    stagnantCounter = 0;
                }
            } else {
                stagnantCounter++;
            }

            // check staganancy when ants agree on one solution
            if (stagnantCounter>=stagnantLimit) {
                System.out.println("\nStagnant after " + stagnantCounter + " iterations!");          
            } else {
                // check stagnant solutions (all ants get the same solutions)
                if (feasibleSolutions.size()==antSolutions.size()) {
                    isAllSame = true;
                    for (int i=0; i<feasibleSolutions.size(); i++) {
                        Solution check = feasibleSolutions.get(i);
                        for (int j=i+1; j<feasibleSolutions.size(); j++) {
                            if (!check.equals(feasibleSolutions.get(j))) {
                                isAllSame = false;
                                break;
                            }
                        }
                        if (!isAllSame) break;
                    }
                }
                if (isAllSame) {
                    System.out.println("\nStagnant, ants agree on one solution!");                      
                }
            }

            // check for perfect solution, i.e., objective score = 0 --> pheromone = Infinity
            for (Solution solution: feasibleSolutions) {
                if (objectiveValues.get(solution)==0.0) {
                    System.out.println("\nPerfect solution found!");
                    perfectSolution = solution;
                }
            }

            // TRACE
            if (rawTracer!=null) traceSolutions(c, antSolutions, feasibleSolutions, currentBestSolution, perfectSolution);
            for (Solution solution: antSolutions) {
                summary.add(solution);
            }
            double bestScore = 0.0;
            if (currentBestSolution!=null) bestScore = currentBestSolution.getAggregateScore();  
            double currentBranching = cons.getAverageNodeBranching(epsilonPheromone);
            double minPheromone = cons.getMinPheromone();
            double maxPheromone = cons.getMaxPheromone();
            if (summaryTracer!=null) summaryTracer.traceln(c + "," + currentBranching + "," + (currentBranching/initialBranching) + "," + minPheromone + "," + maxPheromone + "," + bestScore + "," + summary.getSummary());

            // cross summary
            CrossSummary.add(c, bestScore, summary.getObjevtiveAverage(), summary.getObjevtiveSD(), minPheromone, maxPheromone);

            // PRINT RESULT
            //printSolutions(c, antSolutions, feasibleSolutions, currentBestSolution, perfectSolution);

            c++;
        }

        closeTracer();

        if (CrossSummary.size()==crossSummaryLimit) {
            CrossSummary.trace();
        }

        // return result
        if (feasibleSolutions.size()==0) {
            System.out.println("No feasible solution found!");
            return new Solution();
        } else if (perfectSolution!=null) {
            perfectSolution.setData("*" + c);
            return perfectSolution;
        } else {
            currentBestSolution.setData("" + c);
            return currentBestSolution;
        }

    }

    public void traceSolutions(int iterationNum, ArrayList<Solution> antSolutions, ArrayList<Solution> feasibleSolutions, Solution currentBestSolution, Solution perfectSolution) {
        if (rawTracer!=null) {
            rawTracer.traceln("#" + iterationNum);
            for (Solution solution: antSolutions) {
                String prefix = "";
                if (feasibleSolutions.contains(solution)) prefix += "f";
                else prefix += "n";
                if (solution.equals(currentBestSolution)) prefix += "b";
                if (solution.equals(perfectSolution)) prefix += "p";
                rawTracer.traceln(solution, prefix + ",");
            }
        }

    }


    public void printSolutions(int iterationNum, ArrayList<Solution> antSolutions, ArrayList<Solution> feasibleSolutions, Solution currentBestSolution, Solution perfectSolution) {
        System.out.println("\nIteration #" + iterationNum);
        int a = 0;
        for (Solution solution: antSolutions) {
            String prefix = "";
            if (feasibleSolutions.contains(solution)) prefix += "f";
            else prefix += "n";
            if (solution.equals(currentBestSolution)) prefix += "b";
            if (solution.equals(perfectSolution)) prefix += "p";
            System.out.print("Ant #" + (a++) + "(" + prefix + "): ");
            System.out.println(solution);
        }

    }

    public Solution findBestSolution(ArrayList<Solution> solutions) {
        double bestScore = 9999;
        Solution best = null;
        for (Solution s: solutions) {
            if (bestScore>s.getAggregateScore()) {
                bestScore = s.getAggregateScore();
                best = s;
            }
        }
        return best;
    }


}
