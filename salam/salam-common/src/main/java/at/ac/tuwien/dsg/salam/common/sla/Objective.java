package at.ac.tuwien.dsg.salam.common.sla;

import java.util.Comparator;

public class Objective {
    
    public enum Type {
        STATIC, METRIC, SKILL
    }

    protected String name;
    protected Object value;
    protected Type type;
    protected Comparator<Object> comparator;
    
    public Objective(String name, Object value, Type type) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.comparator = null;
    } 

    public Objective(String name, Object value, Type type, Comparator<Object> comparator) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.comparator = comparator;
    } 
    
    public boolean comply(Object obj) {
        return comparator.compare(obj, value)>=0;
    }

    public String getName() {
        return name;
    }
    
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "[(" + type + ")" + name + "=" + value + "]";
    }

    
}
