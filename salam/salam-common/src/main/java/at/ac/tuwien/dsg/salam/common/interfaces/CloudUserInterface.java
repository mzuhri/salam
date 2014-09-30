package at.ac.tuwien.dsg.salam.common.interfaces;

import at.ac.tuwien.dsg.salam.common.model.Assignment;
import at.ac.tuwien.dsg.salam.common.model.Task;

public interface CloudUserInterface {

    public void reserveAssignment(Assignment assignment);
    public void commitAssignment(Assignment assignment);
    
    public void pauseAssignment(Assignment assignment);
    public void resumeAssignment(Assignment assignment);
    public Assignment.Status queryAssignment(Assignment assignment);
    
    public void notifyTaskResult(Task task);
    public void notifyFinalizeAssignment(Assignment assignment);
    
    // this is internal method for waking up scheduler
    public void wakeSchedulerAt(long time);
    
}
