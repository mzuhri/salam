package at.ac.tuwien.dsg.salam.common.model.optimization;

import at.ac.tuwien.dsg.salam.common.model.Task;

public class TaskWithOptimization extends Task {
    
    OptimizationObjective optimizationObjective;

    public TaskWithOptimization(double load, String name, String description,
            Task parent) {
        super(name, description, load, parent);
        optimizationObjective = new OptimizationObjective();
    }
    
    public TaskWithOptimization(Task t) {
        super();
        this.id = t.getId();
        this.load = t.getLoad();
        this.type = t.getType();
        this.name = t.getName();
        this.description = t.getDescription();
        this.submissionTime = t.getSubmissionTime();
        this.parent = t.getParent();
        this.subTasks = t.getSubTasks();
        this.specification = t.getSpecification();
        this.roles = t.getRoles();
        this.dependency = t.getDependency();
        this.presentation = t.getPresentation();
        this.reward = t.getReward();        
        optimizationObjective = new OptimizationObjective();
    }

    public OptimizationObjective getOptObjective() {
        return optimizationObjective;
    }

    public void setOptObjective(OptimizationObjective optimizationObjective) {
        this.optimizationObjective = optimizationObjective;
    }

    public String detail() {
        return "Task [id=" + id + ", load=" + load + ", name=" + name
                + ", roles=" + roles //+ ", subTasks=" + subTasks
                + ", spec=" + specification 
                + ", optObj=" + optimizationObjective + "]";
    }
    
    
}
