package at.ac.tuwien.dsg.salam.cloud.generator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.dsg.salam.common.model.Functionality;
import at.ac.tuwien.dsg.salam.common.model.Role;
import at.ac.tuwien.dsg.salam.common.model.Task;
import at.ac.tuwien.dsg.salam.common.sla.Objective;
import at.ac.tuwien.dsg.salam.common.sla.Specification;
import at.ac.tuwien.dsg.salam.util.ConfigJson;

public class TaskGenerator {

    private Logger logger = Logger.getLogger("Generator");
    private JSONObject configRoot = null;
    
    // distribution generators
    private Hashtable<String, Object> distOccurances;
    private Hashtable<String, Object> distLoads;
    private Hashtable<String, Object> distValues;
    private Hashtable<String, UniformRealDistribution> distToHaves;
    
    private int seed;
    Hashtable<String, JSONObject> taskTypes;

    public TaskGenerator(ConfigJson config) {
        this.configRoot = config.getRoot();
        distOccurances = new Hashtable<String, Object>();
        distLoads = new Hashtable<String, Object>();
        distValues = new Hashtable<String, Object>();
        distToHaves = new Hashtable<String, UniformRealDistribution>();
        taskTypes = new Hashtable<String, JSONObject>();
    }
    
    public ArrayList<Task> generate() 
            throws InstantiationException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, 
            SecurityException, ClassNotFoundException, JSONException {
        ArrayList<Task> rootTasks = new ArrayList<Task>();
        return generate(rootTasks);
    }

    public ArrayList<Task> generate(ArrayList<Task> rootTasks) 
            throws InstantiationException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, 
            SecurityException, ClassNotFoundException, JSONException {
        
        // init
        ArrayList<Task> tasks = new ArrayList<Task>();

        // get config
        seed = configRoot.getInt("seed");
        JSONArray taskTypeCfgs = configRoot.getJSONArray("taskTypes");
        for (int i=0; i<taskTypeCfgs.length(); i++) {
            taskTypes.put(taskTypeCfgs.getJSONObject(i).getString("name"), 
                    taskTypeCfgs.getJSONObject(i));
        }
        
        // generate tasks
        for (JSONObject curTaskTypeCfg: taskTypes.values()) {
            if (curTaskTypeCfg.getBoolean("isRootTask")) {
                tasks = createTask(curTaskTypeCfg, null, "", 1.0, rootTasks);
            }
        }
        
        return tasks;
    }
    
    protected ArrayList<Task> createTask(JSONObject taskTypeCfg, Task parent,
            String parentPath, double probabilityToHave,
            ArrayList<Task> rootTasks) 
            throws InstantiationException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, 
            SecurityException, ClassNotFoundException, JSONException {

        // init
        ArrayList<Task> tasks = new ArrayList<Task>();
        
        // get config
        String name = taskTypeCfg.getString("name");
        String description = taskTypeCfg.getString("description");
        String currentPath = parentPath + "/" + name;

        // calc occurance
        long occurance = 0;
        if (taskTypeCfg.getBoolean("isRootTask")) {
            //logger.info("Generating " + name + " tasks...");
            // init occurance distribution
            JSONObject occuranceCfg = taskTypeCfg.getJSONObject("tasksOccurance");
            if (distOccurances.get(name)==null) {
                String clazz = GeneratorUtil.getFullClassName(occuranceCfg.getString("class"));
                JSONArray params = occuranceCfg.getJSONArray("params");
                Object dist = GeneratorUtil.createValueDistribution(clazz, params, seed++);
                distOccurances.put(name, dist);
            }
            Object curDistOccurance = distOccurances.get(name);
            // TODO: use sampleMethod on other places as well
            String sampleMethod = occuranceCfg.has("sampleMethod") ? occuranceCfg.getString("sampleMethod") : "sample";
            occurance = Math.round((double)GeneratorUtil.sample(curDistOccurance, null, sampleMethod));
        } else {
            if (distToHaves.get(currentPath)==null) {
                UniformRealDistribution dist = new UniformRealDistribution(new MersenneTwister(seed++), 0, 1);
                distToHaves.put(currentPath, dist);
            }
            UniformRealDistribution distToHave = distToHaves.get(currentPath);
            if (GeneratorUtil.shouldHave(distToHave, probabilityToHave)) {
                occurance = 1;
            } else {
                occurance = 0;
            }
        }
        
        // init load distribution
        if (distLoads.get(name)==null) {
            JSONObject loadCfg = taskTypeCfg.getJSONObject("load");
            String clazz = GeneratorUtil.getFullClassName(loadCfg.getString("class"));
            JSONArray params = loadCfg.getJSONArray("params");
            Object dist = GeneratorUtil.createValueDistribution(clazz, params, seed++);
            distLoads.put(name, dist);
        }

        // create task
        Object curDistLoad = distLoads.get(name);
        for (int i=0; i<occurance; i++) {
            
            double load = (double)GeneratorUtil.sample(curDistLoad, null);
            Task task = new Task(name, description, load, parent);
            if (parent!=null) parent.addSubTask(task);
            if (taskTypeCfg.getBoolean("isRootTask")) {
                rootTasks.add(task);
            }
            tasks.add(task);
            
            // create roles
            JSONArray rolesCfg = taskTypeCfg.getJSONArray("roles");
            createRoles(rolesCfg, task, currentPath);
            
            // create specification
            JSONArray specCfg = taskTypeCfg.getJSONArray("specification");
            Specification spec = createSpecification(specCfg, currentPath);
            task.setSpecification(spec);
            
            // create sub tasks
            JSONArray subTaskCfgs = taskTypeCfg.getJSONArray("subTaskTypes");
            for (int j=0; j<subTaskCfgs.length(); j++) {
                JSONObject curSubTaskCfg = subTaskCfgs.getJSONObject(j);
                String subTaskName = curSubTaskCfg.getString("name");
                double subTaskPToHave = curSubTaskCfg.getDouble("probabilityToHave");
                logger.info("Generating sub-tasks...");
                ArrayList<Task> subTasks = createTask(taskTypes.get(subTaskName), 
                        task, currentPath, subTaskPToHave, rootTasks);
                tasks.addAll(subTasks);
            }
        }
        
        return tasks;
    }
    
