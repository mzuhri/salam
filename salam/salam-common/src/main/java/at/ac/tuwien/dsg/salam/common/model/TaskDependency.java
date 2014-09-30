package at.ac.tuwien.dsg.salam.common.model;

public class TaskDependency implements Cloneable {

    // TODO: define details here
    
    @Override
    protected Object clone() {
        // TODO: deep cloning
        Object obj = null;
        try {
            obj = super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return obj;
    }    
}
