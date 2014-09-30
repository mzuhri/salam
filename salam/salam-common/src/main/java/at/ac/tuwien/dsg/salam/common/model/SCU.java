package at.ac.tuwien.dsg.salam.common.model;


public class SCU {

    Batch batch;
    Metrics metrics;
    Properties properties;
    
    public SCU() {
        this(new Batch());
    }

    public SCU(Batch batch) {
        this.batch = batch;
        metrics = new Metrics();
        properties = new Properties();
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batch == null) ? 0 : batch.hashCode());
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
        SCU other = (SCU) obj;
        if (batch == null) {
            if (other.batch != null)
                return false;
        } else if (!batch.equals(other.batch))
            return false;
        return true;
    }
    
    
    
}
