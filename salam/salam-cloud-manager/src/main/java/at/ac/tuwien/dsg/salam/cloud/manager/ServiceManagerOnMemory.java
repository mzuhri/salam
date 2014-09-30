package at.ac.tuwien.dsg.salam.cloud.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import at.ac.tuwien.dsg.salam.cloud.generator.ServiceGenerator;
import at.ac.tuwien.dsg.salam.common.exceptions.NotFoundException;
import at.ac.tuwien.dsg.salam.common.interfaces.ServiceManagerInterface;
import at.ac.tuwien.dsg.salam.common.model.ComputingElement;
import at.ac.tuwien.dsg.salam.common.model.Connection;
import at.ac.tuwien.dsg.salam.common.model.Functionality;
import at.ac.tuwien.dsg.salam.common.model.Service;
import at.ac.tuwien.dsg.salam.util.ConfigJson;

public class ServiceManagerOnMemory implements
        ServiceManagerInterface {
    
    protected Hashtable<Long, ComputingElement> elementCache;
    protected ArrayList<Service> serviceCache;
    protected long lastId;
    
    private ServiceManagerOnMemory _instance;

    public ServiceManagerOnMemory() {
        this.elementCache = new Hashtable<Long, ComputingElement>();
        this.serviceCache  = new ArrayList<Service>();
        this.lastId = -1;
    }

    @Override
    public ComputingElement createElement() {
        // find last id or next empty id
        ComputingElement cur;
        do {
            cur = elementCache.get(++lastId);
        } while (cur!=null);
        // create and add element
        ComputingElement element = new ComputingElement(lastId);
        element.setManager(this);
        elementCache.put(element.getId(), element);
        return element;
    }

    @Override
    public ComputingElement registerElement(ComputingElement element) {
        element.setManager(this);
        elementCache.put(element.getId(), element);
        return element;
    }

    @Override
    public ComputingElement retrieveElement(long id) {
        return elementCache.get(id);
    }

    @Override
    public void removeElement(ComputingElement element) throws NotFoundException {
        if (elementCache.get(element.getId())==null) {
            throw new NotFoundException();
        }
        elementCache.remove(element).getId();
    }

    @Override
    public Collection<ComputingElement> retrieveElements() {
        return elementCache.values();
    }

    @Override
    public Service registerService(Service service) {
        ComputingElement element = retrieveElement(service.getProvider().getId());
        if (element!=null) {
            element.addService(service);
        } 
        registerElement(service.getProvider());
        serviceCache.add(service);
        return service;
    }

    @Override
    public void removeService(Service service) throws NotFoundException {
        ComputingElement element = retrieveElement(service.getProvider().getId());
        if (element!=null) {
            registerElement(element);
            element.removeService(service.getFunctionality());
        } 
        serviceCache.remove(service);
    }
    
    @Override
    public List<Service> retrieveServices(Functionality functionality) {
        List<Service> services = new ArrayList<Service>();
        for (Service s: serviceCache) {
            if (s.getFunctionality().equals(functionality)) {
                services.add(s);
            }
        }
        return services;
    }

    public void generate(ConfigJson genConfig) {
        try {
            // generate services
            ServiceGenerator svcGen = new ServiceGenerator(genConfig);
            ArrayList<Service> services;
            services = svcGen.generate();
            // save
            for (Service service : services) {
                registerService(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Connection> getConnections(List<Service> services) {
        
        ArrayList<Connection> connections = new ArrayList<Connection>();

        // get a list of computing elements serving the services
        ArrayList<ComputingElement> elements = new ArrayList<ComputingElement>();
        ArrayList<Long> added = new ArrayList<Long>();
        for (Service s: services) {
            if (added.indexOf(s.getProvider().getId())==-1) {
                elements.add(s.getProvider());
                added.add(s.getProvider().getId());
            }
        }
        
        // special case for single worker
        if (elements.size()==1) {
          connections.add(new Connection(elements.get(0)));
        }
          
        // iterate to get the connections, assuming that the connection is undirectional
        for (int i=0; i<elements.size(); i++) {
            ComputingElement e1 = elements.get(i);
            for (int j=i+1; j<elements.size(); j++) {
                ComputingElement e2 = elements.get(j);
                Connection c = e1.getConnection(e2);
                if (c!=null) connections.add(c);
          }
        }
        return connections;
    }

    @Override
    public ServiceManagerInterface getInstance() {
        if (_instance==null) _instance = new ServiceManagerOnMemory();
        return _instance;
    }

}
