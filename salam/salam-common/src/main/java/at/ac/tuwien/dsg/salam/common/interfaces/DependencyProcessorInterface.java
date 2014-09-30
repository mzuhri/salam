package at.ac.tuwien.dsg.salam.common.interfaces;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import at.ac.tuwien.dsg.salam.common.model.Assignment;
import at.ac.tuwien.dsg.salam.common.model.Role;

public interface DependencyProcessorInterface {

    public boolean verify(List<Assignment> assignments);
    public List<Assignment> sort(List<Assignment> assignments);

    public Assignment findAssignmetByRole(List<Assignment> assignments, Role role);
    public Assignment findAssignmetByFunctionality(List<Assignment> assignments, String func);
    public List<Assignment> findNonDependantAssignments(List<Assignment> assignments);
    public List<Assignment> findFinalAssignments(List<Assignment> assignments);

    public boolean isDependant(Role dependant, Role dependency);
    
    public double forecastEarliestStartTime(Assignment assignment);
    public void forecastStartTimeForAllAssignment(List<Assignment> assignments);    
    public double forecastResponseTime(List<Assignment> assignments, Assignment assignment);
    public double forecastFinishTime(List<Assignment> assignments);
    public List<Assignment> startAfter(List<Assignment> assignments, Assignment assignment);
    // TODO: need further design how to finalize a weak dependants
    public List<Assignment> finishAfter(List<Assignment> assignments, Role role);

    public ListenableDirectedWeightedGraph<Assignment, DefaultWeightedEdge> generateGraph(
            List<Assignment> assignments);
}