    private void createRoles(JSONArray rolesCfg, Task task, String parentPath) 
            throws InstantiationException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, 
            SecurityException, ClassNotFoundException, JSONException {
        
        Hashtable<String, Role> roles = new Hashtable<String, Role>(); 
        Hashtable<String, JSONArray> dependecies = new Hashtable<String, JSONArray>();
        
        // iterate roles
        for (int i=0; i<rolesCfg.length(); i++) {

            // get role config
            JSONObject roleCfg = rolesCfg.getJSONObject(i);
            String func = roleCfg.getString("functionality");
            double pRoleToHave = roleCfg.getDouble("probabilityToHave");
            double loadRatio = roleCfg.has("relativeLoadRatio") ? roleCfg.getDouble("relativeLoadRatio") : 1.0;
            JSONArray specCfg = roleCfg.getJSONArray("specification");
            String currentRolePath = parentPath + "/{r}" + func;
            if (roleCfg.has("dependsOn")) {
                dependecies.put(func, roleCfg.getJSONArray("dependsOn"));
            }
            
            // init dist
            if (distToHaves.get(currentRolePath)==null) {
                UniformRealDistribution dist = new UniformRealDistribution(
                        new MersenneTwister(seed++), 0, 1);
                distToHaves.put(currentRolePath, dist);
            }
            UniformRealDistribution distRoleToHave = distToHaves.get(currentRolePath);
            
            if (GeneratorUtil.shouldHave(distRoleToHave, pRoleToHave)) {
                
                // create role
                Role role = new Role(new Functionality(func));
                task.addUpdateRole(role);
                roles.put(func, role);
                
                // create spec
                Specification spec = createSpecification(specCfg, currentRolePath);
                role.setSpecification(spec);
                
                // set load
                role.setLoad(loadRatio * task.getLoad());
                
            }
            
        }
        
        // role dependency
        for (String func: dependecies.keySet()) {
            Role me = roles.get(func); 
            JSONArray others = dependecies.get(func);
            for (int i=0; i<others.length(); i++) {
                String other = others.getString(i);
                if (other.startsWith("*")) {
                    other = other.substring(1);
                    if (roles.get(other)!=null) me.addStrongDependency(roles.get(other));
                } else {
                    if (roles.get(other)!=null) me.addWeakDependency(roles.get(other));
                }
            }
        }
        
    }
    

    private Specification createSpecification(JSONArray specCfg, String parentPath) 
            throws InstantiationException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, 
            SecurityException, ClassNotFoundException, JSONException {
        // create spec
        Specification spec = new Specification();
        
        // iterate spec
        for (int j=0; j<specCfg.length(); j++) {
            
            // get spec's objective config
            JSONObject curSpecCfg = specCfg.getJSONObject(j);
            String type = curSpecCfg.getString("type");
            String name = curSpecCfg.getString("name");
            double pToHave = curSpecCfg.getDouble("probabilityToHave");
            JSONObject valueCfg = curSpecCfg.getJSONObject("value");
            String clazz = GeneratorUtil.getFullClassName(valueCfg.getString("class"));
            JSONArray params = valueCfg.getJSONArray("params");
            JSONObject mapping = null;
            if (valueCfg.has("mapping")) mapping = valueCfg.getJSONObject("mapping");
            String comparator = "";
            if (curSpecCfg.has("comparator")) comparator = curSpecCfg.getString("comparator");
            String currentObjectivePath = parentPath + "/{o}" + name;
            
            // init dist
            if (distToHaves.get(currentObjectivePath)==null) {
                UniformRealDistribution dist = new UniformRealDistribution(
                        new MersenneTwister(seed++), 0, 1);
                distToHaves.put(currentObjectivePath, dist);
            }
            UniformRealDistribution distObjToHave = distToHaves.get(currentObjectivePath);
            if (distValues.get(currentObjectivePath)==null) {
                Object dist = GeneratorUtil.createValueDistribution(clazz, params, seed++);
                distValues.put(currentObjectivePath, dist);
            }
            Object distObjValue = distValues.get(currentObjectivePath);
            
            if (GeneratorUtil.shouldHave(distObjToHave, pToHave)) {
                // create objective
                Object value = GeneratorUtil.sample(distObjValue, mapping);
                Comparator comparatorObj = null;
                if (!comparator.equals("")) comparatorObj = (Comparator)Class.forName(comparator).newInstance();
                Objective objective = new Objective(name, value, toObjType(type), comparatorObj);
                spec.addObjective(objective);
            }
        }
        return spec;
        
    }
    
    public Objective.Type toObjType(String type) {
        Objective.Type t = null;
        switch(type) {
            case "skill":
                t = Objective.Type.SKILL;
                break;
            case "metric":
                t = Objective.Type.METRIC;
                break;
            case "static":
                t = Objective.Type.STATIC;
                break;
        }
        return t;
    }
    
}
