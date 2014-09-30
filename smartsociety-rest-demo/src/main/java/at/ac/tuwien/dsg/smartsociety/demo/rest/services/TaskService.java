package at.ac.tuwien.dsg.smartsociety.demo.rest.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.smartsociety.demo.rest.exceptions.AlreadyExistsException;
import at.ac.tuwien.dsg.smartsociety.demo.rest.exceptions.NotFoundException;
import at.ac.tuwien.dsg.smartsociety.demo.rest.resource.Task;

@Service
public class TaskService {
	private final ConcurrentMap<Integer, Task> tasks = new ConcurrentHashMap<Integer, Task>(); 
	
	public Collection<Task> getTasks(int page, int pageSize) {
		final Collection<Task> slice = new ArrayList<Task>( pageSize );
		
        final Iterator<Task> iterator = tasks.values().iterator();
        for( int i = 0; slice.size() < pageSize && iterator.hasNext(); ) {
        	if( ++i > ( ( page - 1 ) * pageSize ) ) {
        		slice.add(iterator.next());
        	}
        }
		
		return slice;
	}
	
	public Task getById(Integer id) {
		final Task task = tasks.get(id);
		
		if(task == null) {
			throw new NotFoundException();
		}
		
		return task;
	}

	public Task addTask(Integer id, String title, String description) {
		final Task task = new Task(id, title, description);
				
		if( tasks.putIfAbsent(id, task) != null ) {
			throw new AlreadyExistsException();
		}
		
		return task;
	}
	
	public void removeTask(Integer id) {
		if( tasks.remove(id) == null ) {
			throw new NotFoundException();
		}
	}
}
