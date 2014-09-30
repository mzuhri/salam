package at.ac.tuwien.dsg.salam.common.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import at.ac.tuwien.dsg.salam.common.model.Assignment;
import at.ac.tuwien.dsg.salam.common.model.Role;
import at.ac.tuwien.dsg.salam.common.model.Task;
import at.ac.tuwien.dsg.salam.common.model.Assignment.Status;

public class StatisticPoints {
    
    private class Record {
        public Hashtable <Role, Double> data; 
        public Record() {
            data = new Hashtable <Role, Double>();
        }
    }
    
    private Hashtable<String, Record> times;
    private Double totalTime;
    private String lastPattern;
    private Task task;
    
    public StatisticPoints(Task task) {
        
        this.times = new Hashtable<String, Record>();
        this.lastPattern = "";
        this.task = task;
    }
    
    public List<Role> getSortedRoles() {
        // clone roles and sort them
        List<Role> sortedRoles = new ArrayList<Role>();
        for (Role r: task.getAllRoles()) {
            sortedRoles.add(r);
        }
        Collections.sort(sortedRoles);
        return sortedRoles;
    }
    
    public void recordAssignment(Assignment assignment, List<Assignment> assignments, Double currentTime) {

        Record record = new Record();
        Record lastRecord = null;
        if (!lastPattern.equals("")) {
            lastRecord = times.get(lastPattern);
        }
        
        // iterarate trough roles
        for (Role r: task.getAllRoles()) {
            double time = 0.0;
            if (assignment.getRole()==r) {
                //time = assignment.getFinishTime() - assignment.getStartTime();
                //time = assignment.getFinishTime() - task.getSubmissionTime();
                time = assignment.getFinishTime() - assignment.getCommitTime();
            } else {
                // check if it is running
                Assignment a = findAssignmetByRole(assignments, r);
                if (a!=null && a.getStatus()==Status.RUNNING) {
                    time = currentTime - a.getStartTime();
                } else if (lastRecord!=null) {
                    time = lastRecord.data.get(r);
                } else {
                    time = 0.0;
                }
            }
            record.data.put(r, time);
        }
        
        if (!lastPattern.equals("")) lastPattern = lastPattern + "-" + assignment.getRole().getFunctionality().getName();
        else lastPattern = assignment.getRole().getFunctionality().getName();
        
        times.put(lastPattern, record);
        
    }
    
    public void recordFinishTime(double time) {
        totalTime = time - task.getSubmissionTime();
    }
    
    public Assignment findAssignmetByRole(List<Assignment> assignments, Role role) {
        Assignment result = null;
        for (Assignment a: assignments) {
            if (a.getRole()==role) {
                result = a;
                break;
            }
        }
        return result;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    
    public Set<String> getPatternSet() {
        return times.keySet();
    }
    
    public String dump() {
        String result = "";
        List<Role> roles = getSortedRoles();
        for (String pattern: times.keySet()) {
            result += pattern + " = ";
            result += dump(pattern, roles) + "\n";
        }
        result += "Execution time = " + totalTime;
        return result;
    }
    
    
    public String dump(String pattern, List<Role> roles) {
        String row = "";
        boolean empty = false;
        for (Role r: roles) {
            if (!row.equals("")) row += ",";
            if (times.get(pattern)!=null) {
                row += times.get(pattern).data.get(r);
            } else {
                row += "% NULL";
                empty = true;
                break;
            }
        }
        if (!empty) {
            //row = "'Task #" + task.getId() + "','" + task.getStatus() + "'," + row;
            row += "," + totalTime;
        }
        return row;
    }
    
}
