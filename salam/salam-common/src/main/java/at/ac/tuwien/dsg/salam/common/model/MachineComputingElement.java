package at.ac.tuwien.dsg.salam.common.model;

import java.util.ArrayList;

public class MachineComputingElement extends ComputingElement {

    protected static final String DEFAULT_NAME_PREFIX = "HCE_";

    public MachineComputingElement(long id) {
        this(id, "MCE"+id, new Properties(), new Metrics(), new ArrayList<Connection>());
    }

    public MachineComputingElement(long id, String name) {
        this(id, name, new Properties(), new Metrics(), new ArrayList<Connection>());
    }

    public MachineComputingElement(long id, String name, Properties properties,
            Metrics metrics) {
        this(id, name, properties, metrics, new ArrayList<Connection>());
    }

    public MachineComputingElement(long id, String name, Properties properties,
            Metrics metrics, ArrayList<Connection> connections) {
        super(id, name, properties, metrics, connections);
        this.type = MACHINE_COMPUTING_ELEMENT_TYPE;
    }

    public void setId(long id) {
        if (name.equals(DEFAULT_NAME_PREFIX + this.id)) setName(DEFAULT_NAME_PREFIX + id);
        this.id = id;
    }
}
