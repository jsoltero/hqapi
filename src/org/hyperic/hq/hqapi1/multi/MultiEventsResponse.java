package org.hyperic.hq.hqapi1.multi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hyperic.hq.hqapi1.EventApi.EventStatus;
import org.hyperic.hq.hqapi1.EventApi.EventType;
import org.hyperic.hq.hqapi1.types.Event;
import org.hyperic.hq.hqapi1.types.EventsResponse;
import org.hyperic.hq.hqapi1.types.Response;

public class MultiEventsResponse extends MultiResponse{
		
	public static final String EVENT_HOST_NOT_FOUND = "Event Host Not Found";
	
	public MultiEventsResponse() {
		super();
	}
	
	/**
	 * Get All events in descending order from their cTime
	 * @return
	 */
	public List<Event> getAllEvents() {
		List<Event> aList = new ArrayList<Event>();
		for(Response resp : _multiMap.values()) {
			aList.addAll(((EventsResponse)resp).getEvent());
		}
		Collections.sort(aList, new Comparator<Event>() {
			public int compare(Event e1, Event e2) {
				return (int) (e2.getCtime() - e1.getCtime());
			}
		});
		return aList;
	}
	
	/**
	 * Get the source API host for a given event instance
	 * @param aEvent
	 * @return
	 */
	public String getHostForEvent(Event aEvent) {
	    for(String host : _multiMap.keySet()) {
	        if(((EventsResponse)_multiMap.get(host)).getEvent().contains(aEvent)) {
	            return host;
	        }
	    }
	    return EVENT_HOST_NOT_FOUND;
	}
	
	/**
	 * Get events associated with a specific host
	 * @param host - HQApi host
	 * @return Event List
	 */
	public List<Event> getEventsByHost(String host) {
		List<Event> aList = new ArrayList<Event>();
		aList.addAll(((EventsResponse)_multiMap.get(host)).getEvent());
		return aList;
	}
	
	/**
	 * Get list of hosts whose events are in this response
	 * @return
	 */
	public List<String> getHosts() {
		return new ArrayList<String>(_multiMap.keySet());
	}
	
	/**
	 * get all events in a given range and type
	 */
	public List<Event> getEvents(long begin, long end,
                                 EventType type, EventStatus status) {
		List<Event> events = getAllEvents();
		for(Event e : events) {
			if(e.getCtime() < begin || e.getCtime() > end) {
			    events.remove(e);	
			}
			if(!e.getType().equals(type.getType())) {
				events.remove(e);
			}
		}
		return events;
	}
}
