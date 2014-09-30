package at.ac.tuwien.dsg.salam.common.interfaces;

import java.util.List;

import at.ac.tuwien.dsg.salam.common.model.Assignment;
import at.ac.tuwien.dsg.salam.common.model.Role;
import at.ac.tuwien.dsg.salam.common.model.Task;
import at.ac.tuwien.dsg.salam.common.model.optimization.TaskWithOptimization;

public interface ComposerInterface {

    // compose methods
    public List<Assignment> compose(Task task);
    public List<Assignment> compose(TaskWithOptimization task);
    
    // partial compose methods 
    public List<Assignment> partialCompose(Task task, List<Role> roles);
    public List<Assignment> partialCompose(TaskWithOptimization task, List<Role> roles);
    
}
