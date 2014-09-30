package at.ac.tuwien.dsg.salam.common.model;

import java.util.Hashtable;

import at.ac.tuwien.dsg.salam.common.interfaces.MetricMonitorInterface;

public class Metrics extends Properties {

    protected Hashtable<String, MetricMonitorInterface> valueSet;
    private ComputingElement owner;

    public Metrics() {
        valueSet = new Hashtable<String, MetricMonitorInterface>();
    }

    public void setOwner(ComputingElement owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        String result = "";
        for (String name : valueSet.keySet()) {
            if (!result.equals("")) result += ",";
            result += valueSet.get(name).toString();
        }
        result = "{" + result + "}";
        return result;
    }

    @Override
    public Object getValue(String name) {
        MetricMonitorInterface metricInterface = valueSet.get(name);
        Object result = null;
        if (metricInterface!=null) {
            result = metricInterface.measure(owner, name, null);
        }
        return result;
    }

    public Object getValue(String name, Object[] params) {
        MetricMonitorInterface metricInterface = valueSet.get(name);
        Object result = null;
        if (metricInterface!=null) {
            result = metricInterface.measure(owner, name, params);
        }
        return result;
    }

    @Override
    public void setValue(String name, Object value) {
        MetricMonitorInterface metricInterface = valueSet.get(name);
        if (metricInterface!=null) {
            metricInterface.update(owner, name, new Object[]{value});
        }
    }

    public void setValue(String name, Object[] value) {
        MetricMonitorInterface metricInterface = valueSet.get(name);
        if (metricInterface!=null) {
            metricInterface.update(owner, name, value);
        }
    }

    @Override
    public boolean has(String name) {
        return valueSet.containsKey(name);
    }

    public void setInterface(String name, MetricMonitorInterface iface) {
        valueSet.put(name, iface);
    }

}
