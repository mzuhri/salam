package at.ac.tuwien.dsg.smartcom.messaging.policies.delivery.peer;

import at.ac.tuwien.dsg.smartcom.messaging.policies.delivery.AbstractDeliveryPolicy;
import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;

public class SimpleAtLeastOnePeerPolicy extends AbstractDeliveryPolicy {
	
	
	public SimpleAtLeastOnePeerPolicy(){
		super("SimpleAtLeastOnePeerPolicy");
	}

	
	public boolean check(int whatToCheck){
		return true;
	}
}
