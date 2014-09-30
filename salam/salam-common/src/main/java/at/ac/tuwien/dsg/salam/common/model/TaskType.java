package at.ac.tuwien.dsg.salam.common.model;

import java.util.ArrayList;
import java.util.List;

public class TaskType {

    protected String name;
    protected String description;
    protected TaskType parent;
    
    // NOTE: when adding/removing sub-types, it is not necessary to update 
    //          the role list of the parent 
    protected List<Role> roles;
    protected List<TaskType> subTaskTypes;
    
    protected TaskDependency dependency;
    protected TaskPresentation presentation;
    protected Reward reward;
    
    public TaskType(String name, String description) {
        this(name, description, null);
    }

    public TaskType(String name, String description, TaskType parent) {
        this.name = name;
        this.description = description;
        this.parent = parent;
        this.roles = new ArrayList<Role>();
        this.subTaskTypes = new ArrayList<TaskType>();
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

    public TaskType getParent() {
        return parent;
    }

    public void setParent(TaskType parent) {
        this.parent = parent;
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
        for (TaskType child : subTaskTypes) {
            ArrayList<Role> childRoles = (ArrayList<Role>)child.getAllRoles();
            clone.addAll(childRoles);
        }
        return clone;
    }

    public List<TaskType> getSubTaskTypes() {
        return subTaskTypes;
    }

    public void addSubTaskTypes(TaskType taskType) {
        subTaskTypes.add(taskType);
    }

    public TaskType getRoot() {
        TaskType t = this;
        while (t.getParent()!=null) {
            t = t.getParent();
        }
        return t;
    }
}
