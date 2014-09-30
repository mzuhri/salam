package at.ac.tuwien.dsg.salam.common.model;

public class Assignment {
    
    public enum Status {
        IDLE (100, "Idle"),
        RESERVED (110, "Reserved"),
        READY (120, "Ready"),
        RUNNING (200, "Running"),
        SUSPENDED (300, "Suspended"),
        SUCCESSFUL (410, "Successful"),
        FAILED (420, "Failed");
        
        int code;
        String description;
        private Status(int code, String description) {
            this.code = code;
            this.description = description;
        }
        public String toString() {
            return description;
        }
    };
    
    private static int _lastId = 1000000;

    protected int id;
    protected int batchId;
    protected Service assignee;
    protected Task task;
    protected Role role;
    protected Status status;
    protected double commitTime;
    protected double startTime;
    protected double finishTime;
    protected double forecastedDuration;
    
    public Assignment(Service assignee, Task task, Functionality functionality) {
        this();
        this.assignee = assignee;
        this.task = task;
        setRoleByFunctionality(functionality);
    }

    public Assignment(Service assignee, Task task, Role role) {
        this();
        this.assignee = assignee;
        this.task = task;
        this.role = role;
    }
    
    public Assignment() {
        this.id = _lastId++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Service getAssignee() {
        return assignee;
    }

    public void setAssignee(Service assignee) {
        this.assignee = assignee;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }
    
    public double getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(double commitTime) {
        this.commitTime = commitTime;
    }

    public double getForecastedDuration() {
        return forecastedDuration;
    }

    public void setForecastedDuration(double forecastedDuration) {
        this.forecastedDuration = forecastedDuration;
    }

    public void setRoleByFunctionality(Functionality functionality) {
        Role r = task.getRole(functionality);
        if (r!=null) this.role = r;        
    }

    @Override
    public String toString() {
        return "[" + assignee 
                + ", " + role.getFunctionality() 
                + ", " + task
                + ", startAt " + startTime
                + ", duration " + forecastedDuration
                + ", status " + status
                + "]";
    }
    
    

}
