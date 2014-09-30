package at.ac.tuwien.dsg.salam.common.interfaces;

import at.ac.tuwien.dsg.salam.common.model.Assignment;
import at.ac.tuwien.dsg.salam.common.model.Task;

public interface SchedulerInterface {

    public void setCloudUserInterface(CloudUserInterface cloudUserInterface);
    
    public void submitTask(Task task);
    public void notifyExecutionResult(Assignment assignment, Assignment.Status status);
    public void notifyReservationResult(Assignment assignment, Assignment.Status status);

    // this is internal method for waking up scheduler
    public void wake();
}
