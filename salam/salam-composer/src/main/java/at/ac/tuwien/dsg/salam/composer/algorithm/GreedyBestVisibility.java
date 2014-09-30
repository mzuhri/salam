package at.ac.tuwien.dsg.salam.composer.algorithm;

import java.util.ArrayList;
import java.util.Hashtable;

import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.Solution;
import at.ac.tuwien.dsg.salam.composer.model.SolutionComponent;

public class GreedyBestVisibility implements ComposerAlgorithmInterface {

    private ConstructionGraph cons;
    private Composer composer;

    @Override
    public void init(String configFile, ConstructionGraph cons, Composer composer) {
        this.cons = cons;
        this.composer = composer;

    }

    @Override
    public Solution solve() {
        Solution solution = new Solution();
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
        ArrayList<Solution> solutionList = new ArrayList<Solution>();
        solutionList.add(solution);
        composer.calculateObjectiveValues(solutionList);

        return solution;
    }

}
