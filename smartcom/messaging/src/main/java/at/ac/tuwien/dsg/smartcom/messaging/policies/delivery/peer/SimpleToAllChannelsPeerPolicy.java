package at.ac.tuwien.dsg.smartcom.messaging.policies.delivery.peer;

import at.ac.tuwien.dsg.smartcom.exception.DeliveryPolicyFailedException;
import at.ac.tuwien.dsg.smartcom.messaging.policies.delivery.AbstractDeliveryPolicy;
import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;

public class SimpleToAllChannelsPeerPolicy extends AbstractDeliveryPolicy {
	
	
	public SimpleToAllChannelsPeerPolicy(){
		super("SimpleToAllChannelsPeerPolicy");
	}
	
	public boolean check(int whatToCheck) throws DeliveryPolicyFailedException {
        if (DeliveryPolicy.CHECK_ERR == whatToCheck) {
            throw new DeliveryPolicyFailedException();
        }
		return true; //TODO this is not complete...
	}
}