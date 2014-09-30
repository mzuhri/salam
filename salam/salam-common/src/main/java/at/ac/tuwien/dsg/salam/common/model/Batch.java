package at.ac.tuwien.dsg.salam.common.model;

import java.util.ArrayList;
import java.util.List;

public class Batch {

    private static int _lastId = 1;
    
    protected int id;
    protected List<Assignment> assignments;
    protected SCU runningSCU;
    protected Task task;
    
    // create batch from a task with empty assignees
    public Batch(Task task) {
        this();
        for (Role r: task.getAllRoles()) {
            Assignment a = new Assignment(null, task, r);
            addAssignment(a);
        }
        this.task = task;
    }
    
    public Batch(List<Assignment> assignments) {
        this();
        for (Assignment a: assignments) {
            addAssignment(a);
            if (this.task==null) this.task = a.getTask(); // TODO: make sure all assignments is for the same task
        }
    }

    public Batch() {
        this(_lastId++);
    }

    public Batch(int id) {
        this.id = id;
        assignments = new ArrayList<Assignment>();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
        // also set the task for all assignments
        for (Assignment a: assignments) {
            a.setTask(task);
        }
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
    
    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
        assignment.setBatchId(getId());
    }

    public int getId() {
        return id;
    }

    public SCU getRunningSCU() {
        return runningSCU;
    }

    public void setRunningSCU(SCU runningSCU) {
        this.runningSCU = runningSCU;
    }
    
    public void replaceAssignment(Assignment oldAssignment, Assignment newAssignment) {
        assignments.remove(oldAssignment);
        assignments.add(newAssignment);
        newAssignment.setBatchId(this.id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        Batch other = (Batch) obj;
        if (id != other.id)
            return false;
        return true;
    }

    
}
