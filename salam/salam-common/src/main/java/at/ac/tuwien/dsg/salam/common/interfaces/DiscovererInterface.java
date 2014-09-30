package at.ac.tuwien.dsg.salam.common.interfaces;

import java.util.List;

import at.ac.tuwien.dsg.salam.common.model.Functionality;
import at.ac.tuwien.dsg.salam.common.model.Service;
import at.ac.tuwien.dsg.salam.common.sla.Specification;

public interface DiscovererInterface {

    // discover service without time constraint
    public List<Service> discoverServices(Functionality functionality,
            Specification specification);   
    
    // discover service with time constraint
    List<Service> discoverServices(Functionality functionality,
            Specification specification, double timeStart, double load,
            double deadline);

}
