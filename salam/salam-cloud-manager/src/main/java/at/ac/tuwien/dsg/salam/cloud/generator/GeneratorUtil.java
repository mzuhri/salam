package at.ac.tuwien.dsg.salam.cloud.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.ac.tuwien.dsg.salam.common.interfaces.MetricMonitorInterface;
import at.ac.tuwien.dsg.salam.common.model.HumanComputingElement;

public class GeneratorUtil {

    public final static String DISTRIBUTION_PACKAGE = "org.apache.commons.math3.distribution.";
    
    public static Object createValueDistribution(String clazz, JSONArray params, 
            int seed) 
            throws InstantiationException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, 
            SecurityException, ClassNotFoundException, JSONException {
        
        // find constructor
        Constructor[] constructors = Class.forName(clazz).getConstructors();
        Constructor constructor = null;
        for (int i=0; i<constructors.length; i++) {
            Class[] parameterTypes = constructors[i].getParameterTypes();
            if (clazz.startsWith(DISTRIBUTION_PACKAGE)) {
                if (parameterTypes.length==params.length()+1 && 
                        parameterTypes[0].equals(RandomGenerator.class)) {
                    constructor = constructors[i];
                    break;
                }
            } else {
                if (parameterTypes.length==params.length()) {
                    constructor = constructors[i];
                    break;
                }                
            }
        }
        
        if (constructor==null) throw new ClassNotFoundException("Constructor not found, class: " + 
                clazz + "([RandomGenerator], " + params + ")");

        // constructor param list
        Class[] parameterTypes = constructor.getParameterTypes();
        Object[] paramList = new Object[parameterTypes.length];
        int i=0;
        if (clazz.startsWith(DISTRIBUTION_PACKAGE)) {
            paramList[i++] = new MersenneTwister(seed);
        }
        for (int j=0; j<params.length(); j++) {
            paramList[i++] = params.get(j);
        }
        
        Object dist = constructor.newInstance(paramList);
        
        return dist;
    }
    
    public static MetricMonitorInterface createMetricObject(String clazz) 
            throws InstantiationException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, 
            SecurityException, ClassNotFoundException, JSONException {
        
        Object metric = Class.forName(clazz).newInstance();
        return (MetricMonitorInterface) metric;
    }

    public static Object sample(Object dist) {
        return sample(dist, null);
    }
    
    public static Object sample(Object dist, JSONObject mapping) {
        return sample(dist, mapping, "sample");
    }
    
    public static Object sample(Object dist, JSONObject mapping, String methodName) {
        Object value = null;
        try {
            Method method = dist.getClass().getMethod(methodName, null);
            value = method.invoke(dist, null);
            if (mapping!=null) {
                String svalue = mapping.getString(value.toString());
                value = svalue;
            }
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    public static void generateProperty(HumanComputingElement element, 
            String propName, String propType, Object distValue, JSONObject mapping) {
        Object value = GeneratorUtil.sample(distValue, mapping);
        switch (propType) {
            case "static":
                element.getProperties().setValue(propName, value);
                break;
            case "skill":
                element.getSkills().setValue(propName, value);
                break;
        }
    }

    public static boolean shouldHave(UniformRealDistribution dist, 
            double probabilityToHave) {
        return dist.sample()<=probabilityToHave;
    }
    
    public static String getFullClassName(String name) {
        if (name.contains(".")) return name;
        else return DISTRIBUTION_PACKAGE + name;
    }
    
    public static int selectOne(UniformRealDistribution dist, double[] probabilityToHave) {

        // sum all probability to have for normalizing them
        double sum = 0;
        for (int i=0; i<probabilityToHave.length; i++) {
            sum += probabilityToHave[i];
        }
        
        double x = dist.sample() * sum;
        
        double limit = 0;
        for (int i=0; i<probabilityToHave.length; i++) {
            limit += probabilityToHave[i];
            if (x <= limit) return i;
        }
        
        return probabilityToHave.length - 1;
    }
    
}
