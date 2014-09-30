package at.ac.tuwien.dsg.salam.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import at.ac.tuwien.dsg.salam.common.model.Role;
import at.ac.tuwien.dsg.salam.common.model.Task;

public class WekaExporter {

    public static void exportOld(List<Task> tasks, String pathPrefix) {

        FileWriter fstream;
        BufferedWriter out;
        
        if (tasks.size()==0) return;
        
        // assuming all tasks statistic have the same set of patterns
        Set<String> patterns = tasks.get(0).getStat().getPatternSet();
        List<Role> sortedRoles = tasks.get(0).getStat().getSortedRoles();

        try {
            
            for (String pattern: patterns) {

                String file = pathPrefix + pattern + ".arff";
                fstream = new FileWriter(file);
                out = new BufferedWriter(fstream);
                
                // print out headers
                out.write("@RELATION pattern\n\n");
                //out.write("@ATTRIBUTE task STRING\n");
                //out.write("@ATTRIBUTE task_status STRING\n");
                for (Role role: sortedRoles) {
                    out.write("@ATTRIBUTE " + role.getFunctionality().getName() + " NUMERIC\n");
                }
                out.write("@ATTRIBUTE response_time NUMERIC\n\n");
                out.write("@DATA\n");
                out.flush();

                for (Task task: tasks) {
                    String row = task.getStat().dump(pattern, sortedRoles);
                    out.write(row + "\n");
                    out.flush();
                }
                
                out.close();
                fstream.close();
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }
    
    public static void export(List<Task> tasks, String pathPrefix) {

        if (tasks.size()==0) return;
        
        try {
            
            Hashtable<String, FileWriter> fstreams = new Hashtable<String, FileWriter>();
            Hashtable<String, BufferedWriter> buffers = new Hashtable<String, BufferedWriter>();
            
            for (Task task: tasks) {
                
                Set<String> patterns = task.getStat().getPatternSet();
                List<Role> sortedRoles = task.getStat().getSortedRoles();
                
                for (String p: patterns) {
                    
                    String pattern = getNormalizedPattern(p);
                    
                    // get or initate file for this pattern
                    FileWriter fstream = fstreams.get(pattern);
                    if (fstream==null) {
                        String file = pathPrefix + pattern + ".arff";
                        fstream = new FileWriter(file);
                        fstreams.put(pattern, fstream);
                    }
                    BufferedWriter buffer = buffers.get(pattern);
                    if (buffer==null) {
                        buffer = new BufferedWriter(fstream);
                        buffers.put(pattern, buffer);

                        // print out headers
                        buffer.write("@RELATION pattern\n\n");
                        //buffer.write("@ATTRIBUTE task STRING\n");
                        //buffer.write("@ATTRIBUTE task_status STRING\n");
                        for (Role role: sortedRoles) {
                            buffer.write("@ATTRIBUTE " + role.getFunctionality().getName() + " NUMERIC\n");
                        }
                        buffer.write("@ATTRIBUTE response_time NUMERIC\n\n");
                        buffer.write("@DATA\n");
                        buffer.flush();
                    }
                    
                    String row = task.getStat().dump(p, sortedRoles);
                    buffer.write(row + "\n");
                    buffer.flush();
                    
                }
                
            }
            
            // close all
            for (BufferedWriter buffer: buffers.values()) {
                buffer.close();
            }
            for (FileWriter stream: fstreams.values()) {
                stream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    private static String getNormalizedPattern(String pattern) {
        String[] roles = pattern.split("-");
        ArrayList<String> roleList = new ArrayList<String>();
        for (String r: roles) {
            roleList.add(r);
        }
        Collections.sort(roleList);
        String result = "";
        int i = 0;
        for (String r: roleList) {
            if (i>0) result = result.concat("-");
            result = result.concat(r);
            i++;
        }
        return result;
    }

}
