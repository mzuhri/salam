package at.ac.tuwien.dsg.salam.cloud.scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import at.ac.tuwien.dsg.salam.common.interfaces.DependencyProcessorInterface;
import at.ac.tuwien.dsg.salam.common.model.Assignment;
import at.ac.tuwien.dsg.salam.common.model.Role;
import at.ac.tuwien.dsg.salam.util.Util;

/**
 * NOTE, currently
 * - we support only one role = one dependency point
 * - we support only one role = one assignment, cardinality can be obtained by cloning roles
 */

public class DependencyProcessor implements DependencyProcessorInterface {
    
    public DependencyProcessor() {
    }

    @Override
    public boolean verify(List<Assignment> assignments) {
        ListenableDirectedWeightedGraph<Assignment, DefaultWeightedEdge> graph = generateGraph(assignments);
        if (graph==null) return false;
        return !isCyclic(assignments);
    }

    // partial ordering of assignments
    // if A <-- ... <-- B then A < B
    @Override
    public List<Assignment> sort(List<Assignment> assignments) {
        List<Assignment> result = new LinkedList<Assignment>();
        for (Assignment a: assignments) {
            // for each assignment, find its last dependency in current list
            int lastDependecyPosition = -1;
            int curPosition = -1;
            for (Assignment prev: result) {
                curPosition++;
                if (isDependant(a.getRole(), prev.getRole())) {
                    lastDependecyPosition = curPosition;
                }
            }
            // insert after the last dependency
            result.add(lastDependecyPosition+1, a);
        }
        return result;
    }
    
    @Override
    public boolean isDependant(Role dependant, Role dependency) {
        // use recursive DFS
        List<Role> dependencies = dependant.getStrongDependencies();
        // NOTE: weak dependants do not have to wait
        if (dependencies.contains(dependency)) {
            return true;
        } else {
            // recurse
            for (Role r: dependencies) {
                if (isDependant(r, dependency)) return true;
            }
            return false;
        }
    }
    
    public double forecastEarliestStartTime(Assignment assignment) {
        // NOTE, for this function to work:
        // - forecasted duration must be specified for each assignments 
        // - all startTime must be set to 0
        
        // No need to calculate again if have calculated before
        if (assignment.getStartTime() > 0) return assignment.getStartTime(); 
        
        // initially make t0 equals to task submission time
        double t0 = assignment.getTask().getSubmissionTime();
        // request availability metric
        double result = (double)assignment.getAssignee().getMetric("earliest_availability", 
                new Object[]{t0, assignment.getForecastedDuration()});
        if (result > t0) t0 = result; 
        
        return t0;
    }
    
    private double recPorecastStartTimeForAllAssignment(List<Assignment> assignments, Assignment assignment) {

        double t0 = forecastEarliestStartTime(assignment);
        
        // if it has dependencies, forecast dependency finish time and use it as t0
        // NOTE: weak dependants do not have to wait
        for (Role r: assignment.getRole().getStrongDependencies()) {
            Assignment a = findAssignmetByRole(assignments, r);
            if (a!=null) {
                // recursively calculate dependency finish time
                double startTime = recPorecastStartTimeForAllAssignment(assignments, a);
                double dependencyFinishTime = startTime
                        + a.getForecastedDuration() + 1;
                if (dependencyFinishTime > t0) t0 = dependencyFinishTime;
            }
        }

        // for weak dependencies, just recursively calculate, no need to update current t0
        for (Role r: assignment.getRole().getWeakDependencies()) {
            Assignment a = findAssignmetByRole(assignments, r);
            if (a!=null) {
                // recursively calculate dependency start time
                recPorecastStartTimeForAllAssignment(assignments, a);
            }            
        }
        
        assignment.setStartTime(t0);
        return t0;
    }
    
    @Override
    public void forecastStartTimeForAllAssignment(List<Assignment> assignments) {

        // initialized all startTime to 0
        for (Assignment a: assignments) {
            a.setStartTime(0);
        }
        
        List<Assignment> finals = findFinalAssignments(assignments);
        for (Assignment a: finals) {
            recPorecastStartTimeForAllAssignment(assignments, a);
        }
        
    }

    @Override
    public double forecastResponseTime(List<Assignment> assignments, Assignment assignment) {
        // Forecasted duration must be specified for each assignments for this function to work
        
        List<Role> dependencies = assignment.getRole().getStrongDependencies();
        // NOTE: weak dependants do not have to wait
        
        // initially make t0 equals to task submission time
        double t0 = assignment.getTask().getSubmissionTime();
        
        // if it has dependencies, forecast dependency finish time and use it as t0
        for (Role r: dependencies) {
            Assignment a = findAssignmetByRole(assignments, r);
            if (a!=null) {
                // recursively calculate dependency finish time
                double dependencyFinishTime = forecastResponseTime(assignments, a);
                if (dependencyFinishTime > t0) t0 = dependencyFinishTime;
            }
        }
        
        if (assignment.getAssignee()==null) {
            Util.log().severe("Unable to forecast response time, assignee is null: " + assignment);
            return 0;
        }
        
        double result = assignment.getForecastedDuration();
        if (assignment.getAssignee().getMetrics().has("response_time")) {
        	result = (double)assignment.getAssignee().getMetric("response_time", 
                new Object[]{t0, 0.0, result});
        }
        
        return result;
    }

    @Override
    public double forecastFinishTime(List<Assignment> assignments) {
        double finishTime = 0;
        List<Assignment> finals = findFinalAssignments(assignments);
        for (Assignment a: finals) {
            double forecastResponseTime = forecastResponseTime(assignments, a); 
            if (forecastResponseTime > finishTime) {
                finishTime = forecastResponseTime;
            }
        }
        return finishTime;
    }

