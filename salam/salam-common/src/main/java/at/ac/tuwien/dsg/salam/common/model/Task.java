package at.ac.tuwien.dsg.salam.common.model;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.salam.common.sla.Specification;
import at.ac.tuwien.dsg.salam.common.statistics.StatisticPoints;

public class Task {

    public enum Status {
        CREATED (100, "Created"),
        QUEUED (110, "Queued"),
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
    
    protected int id;
    protected TaskType type;
    protected String name;
    protected String description;
    protected double submissionTime;
    protected double load;

    protected Task parent;
    protected List<Task> subTasks;

    protected List<Role> roles;
    protected Specification specification;
    protected TaskDependency dependency;
    protected TaskPresentation presentation;
    protected Reward reward;
    protected Status status;
    protected StatisticPoints stat;
    
    private static int _lastId = 1;

    public Task(double load, Task parent, TaskType type) {
        this(type.getName(), type.getDescription(), load, parent, type);
    }
    
    @SuppressWarnings("unchecked")
	public Task(String name, String description, double load, 
            Task parent, TaskType type) {
        super();
        this.id = _lastId++;
        this.load = load;
        this.type = type;
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.subTasks = new ArrayList<Task>();
        
        // clone properties from task type
        this.roles = (ArrayList<Role>)((ArrayList<Role>) type.getRoles()).clone();
        this.dependency = (TaskDependency) type.getDependency().clone();
        this.presentation = (TaskPresentation) type.getPresentation().clone();
        this.reward = (Reward) type.getReward().clone();
        this.status = Task.Status.CREATED;
        this.stat = new StatisticPoints(this);
    }

    public Task(String name, String description, double load, Task parent) {
        super();
        this.id = _lastId++;
        this.load = load;
        this.type = null;
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.subTasks = new ArrayList<Task>();
        this.roles = new ArrayList<Role>();
        this.dependency = new TaskDependency();
        this.presentation = new TaskPresentation();
        this.reward = new Reward();
        this.status = Task.Status.CREATED;
        this.stat = new StatisticPoints(this);
    }

    public Task() {
        this.subTasks = new ArrayList<Task>();
        this.roles = new ArrayList<Role>();
        this.dependency = new TaskDependency();
        this.presentation = new TaskPresentation();
        this.reward = new Reward();
        this.stat = new StatisticPoints(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Role getRole(Functionality functionality) {
        int index = roles.indexOf(new Role(functionality));
        Role r = null;
        if (index>-1) {
            r = roles.get(index);
        } 
        return r;
    }

    public void addUpdateRole(Role role) {
        int index = roles.indexOf(role);
        if (index>-1) {
            // replace
            roles.set(index, role);
        } else {
            // add
            roles.add(role);
        }
    }
    
    public void removeRole(Role role) {
        roles.remove(role);
    }
    
    /**
     * Recursively get the list of the roles of this task type and its descendents
     * @return ArrayList<Role>
     */
    @SuppressWarnings("unchecked")
	public List<Role> getAllRoles() {
        ArrayList<Role> clone = (ArrayList<Role>)((ArrayList<Role>) this.roles).clone();
        for (Task child : subTasks) {
            ArrayList<Role> childRoles = (ArrayList<Role>)child.getAllRoles();
            clone.addAll(childRoles);
        }
        return clone;
    }

    public TaskDependency getDependency() {
        return dependency;
    }

    public void setDependency(TaskDependency dependency) {
        this.dependency = dependency;
    }

    public TaskPresentation getPresentation() {
        return presentation;
    }

    public void setPresentation(TaskPresentation presentation) {
        this.presentation = presentation;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }
    
    public void addSubTask(Task task) {
        subTasks.add(task);
    }

    public void addSubTask(double load, TaskType taskType) {
        Task newTask = new Task(load, this, taskType);
        subTasks.add(newTask);
    }

    public Task getRoot() {
        Task t = this;
        while (t.getParent()!=null) {
            t = t.getParent();
        }
        return t;
    }
    
    public double getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(double submissionTime) {
        this.submissionTime = submissionTime;
    }

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }
    
    public Object getObjectiveValue(String name, Object defaultValue) {
    	if (specification.findObjective(name)==null) return defaultValue;
        return specification.findObjective(name).getValue();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public StatisticPoints getStat() {
        return stat;
    }

    public void setStat(StatisticPoints stat) {
        this.stat = stat;
    }

    @Override
    public String toString() {
        //return "Task #" + id + " (" + status + ")";
    	return "Task #" + id;
    }

    public String detail() {
        return "Task [id=" + id + ", load=" + load + ", name=" + name
                + ", roles=" + roles + ", subTasks=" + subTasks
                + ", spec=" + specification + "]";
    }

    /**
     * Tool to instantiate tasks from a TaskType.
     * Note: this can be implementation specific.
     */
    
    
}
