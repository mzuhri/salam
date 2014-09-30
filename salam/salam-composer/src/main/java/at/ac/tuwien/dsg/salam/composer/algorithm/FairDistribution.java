package at.ac.tuwien.dsg.salam.composer.algorithm;

import java.util.ArrayList;

import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.Solution;
import at.ac.tuwien.dsg.salam.composer.model.SolutionComponent;

public class FairDistribution implements ComposerAlgorithmInterface {

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
            int lowestAssignmentCount = 9999;
            SolutionComponent solutionComponent = null;
            for (SolutionComponent comp: components) {
                int assignmentCount = comp.getAssignee().getProvider().getAssignmentCount();
                if (lowestAssignmentCount > assignmentCount) {
                    lowestAssignmentCount = assignmentCount;
                    solutionComponent = comp;
                }
            }
            solution.addSolutionComponent(solutionComponent);
        }

        // measure objective values
        ArrayList<Solution> solutionList = new ArrayList<Solution>();
        solutionList.add(solution);
        composer.calculateObjectiveValues(solutionList);

        return solution;
    }

}
