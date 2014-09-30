package at.ac.tuwien.dsg.salam.common.sla.comparator;

import java.util.Comparator;

public class NumericAscendingComparator implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        Double val1 = (Double)o1;
        Double val2 = (Double)o2;
        return val1.compareTo(val2);
    }

}
