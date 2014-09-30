package at.ac.tuwien.dsg.salam.common.model;

public class Connection {

    // TODO: directed connection
    
    private ComputingElement element1; // element1.id<element2
    private ComputingElement element2;
    private double weight;

    public Connection() {
    }

    public Connection(ComputingElement element1) {
        this.element1 = element1;
        this.element2 = null;
        this.weight = Double.MAX_VALUE; // TODO: this max value will depend on our conectedness membership function
    }

    public Connection(ComputingElement element1, ComputingElement element2, double weight) {
        if (element1.getId()<element2.getId()) {
            this.element1 = element1;
            this.element2 = element2;
        } else {
            this.element1 = element2;
            this.element2 = element1;      
        }
        this.weight = weight;
    }

    public void setComputingElement1(ComputingElement element1) {
        this.element1 = element1;
    }

    public void setComputingElement2(ComputingElement element2) {
        this.element2 = element2;
    }

    public ComputingElement getComputingElement1() {
        return element1;
    }
    public ComputingElement getComputingElement2() {
        return element2;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight2) {
        this.weight = weight2;
    }
    
    public ComputingElement getOther(ComputingElement e1) {
        ComputingElement other = null;
        if (e1==element1) other = element2;
        else if (e1==element2) other= element1;
        return other;
    }

    @Override
    public String toString() {
        return "Conn [element1=" + element1 + ", element2=" + element2
                + ", weight=" + weight + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element1 == null) ? 0 : element1.hashCode());
        result = prime * result + ((element2 == null) ? 0 : element2.hashCode());
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
        Connection other = (Connection) obj;
        if (element1 == null) {
            if (other.element1 != null)
                return false;
        } else if (!element1.equals(other.element1))
            return false;
        if (element2 == null) {
            if (other.element2 != null)
                return false;
        } else if (!element2.equals(other.element2))
            return false;
        return true;
    }


}
