package at.ac.tuwien.dsg.salam.common.model;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.salam.common.sla.Specification;

public class Role implements Comparable<Role> {

    protected Functionality functionality;
    protected Specification specification;
    
    protected double load;
    
    // dependendency
    // TODO: 1-to-n dependency, i.e., one role can have multiple dependency points
    //       for now, 1 role = 1 dependency point, 1 dependency point depends on multiple other points
    protected List<Role> strongDependencies;
    protected List<Role> weakDependencies;
    
    public Role(Functionality functionality) {
        this(functionality, null);
    }    

    public Role(Functionality functionality, Specification specification) {
        this.functionality = functionality;
        this.specification = specification;
        this.strongDependencies = new ArrayList<Role>();
        this.weakDependencies = new ArrayList<Role>();
    }

    public Functionality getFunctionality() {
        return functionality;
    }

    public void setFunctionality(Functionality functionality) {
        this.functionality = functionality;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }


    public List<Role> getStrongDependencies() {
        return strongDependencies;
    }

    public void addStrongDependency(Role role) {
        this.strongDependencies.add(role);
    }

    public List<Role> getWeakDependencies() {
        return weakDependencies;
    }
    
    public List<Role> getMergedDependencies() {
        List<Role> merged = new ArrayList<Role>();
        merged.addAll(strongDependencies);
        merged.addAll(weakDependencies);
        return merged;
    }

    public void addWeakDependency(Role role) {
        this.weakDependencies.add(role);
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((functionality == null) ? 0 : functionality.hashCode());
        return result;
    }

    // Two roles with the same functionality are considered equal
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Role other = (Role) obj;
        if (functionality == null) {
            if (other.functionality != null)
                return false;
        } else if (!functionality.equals(other.functionality))
            return false;
        return true;
    }

    @Override
    public String toString() {
        //return "[func=" + functionality + ", spec=" + specification + "]";
        return functionality.toString();
    }

    public int compareTo(Role r) {
        Role other = r;
        return functionality.getName().compareTo(other.getFunctionality().getName());
    }
    
    
    
}
