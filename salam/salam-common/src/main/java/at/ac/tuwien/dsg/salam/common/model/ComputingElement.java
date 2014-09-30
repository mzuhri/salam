package at.ac.tuwien.dsg.salam.common.model;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.salam.common.interfaces.ServiceManagerInterface;

public class ComputingElement {

    protected long id;
    protected String name;
    protected int type;
    protected String description;
    protected int status;
    protected Properties properties;
    protected Metrics metrics;
    protected List<Connection> connections;
    protected List<Service> services;
    protected ServiceManagerInterface manager;
    protected int assignmentCount = 0;
    
    protected static final String DEFAULT_NAME_PREFIX = "CE_";

    public static final int UNKNOWN_COMPUTING_ELEMENT_TYPE = 0;
    public static final int HUMAN_COMPUTING_ELEMENT_TYPE = 1;
    public static final int MACHINE_COMPUTING_ELEMENT_TYPE = 2;

    public ComputingElement(long id) {
        this(id, DEFAULT_NAME_PREFIX + id, new Properties(), new Metrics(),
                new ArrayList<Connection>());
    }

    public ComputingElement(long id, String name) {
        this(id, name, new Properties(), new Metrics(), new ArrayList<Connection>());
    }

    public ComputingElement(long id, String name, Properties properties,
            Metrics metrics) {
        this(id, name, properties, metrics, new ArrayList<Connection>());
    }

    public ComputingElement(long id, String name, Properties properties,
            Metrics metrics, ArrayList<Connection> connections) {
        this.type = UNKNOWN_COMPUTING_ELEMENT_TYPE;
        this.id = id;
        this.name = name;
        this.properties = properties;
        this.metrics = metrics;
        this.metrics.setOwner(this);
        this.connections = connections;
        this.services = new ArrayList<Service>();
    }

    public int getType() {
        return type;
    }

    public ServiceManagerInterface getManager() {
        return manager;
    }

    public void setManager(ServiceManagerInterface manager) {
        this.manager = manager;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (name.equals(DEFAULT_NAME_PREFIX + this.id)) setName(DEFAULT_NAME_PREFIX + id);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
        autoUpdate();
    }
    
    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Connection> connections) {
        // TODO: verify if the connection is for this element
        this.connections = connections;
        autoUpdate();
    }
    
    public void setConnection(ComputingElement other, double weight) {
        Connection conn = getConnection(other);
        if (conn==null) {
            conn = new Connection(this, other, weight);
            connections.add(conn);
        } else {
            conn.setWeight(weight);
        }
        autoUpdate();
    }

    public Connection getConnection(ComputingElement other) {
        Connection conn = null;
        for (Connection c : connections) {
            if ((c.getComputingElement1()==this && c.getComputingElement2()==other) || 
                (c.getComputingElement2()==this && c.getComputingElement1()==other)) {
                conn = c;
                break;
            }
        }
        return conn;
    }

    protected void autoUpdate() {
        if (manager!=null) {
            manager.registerElement(this);
        }        
    }

    public String connectionsToString() {
        String sconn = "";
        for (Connection c: connections) {
            ComputingElement other = c.getOther(this);
            if (!sconn.equals("")) sconn += ",";
            sconn += other.getId() + ":" + c.getWeight(); 
        }
        return "[" + sconn + "]";
    }
    
    public String servicesToString() {
        String svc = "";
        for (Service s : services) {
            if (!svc.equals("")) svc += ",";
            svc += s.getFunctionality();
        }
        return "[" + svc + "]";
    }
    
    public String detail() {
        return "E[id=" + id + ", type=" + type + ", prop=" + properties + 
                ", metrics=" + metrics + ", services=" + servicesToString() + 
                ", connections=" + connectionsToString() + "]";
    }
    @Override
    public String toString() {
        //return name;
        return name + servicesToString();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComputingElement other = (ComputingElement) obj;
        if (id != other.getId())
            return false;
        return true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        autoUpdate();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        autoUpdate();
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
        autoUpdate();
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
        autoUpdate();
    }
    
    public Service getService(Functionality functionality) {
        Service service = null;
        int index = services.indexOf(new Service(functionality, this));
        if (index>-1) service = services.get(index);
        return service;
    }

    public void addService(Service service) {
        int index = services.indexOf(service);
        if (index > -1) {
            // same functionality exists, replace
            this.services.set(index, service);
        } else {
            // add
            this.services.add(service);
        }
        autoUpdate();
    }

    public void removeService(Functionality functionality) {
        this.services.remove(new Service(functionality, this));
        autoUpdate();
    }

    public void updateService(Service service) {
        this.removeService(service.getFunctionality());
        this.addService(service);
        autoUpdate();
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public void addAssignmentCount() {
        this.assignmentCount++;
    }

}
