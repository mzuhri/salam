package at.ac.tuwien.dsg.salam.common.model;

import java.util.ArrayList;

public class HumanComputingElement extends ComputingElement {
    protected Skills skills;

    protected static final String DEFAULT_NAME_PREFIX = "HCE_";

    public HumanComputingElement(long id) {
        this(id, DEFAULT_NAME_PREFIX + id, new Properties(), new Metrics(), 
                new Skills(), new ArrayList<Connection>() );
    }

    public HumanComputingElement(long id, String name) {
        this(id, name, new Properties(), new Metrics(), 
                new Skills(), new ArrayList<Connection>());
    }

    public HumanComputingElement(long id, String name, Properties properties,
            Metrics metrics) {
        this(id, name, properties, metrics, new ArrayList<Connection>());
    }

    public HumanComputingElement(long id, String name, Properties properties, 
            Metrics metrics, ArrayList<Connection> connections) {
        this(id, name, properties, metrics, new Skills(), connections);
    }

    public HumanComputingElement(long id, String name, Properties properties, 
            Metrics metrics, Skills skills, ArrayList<Connection> connections) {
        super(id, name, properties, metrics, connections);
        this.type = HUMAN_COMPUTING_ELEMENT_TYPE;
        this.skills = skills;
    }

    public Skills getSkills() {
        return skills;
    }

    public void setSkills(Skills skills) {
        this.skills = skills;
    }
    
    public void setId(long id) {
        if (name.equals(DEFAULT_NAME_PREFIX + this.id)) setName(DEFAULT_NAME_PREFIX + id);
        this.id = id;
    }

    @Override
    public String detail() {
        return "E[id=" + id + ", name=" + name + ", type=" + type + 
                ", prop=" + properties + ", metrics=" + metrics + 
                ", skills=" + skills + ", services=" + servicesToString() + 
                ", connections=" + connectionsToString() + "]";
    }

    @Override
    public String toString() {
        return name;
    }
}
