package at.ac.tuwien.dsg.smartsociety.demo.rest.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.smartsociety.demo.rest.exceptions.AlreadyExistsException;
import at.ac.tuwien.dsg.smartsociety.demo.rest.exceptions.NotFoundException;
import at.ac.tuwien.dsg.smartsociety.demo.rest.resource.Peer;

@Service
public class PeerService {
	private final ConcurrentMap<String, Peer> peers = new ConcurrentHashMap<String, Peer>(); 
	
	public Collection<Peer> getPeers(int page, int pageSize) {
		final Collection<Peer> slice = new ArrayList<Peer>( pageSize );
		
        final Iterator<Peer> iterator = peers.values().iterator();
        for( int i = 0; slice.size() < pageSize && iterator.hasNext(); ) {
        	if( ++i > ( ( page - 1 ) * pageSize ) ) {
        		slice.add(iterator.next());
        	}
        }
		
		return slice;
	}
	
	public Peer getByEmail(String email) {
		final Peer peer = peers.get(email);
		
		if(peer == null) {
			throw new NotFoundException();
		}
		
		return peer;
	}

	public Peer addPeer(String email, String name) {
		final Peer peer = new Peer(email);
		peer.setName(name);
				
		if( peers.putIfAbsent(email, peer) != null ) {
			throw new AlreadyExistsException();
		}
		
		return peer;
	}
	
	public void removePeer(String email ) {
		if( peers.remove(email) == null ) {
			throw new NotFoundException();
		}
	}
}
