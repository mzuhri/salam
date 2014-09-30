package at.ac.tuwien.dsg.salam.cloud.monitor;

import java.util.Hashtable;

import at.ac.tuwien.dsg.salam.cloud.generator.AvailabilityGenerator;
import at.ac.tuwien.dsg.salam.common.interfaces.MetricMonitorInterface;
import at.ac.tuwien.dsg.salam.common.model.ComputingElement;
import at.ac.tuwien.dsg.salam.common.model.Service;
import at.ac.tuwien.dsg.salam.util.ConfigJson;
import at.ac.tuwien.dsg.salam.util.Util;

public class AvailabilityMonitor implements MetricMonitorInterface {

    public static int NOT_AVAILABLE = 0;
    public static int AVAILABLE = 1;
    public static int BUSY = 2;
    
    private static AvailabilityMonitor _instance;
    private static AvailabilityGenerator generator;
    private Hashtable<Long, String> availabilityCache;

    public AvailabilityMonitor() {
        availabilityCache = new Hashtable<Long, String>();
    }

    public static void initGenerator(ConfigJson config) {
        generator = new AvailabilityGenerator(config);
    }

    public static AvailabilityMonitor getInstance() {
        if (_instance==null) _instance = new AvailabilityMonitor();
        return _instance;
    }

    @Override
    public Object measure(Service service, String name, Object[] params) {
        return getInstance().measureMetric(service.getProvider(), name, params);
    }

    @Override
    public Object measure(ComputingElement element, String name, Object[] params) {
        return getInstance().measureMetric(element, name, params);
    }

    private Object measureMetric(ComputingElement element, String name, Object[] params) {
        Object result = null;
        switch (name) {
            case "response_time":
                if (params.length>=2) {
                    int start = (int) params[0]; 
                    int duration = (int) params[1]; 
                    int response = earliestResponseTime(element.getId(), start, duration);
                    result = response;
                }
                break;
            case "availability_status":
                if (params.length>=1) {
                    int time = (int) params[0]; 
                    int response = getStatus(element.getId(), time);
                    result = response;
                }
                break;
            case "availability_sequence":
                result = availabilityCache.get(element.getId());
                break;
        }
        return result;
    }

    @Override
    public Object update(Service service, String name, Object[] params) {
        getInstance().updateMetric(service.getProvider(), name, params);
        return null;
    }

    @Override
    public Object update(ComputingElement element, String name, Object[] params) {
        getInstance().updateMetric(element, name, params);
        return null;
    }

    private void updateMetric(ComputingElement element, String name, Object[] params) {
        switch (name) {
            case "availability_status":
                if (params.length>=3) {
                    int start = (int) params[0]; 
                    int duration = (int) params[1]; 
                    int status = (int) params[2]; 
                    setStatus(element.getId(), start, duration, status);
                }
                break;
            case "availability_sequence":
                if (params.length>=1) {
                    String seq = (String) params[0]; 
                    availabilityCache.put(element.getId(), seq);
                }
                break;
        }
    }

    private int getStatus(long id, int time) {

        // get availability sequence
        String sequence = availabilityCache.get(id);
        if (sequence==null) {
            sequence = generator.generate(time + 30, "");
        }
        if (sequence.length()<time+1) {
            sequence += generator.generate(time - sequence.length() + 30, sequence);
        }
        availabilityCache.put(id, sequence);
        return Character.getNumericValue(sequence.charAt(time));

    }


    private void setStatus(long id, int start, int duration, int status) {

        // get availability sequence
        String sequence = availabilityCache.get(id);
        if (sequence==null) {
            sequence = generator.generate(start + duration + 30, "");
        }
        if (sequence.length()<start + duration) {
            sequence += generator.generate(start + duration - sequence.length() + 30, sequence);
        }
        
        String replacement = Util.stringRepeat(Integer.toString(status), duration);
        sequence = sequence.substring(0, start-1) + replacement + sequence.substring(start+duration);

        availabilityCache.put(id, sequence);
    }

    private int earliestResponseTime(long id, int start, int duration) {

        // get availability sequence
        String sequence = availabilityCache.get(id);
        if (sequence==null) {
            sequence = generator.generate((duration>30?duration:30), "");
        }

        String block = Util.stringRepeat(Integer.toString(AVAILABLE), duration);
        int pos = sequence.indexOf(block, (int) start);

        int maxExtension = 5;
        int i = 0;
        while (pos==-1 && i<maxExtension) {
            sequence += generator.generate((duration>30?duration:30), sequence);
            pos = sequence.indexOf(block, (int) start);
            i++;
        }
        availabilityCache.put(id, sequence);

        if (pos==-1) {
            return -1;
        } else {
            return pos+duration;
        }

    }

}
