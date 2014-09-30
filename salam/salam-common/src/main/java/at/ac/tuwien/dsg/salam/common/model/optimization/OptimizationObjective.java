package at.ac.tuwien.dsg.salam.common.model.optimization;

import java.util.Hashtable;

public class OptimizationObjective { 

    private Hashtable<String, Double> weights;

    public OptimizationObjective() {
        weights = new Hashtable<String, Double>();
    }

    public double getWeight(String name) {
        Double weight = weights.get(name);
        if (weight==null) weight = new Double(0.0);
        return weight.doubleValue();
    }
    
    public OptimizationObjective setWeight(String name, double weight) {
        weights.put(name, new Double(weight));
        return this;
    }

    @Override
    public String toString() {
        return weights.toString();
    }

    

}
