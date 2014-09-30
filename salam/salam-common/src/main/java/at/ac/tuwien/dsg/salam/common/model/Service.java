package at.ac.tuwien.dsg.salam.common.model;


public class Service {

    protected int id;
    protected Functionality functionality;
    protected String description;
    protected Object _interface;
    
    protected ComputingElement provider;
    protected Metrics metrics; // this is to keep runtime svc metrics, which may be 
                               // different from computing element metrics
    
    public Service(Functionality functionality, ComputingElement provider) {
        this.functionality = functionality;
        setProvider(provider);
        metrics = new Metrics();
        metrics.setOwner(getProvider());
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ComputingElement getProvider() {
        return provider;
    }
    
    public void setProvider(ComputingElement provider) {
        this.provider = provider;
        this.provider.addService(this);
    }
    
    public Metrics getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }
    
    public Object getMetric(String name) {
        return getMetric(name, null);
    }
    
    public Object getMetric(String name, Object[] params) {
        Object metricValue = null;
        if (metrics.has(name)) {
            metricValue = metrics.getValue(name, params); 
        } else {
            // try to get metric from service provider
            metricValue = getProvider().getMetrics().getValue(name, params); 
        }
        return metricValue;
    }
    
    public Functionality getFunctionality() {
        return functionality;
    }
    
    public void setFunctionality(Functionality functionality) {
        this.functionality = functionality;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Object getInterface() {
        return _interface;
    }
    
    public void setInterface(Object _interface) {
        this._interface = _interface;
    }
    
    public String getTitle() {
        String title = "";
        if (provider.getType()==ComputingElement.HUMAN_COMPUTING_ELEMENT_TYPE) {
            title = "HBS_";
        } else if (provider.getType()==ComputingElement.MACHINE_COMPUTING_ELEMENT_TYPE) {
            title = "SBS_";
        } else {
            title = "SVC_";
        }
        title += functionality + "_" + provider.getId();
        return title;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((functionality == null) ? 0 : functionality.hashCode());
        result = prime * result
                + ((provider == null) ? 0 : provider.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        // two services are equal if they provide functionality from the same provider 
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Service other = (Service) obj;
        if (functionality == null) {
            if (other.functionality != null)
                return false;
        } else if (!functionality.equals(other.functionality))
            return false;
        if (provider == null) {
            if (other.provider != null)
                return false;
        } else if (!provider.equals(other.provider))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        //return getTitle();
        return provider + "[" + getTitle() + "]";
    }
    
    public String detail() {
        return getTitle() + "[" + provider + "]";
    }
    
    
}
