package at.ac.tuwien.dsg.salam.common.model;

import java.util.Hashtable;

public class Properties {
    
    protected Hashtable<String, Object> valueSet;

    public Properties() {
        valueSet = new Hashtable<String, Object>();
    }

    public String toString() {
      String result = "";
      for (String name : valueSet.keySet()) {
        if (!result.equals("")) result += ",";
        result += valueSet.get(name).toString();
      }
      result = "{" + result + "}";
      //return result;
      return valueSet.toString();
    }
    
    public Object getValue(String name) {
        return valueSet.get(name);
    }
    
    public void setValue(String name, Object value) {
        valueSet.put(name, value);
    }
    
    public Hashtable<String, Object> getValueSet() {
        return valueSet;
    }
    
    public boolean has(String name) {
        return valueSet.containsKey(name);
    }
}
