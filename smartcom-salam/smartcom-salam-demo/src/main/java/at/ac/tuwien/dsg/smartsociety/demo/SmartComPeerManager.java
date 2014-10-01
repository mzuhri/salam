package at.ac.tuwien.dsg.smartsociety.demo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import at.ac.tuwien.dsg.salam.cloud.manager.ServiceManagerOnMemory;
import at.ac.tuwien.dsg.salam.common.model.Assignment;
import at.ac.tuwien.dsg.salam.common.model.ComputingElement;
import at.ac.tuwien.dsg.smartcom.callback.CollectiveInfoCallback;
import at.ac.tuwien.dsg.smartcom.callback.PeerAuthenticationCallback;
import at.ac.tuwien.dsg.smartcom.callback.PeerInfoCallback;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchCollectiveException;
import at.ac.tuwien.dsg.smartcom.callback.exception.NoSuchPeerException;
import at.ac.tuwien.dsg.smartcom.callback.exception.PeerAuthenticationException;
import at.ac.tuwien.dsg.smartcom.model.CollectiveInfo;
import at.ac.tuwien.dsg.smartcom.model.DeliveryPolicy;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.PeerInfo;

public class SmartComPeerManager extends ServiceManagerOnMemory 
	implements PeerAuthenticationCallback, PeerInfoCallback, CollectiveInfoCallback{

	protected static String PEER_INFO_KEY = "peer_info";

	protected Hashtable<Long, CollectiveInfo> collectiveCache = new Hashtable<Long, CollectiveInfo>();
    protected long lastCollectiveId = 0;

	@Override
	public CollectiveInfo getCollectiveInfo(Identifier collective)
			throws NoSuchCollectiveException {
		long id = Long.parseLong(collective.getId());
		return collectiveCache.get(id);
	}

	@Override
	public PeerInfo getPeerInfo(Identifier id) throws NoSuchPeerException {
		long elementId = Long.parseLong(id.getId());
		ComputingElement element = retrieveElement(elementId);
		if (element==null) return null;
		else {
			PeerInfo info = (PeerInfo)element.getProperties().getValue(PEER_INFO_KEY);
			return info;
		}
	}

	@Override
	public boolean authenticate(Identifier peerId, String password)
			throws PeerAuthenticationException {
		return true;
	}
	
	public void setPeerInfo(long id, PeerInfo info) {
		ComputingElement element = retrieveElement(id);
		if (element!=null) {
			element.getProperties().setValue(PEER_INFO_KEY, info);
		}
	}
	
	public Identifier registerCollective(List<Assignment> assignments, DeliveryPolicy.Collective deliveryPolicy) {
		if (assignments==null || assignments.size()==0) return null;
		List<Long> added = new ArrayList<Long>();
		List<Identifier> peers = new ArrayList<Identifier>();
		for (Assignment assignment: assignments) {
			ComputingElement provider = assignment.getAssignee().getProvider();
			if (!added.contains(provider.getId())) {
				added.add(provider.getId());
				peers.add(Identifier.peer(Long.toString(provider.getId())));
			}
		}
		long collectiveId = lastCollectiveId++;
		Identifier id = Identifier.collective(Long.toString(collectiveId));
		CollectiveInfo collective = new CollectiveInfo(id, peers, deliveryPolicy);
		collectiveCache.put(collectiveId, collective);
		return id;
	}

}