    // NOW we support only one role = one assignment
    @Override
    public Assignment findAssignmetByRole(List<Assignment> assignments, Role role) {
        Assignment result = null;
        for (Assignment a: assignments) {
            if (a.getRole()==role) {
                result = a;
                break;
            }
        }
        return result;
    }
    
    @Override
    public Assignment findAssignmetByFunctionality(List<Assignment> assignments, String func) {
        Assignment result = null;
        for (Assignment a: assignments) {
            if (a.getRole().getFunctionality().getName().equalsIgnoreCase(func)) {
                result = a;
                break;
            }
        }
        if (result==null) {
            // this should not happen
            Util.log().warning("Cannot find assignment for " + func);
        }
        return result;
    }

    // NOTE: the status of assignment must be set to SUCCESSFUL first
    @Override
    public List<Assignment> startAfter(List<Assignment> assignments, Assignment assignment) {
        
        List<Assignment> dependants = new ArrayList<Assignment>();
        List<Assignment> result = new ArrayList<Assignment>();

        // find dependants
        for (Assignment a: assignments) {
            List<Role> dependencies = a.getRole().getStrongDependencies();
            if (dependencies.contains(assignment.getRole())) {
                dependants.add(a);
            }
        }
        
        // for each dependant, check if their other strong dependencies finished succesfully
        for (Assignment a: dependants) {
            boolean isFinished = true;
            List<Role> dependencies = a.getRole().getStrongDependencies();
            for (Role r: dependencies) {
                Assignment dependency = findAssignmetByRole(assignments, r);
                if (dependency.getStatus()!=Assignment.Status.SUCCESSFUL) isFinished = false;
            }
            if (isFinished) result.add(a);
        }
        
        return result;
    }
    
    // TODO: need further design how to finalize a weak dependants
    @Override
    public List<Assignment> finishAfter(List<Assignment> assignments, Role role) {
        List<Assignment> result = new ArrayList<Assignment>();
        for (Assignment a: assignments) {
            List<Role> dependencies = a.getRole().getWeakDependencies();
            if (dependencies.contains(role)) {
                result.add(a);
            }
        }
        return result;
    }
    
    private boolean recIsCyclic(Role role, Deque<Role> stack) {
        
        // push in this vertex to stack
        stack.push(role);

        // merge strong and week dependencies
        List<Role> dependencies = role.getMergedDependencies();
        
        for (Role neighbor: dependencies) {
            if (stack.contains(neighbor)) return true;
            else if (recIsCyclic(neighbor, stack)) return true;
        }
        
        // no cycle detected on this vertex
        stack.pop();
        return false;
        
    }
    
    public boolean isCyclic(List<Assignment> assignments) {

        // get final vertices
        List<Assignment> finals = findFinalAssignments(assignments);
        
        // if we don't have final vertices, that means we have cycle
        if (finals.size()==0) return true;
        
        // init stack trace
        Deque<Role> stack = new ArrayDeque<Role>();
        // search a cycle using DFS starting from the final vertices
        for (Assignment a: finals) {
            if (recIsCyclic(a.getRole(), stack)) return true;
        }
        
        return false;
    }

    // find ROOT element
    @Override
    public List<Assignment> findNonDependantAssignments(List<Assignment> assignments) {
        List<Assignment> result = new ArrayList<Assignment>();
        for (Assignment a: assignments) {
            if (a.getRole().getMergedDependencies().size()==0) {
                result.add(a);
            }
        }
        return result;
    } 
    
    // find FINAL/LEAF elements
    @Override
    public List<Assignment> findFinalAssignments(List<Assignment> assignments) {
        List<Assignment> result = new ArrayList<Assignment>();
        for (Assignment a: assignments) {
            boolean isLeaf = true;
            for (Assignment b: assignments) {
                if (b.getRole().getMergedDependencies().contains(a.getRole())) {
                    // b depends on a, don't add a
                    isLeaf = false;
                    break;
                }
            }
            if (isLeaf) {
                result.add(a);
            }
        }
        return result;
    } 
    
    @Override
    public ListenableDirectedWeightedGraph<Assignment, DefaultWeightedEdge> generateGraph(
            List<Assignment> assignments) {
        
        ListenableDirectedWeightedGraph<Assignment, DefaultWeightedEdge> result = 
                new ListenableDirectedWeightedGraph<Assignment, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        // add vertices
        for (Assignment a: assignments) {
            result.addVertex(a);
        }
        
        // add edges
        // strong dependencies, weight = 1
        // weak dependencies, weight = 0
        for (Assignment a: assignments) {
            // strong dependencies
            for (Role r: a.getRole().getStrongDependencies()) {
                Assignment b = findAssignmetByRole(assignments, r);
                if (b!=null) {
                    DefaultWeightedEdge newEdge = result.addEdge(a, b);
                    if (newEdge==null) {
                        Util.log().severe("Unable to create strong dependency from " + a.getRole() + " to " + r );
                        return null;
                    }
                    result.setEdgeWeight(newEdge, 1);
                }
            }
            // weak dependencies
            for (Role r: a.getRole().getWeakDependencies()) {
                Assignment b = findAssignmetByRole(assignments, r);
                if (b!=null) {
                    DefaultWeightedEdge newEdge = result.addEdge(a, b);
                    if (newEdge==null) {
                        Util.log().severe("Unable to create weak dependency from " + a.getRole() + " to " + r );
                        return null;
                    }
                    result.setEdgeWeight(newEdge, 0);
                }
            }
        }
        
        return result;
    }

}
