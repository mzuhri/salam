package at.ac.tuwien.dsg.salam.common.sla.comparator;

import java.util.Comparator;

public class StringComparator implements Comparator<Object> {
    public int compare(Object o1, Object o2) {
        String val1 = (String)o1;
        String val2 = (String)o2;
        return val1.compareTo(val2);
    }
}