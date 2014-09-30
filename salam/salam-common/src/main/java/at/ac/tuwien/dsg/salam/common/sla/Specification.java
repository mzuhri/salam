package at.ac.tuwien.dsg.salam.common.sla;

import java.util.ArrayList;

import at.ac.tuwien.dsg.salam.common.model.Properties;

public class Specification {

    protected ArrayList<Objective> slos;
    
    public Specification() {
        this.slos = new ArrayList<Objective>();
    }

    public ArrayList<Objective> getObjectives() {
        return slos;
    }

    public void addObjective(Objective objective) {
        slos.add(objective);
    }
    
    public Objective findObjective(String name) {
        Objective objective = null;
        for (Objective slo : slos) {
            if (slo.getName().equalsIgnoreCase(name)) {
                objective = slo;
                break;
            }
        }
        return objective;
    }
 
    // This is to model only full compliance, i.e., all required properties exist and comply
    // TODO: separate to a Spec Matcher class (using interface)
    public boolean comply(Properties properties) {
        boolean isComply = true;
        for (Objective objective : slos) {
            Object value = properties.getValue(objective.getName());
            if (value==null) {
                isComply = false;
                break;
            } else {
                if (!objective.comply(value)) {
                    isComply = false;
                    break;
                }
            }
        }
        return isComply;
    }

    @Override
    public String toString() {
        return slos.toString();
    }
    
    // NOTE: this is a shallow copy merge
    @SuppressWarnings("unchecked")
	public Specification merge(Specification spec) {
        Specification result = new Specification();
        result.slos = (ArrayList<Objective>) this.slos.clone();
        result.slos.addAll(spec.getObjectives());
        return result;
    }
    
}
