package at.ac.tuwien.dsg.salam.cloud.discoverer;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.salam.common.fuzzy.MembershipFunction;
import at.ac.tuwien.dsg.salam.common.fuzzy.function.SkillMembershipFunctionCollection;
import at.ac.tuwien.dsg.salam.common.interfaces.DiscovererInterface;
import at.ac.tuwien.dsg.salam.common.interfaces.ServiceManagerInterface;
import at.ac.tuwien.dsg.salam.common.model.Functionality;
import at.ac.tuwien.dsg.salam.common.model.HumanComputingElement;
import at.ac.tuwien.dsg.salam.common.model.Service;
import at.ac.tuwien.dsg.salam.common.sla.Objective;
import at.ac.tuwien.dsg.salam.common.sla.Specification;

public class Discoverer implements DiscovererInterface {

    private ServiceManagerInterface manager;

    // cache
    private static List<Service> serviceCache = null; 

    public Discoverer(ServiceManagerInterface manager) {
        this.manager = manager;        
    }

    public List<Service> discoverServices(Functionality functionality,
            Specification specification) {
        return discoverServices(functionality, specification, 0, 0, 0);
    }

    public List<Service> discoverServices(Functionality functionality,
            Specification specification,
            double timeStart, double load, double deadline) {   

        serviceCache = (List<Service>) manager.retrieveServices(functionality);

        List<Service> services = new ArrayList<Service>();

        for (Service service: serviceCache) {
            
            boolean match = true;
            
            for (Objective objective: specification.getObjectives()) {
                switch (objective.getType()) {
                case STATIC:
                    match = isPropertyMatched(service, objective);
                    break;
                case METRIC:
                    match = isMetricMatched(service, objective);
                    break;
                case SKILL:
                    match = isSkillMatched(service, objective);
                    break;
                default:
                    break;
                }
                if (!match) break;
            }
        
            // check response time
            if (match && deadline>0) {
                Double estimatedResponseTime = (Double) service.getMetric(
                                "response_time", new Object[]{timeStart, load});
                if (estimatedResponseTime!=null) {
                    if (estimatedResponseTime > deadline) {
                        // deadline will be violated if we assign to this service
                        match = false;
                    }
                } else {
                    match = false;
                }
                
            }

            if (match) services.add(service);
        }

        return services;
    }
    
    private boolean isPropertyMatched(Service service, Objective objective) {
        // for now we only consider skill for filtering services
        return true;
    }
    
    private boolean isMetricMatched(Service service, Objective objective) {
        // for now we only consider skill for filtering services
        return true;
    }

    private boolean isSkillMatched(Service service, Objective objective) {

        boolean match = true;
        
        MembershipFunction function = SkillMembershipFunctionCollection
                .getInstance()
                .getMembershipFunction((String) objective.getValue());
        double lowerBound = function.lowerBound();
        double upperBound = function.upperBound();
        HumanComputingElement element = (HumanComputingElement)service.getProvider();
        Double skillScore = (Double) element.getSkills().getValue(objective.getName());
        if (skillScore==null || (skillScore<=lowerBound || skillScore>=upperBound)) {
            match = false;
        }

        return match;
    }


}
