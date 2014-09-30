package at.ac.tuwien.dsg.salam.composer.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.jgrapht.graph.Subgraph;

import at.ac.tuwien.dsg.salam.common.model.ComputingElement;
import at.ac.tuwien.dsg.salam.common.model.Connection;

public class ConnectednessGraph extends 
        ListenableUndirectedWeightedGraph<ComputingElement, RelationEdge> {

    private static final long serialVersionUID = 6477319124372736025L;

    Hashtable<Long, ComputingElement> computingElements;

    public ConnectednessGraph() {
        super(RelationEdge.class);
    }

    public ConnectednessGraph(Class<? extends RelationEdge> arg0) {
        super(arg0);
    }

    public Hashtable<Long, ComputingElement> getComputingElements() {
        return computingElements;
    }

    public void setComputingElements(
            Hashtable<Long, ComputingElement> computingElements) {
        this.computingElements = computingElements;
    }

    public void generate(ArrayList<Connection> connections) {

        setComputingElements(new Hashtable<Long, ComputingElement>());

        for (Connection c: connections) {

            ComputingElement e1;
            ComputingElement e2;

            // ComputingElement 1
            if (getComputingElements().containsKey(c.getComputingElement1().getId())) {
                e1 = getComputingElements().get(c.getComputingElement1().getId());
            } else {
                e1 = new ComputingElement(c.getComputingElement1().getId());
                getComputingElements().put(c.getComputingElement1().getId(), e1);
                addVertex(e1);
            }

            if (c.getComputingElement2()!=null) {

                // ComputingElement 2
                if (getComputingElements().containsKey(c.getComputingElement2().getId())) {
                    e2 = getComputingElements().get(c.getComputingElement2().getId());
                } else {
                    e2 = new ComputingElement(c.getComputingElement2().getId());
                    getComputingElements().put(c.getComputingElement2().getId(), e2);
                    addVertex(e2);
                }

                // define edge
                double weight = 0;
                RelationEdge edge = getEdge(e1, e2);
                if (edge==null) edge = addEdge(e1, e2);
                else weight = getEdgeWeight(edge);
                setEdgeWeight(edge, weight + c.getWeight()); // *ADD* if already exists

            }

            // else, only vertex w1 is added
        }
    }

    public Subgraph<ComputingElement, RelationEdge, ConnectednessGraph> 
            getSubgraphFromComputingElement(ArrayList<ComputingElement> computingElements) {

        // generate vertex set
        HashSet<ComputingElement> computingElementSet = new HashSet<ComputingElement>();
        for (ComputingElement e: computingElements) {
            if (getComputingElements().containsKey(e.getId())) {
                computingElementSet.add(getComputingElements().get(e.getId()));
            }
        }

        // return subgraph
        return new Subgraph<ComputingElement, RelationEdge, ConnectednessGraph>
                (this, computingElementSet);

    }

    public Subgraph<ComputingElement, RelationEdge, ConnectednessGraph> 
            getSubgraphFromSolution(Solution solution) {

        // generate vertex set
        HashSet<ComputingElement> ComputingElementSet = new HashSet<ComputingElement>();
        for (SolutionComponent s: solution.getList()) {
            long id = s.getAssignee().getProvider().getId();
            if (getComputingElements().containsKey(id)) {
                ComputingElementSet.add(getComputingElements().get(id));
            }
        }

        // return subgraph
        return new Subgraph<ComputingElement, RelationEdge, ConnectednessGraph>
                (this, ComputingElementSet);

    }

    public double _getConnectedness() {
        double sum = 0;
        Set<RelationEdge> edges = edgeSet();
        for (RelationEdge e: edges) {
            sum += getEdgeWeight(e);
        }
        return sum;
    }

    public double _getConnectedness(Subgraph<ComputingElement, RelationEdge, ConnectednessGraph> subgraph) {
        double sum = 0;
        Set<RelationEdge> edges = subgraph.edgeSet();
        for (RelationEdge e: edges) {
            sum += subgraph.getEdgeWeight(e);
        }
        return sum;
    }

    public double getConnectedness(Solution solution) {
        // get ComputingElement id set
        HashSet<Long> computingElementSet = new HashSet<Long>();
        for (SolutionComponent s: solution.getList()) {
            long id = s.getAssignee().getProvider().getId();
            if (getComputingElements().containsKey(id)) {
                computingElementSet.add(id);
            }
        }
        // iterate all edges
        double sum = 0;
        Set<RelationEdge> edges = edgeSet();
        for (RelationEdge e: edges) {
            if (computingElementSet.contains(getEdgeSource(e).getId()) && 
                    computingElementSet.contains(getEdgeTarget(e).getId())) {
                sum += getEdgeWeight(e);
            }      
        }
        int size = solution.size();
        int nEdgeComplete = size * (size -1) / 2;
        double result = 9999;
        if (nEdgeComplete!=0) result = sum / nEdgeComplete;
        return result;
    }

    public double getConnectedness(ArrayList<ComputingElement> ComputingElements) {
        // iterate all edges
        double sum = 0;
        Set<RelationEdge> edges = edgeSet();
        for (RelationEdge e: edges) {
            if (ComputingElements.contains(getEdgeSource(e)) && 
                    ComputingElements.contains(getEdgeTarget(e))) {
                sum += getEdgeWeight(e);
            }      
        }
        return sum / ComputingElements.size();

    }

    public double getDeltaConnectedness(Solution solution, ComputingElement computingElement) {
        // iterate all edges
        double sum = 0;
        for (SolutionComponent comp: solution.getList()) {
            RelationEdge edge = getEdge(computingElement, comp.getAssignee().getProvider());
            if (edge!=null) {
                sum += getEdgeWeight(edge);
            }
        }
        return sum / (solution.size()+1);
    }
}
