package at.ac.tuwien.dsg.smartsociety.demo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import at.ac.tuwien.dsg.salam.cloud.discoverer.Discoverer;
import at.ac.tuwien.dsg.salam.cloud.generator.ServiceGenerator;
import at.ac.tuwien.dsg.salam.cloud.scheduler.DependencyProcessor;
import at.ac.tuwien.dsg.salam.common.interfaces.ComposerInterface;
import at.ac.tuwien.dsg.salam.common.interfaces.DependencyProcessorInterface;
import at.ac.tuwien.dsg.salam.common.interfaces.DiscovererInterface;
import at.ac.tuwien.dsg.salam.common.model.Assignment;
import at.ac.tuwien.dsg.salam.common.model.ComputingElement;
import at.ac.tuwien.dsg.salam.common.model.Functionality;
import at.ac.tuwien.dsg.salam.common.model.Role;
import at.ac.tuwien.dsg.salam.common.model.Service;
import at.ac.tuwien.dsg.salam.common.model.Task;
import at.ac.tuwien.dsg.salam.common.sla.Specification;
import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.util.ConfigJson;
import at.ac.tuwien.dsg.salam.util.Util;
import at.ac.tuwien.dsg.smartcom.Communication;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import at.ac.tuwien.dsg.smartcom.adapters.EmailInputAdapter;
import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.model.PeerChannelAddress;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;
import at.ac.tuwien.dsg.smartcom.utils.PropertiesLoader;

public class RunDemo {

	private static String configFile = "./config/demo.properties";
	
	private static SmartComPeerManager manager;
	private static ComposerInterface composer;
	private static Communication communication;
	
    public static void main(String[] args) {
    	
        try {

        	// config
			ConfigJson svcConfig = new ConfigJson(Util.getProperty(configFile, "service_generator_config"));
			String composerConfigFile = Util.getProperty(configFile, "composer_config");
			
            // init components
			manager = new SmartComPeerManager();
            DiscovererInterface discoverer = new Discoverer(manager);
            DependencyProcessorInterface dp = new DependencyProcessor();
            composer = new Composer(composerConfigFile, manager, discoverer, dp);
            
            // init smartcom
            SmartCom smartCom = new SmartCom(manager, manager, manager);
            smartCom.initializeSmartCom();
            communication = smartCom.getCommunication();
            communication.registerNotificationCallback(new NotificationHandler());
	    	
	    	// generate services
	    	generateServices(svcConfig);
	    	
	    	// create task
	    	Task task = createTask();
	    	System.out.println("Task " + task + " created: " + task.detail());
	    	
	    	// handle
	    	handleTask(task);
	    	
	    	//smartCom.tearDownSmartCom();

        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}

    }
    
    public static void generateServices(ConfigJson svcConfig) {
    	
    	try {
        	
    		// generate services
    		ServiceGenerator serviceGen = new ServiceGenerator(svcConfig);
			ArrayList<Service> services = serviceGen.generate();
			
			// register services
			for (Service service: services) {
				manager.registerService(service);
			}
			
			// create a peer info for each elements
            for (ComputingElement element: manager.retrieveElements()) {
            	List<PeerChannelAddress> addresses = new ArrayList<>();
            	// add email channel 
            	String email = "mzuhri+" + element.getId() + "@gmail.com";
            	//String email = "mzuhri@gmail.com";
                List<Serializable> parameters = new ArrayList<>(1);
                parameters.add(email);
                Identifier id = Identifier.peer(Long.toString(element.getId()));
                PeerChannelAddress address = new PeerChannelAddress(id, Identifier.channelType("Email"), parameters);
                addresses.add(address);
                // create peerinfo
                PeerInfo info = new PeerInfo(id, DeliveryPolicy.Peer.TO_ALL_CHANNELS, null, addresses);
                manager.setPeerInfo(element.getId(), info);
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    public static Task createTask() {
    	
    	// create task
    	Task task = new Task("Test Task", "Task description", 1.0, null);
    	
    	// add roles with empty spec
    	task.addUpdateRole(new Role(new Functionality("S1"), new Specification()));
    	task.addUpdateRole(new Role(new Functionality("S2"), new Specification()));
    	task.addUpdateRole(new Role(new Functionality("S3"), new Specification()));
    	
    	// set task spec, an empty one
    	Specification spec = new Specification();
    	task.setSpecification(spec);
    	
    	return task;
    }
    
    public static void handleTask(Task task) {
    	
        try {
	    	// compose collective
	    	List<Assignment> assignments = composer.compose(task);
	    	
	    	// register
	    	Identifier collectiveIdentifier = manager.registerCollective(assignments, DeliveryPolicy.Collective.TO_ALL_MEMBERS);
	    	
	    	// dump
	    	System.out.println("Collective created for task " + task);
	    	for (Assignment assignment: assignments) {
	    		System.out.println(assignment.getAssignee());
	    		//System.out.println(assignment.getAssignee().get);
	    	}
	    	
	    	// build message for the collective
	        Message.MessageBuilder builder = new Message.MessageBuilder()
		        .setType("TASK")
		        .setSubtype("")
		        .setReceiverId(collectiveIdentifier)
		        .setSenderId(Identifier.component("DEMO"))
		        .setConversationId(System.nanoTime() + "")
		        .setContent("You got the following task: " + task.detail());
	        Message msg = builder.create();
	    	
	    	// create input adapter using email
	        EmailInputAdapter input = new EmailInputAdapter(msg.getConversationId(),
                    PropertiesLoader.getProperty("EmailAdapter.properties", "hostIncoming"),
                    PropertiesLoader.getProperty("EmailAdapter.properties", "username"),
                    PropertiesLoader.getProperty("EmailAdapter.properties", "password"),
                    Integer.valueOf(PropertiesLoader.getProperty("EmailAdapter.properties", "portIncoming")),
                    true, "test", "test", true);
            communication.addPullAdapter(input, 1000, true);

            // send
			communication.send(msg);
			
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
    }
    
    private static class NotificationHandler implements NotificationCallback {

        @Override
        public void notify(Message message) {
            StringBuilder builder = new StringBuilder();
            builder.append("################ New Message received ################").append("\n");
            builder.append(message.toString()).append("\n");
            builder.append("################                      ################");
            //System.out.println("New Message received: "+message.getContent());
            System.out.println(builder.toString());
        }
    }

}
