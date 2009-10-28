package org.hyperic.hq.hqapi1.multi;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.EventApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.EventApi.EventStatus;
import org.hyperic.hq.hqapi1.EventApi.EventType;
import org.hyperic.hq.hqapi1.types.EventsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;

/**
 * Provides access to events across multiple HQ servers
 * Most public methods return instances of MultiResponse objects
 * which serve as a map of responses of different types which 
 * have the originating HQ Server as the key.
 * 
 */
public class MultiEventApi {
	
	private Map<String, EventApi> _eventApiMap;
	private MultiResourceApi _multiResourceApi;
	
	public MultiEventApi(Collection<HQApi> hqApis) {
		_eventApiMap = new HashMap<String, EventApi>();
		for(HQApi anApi : hqApis) {
			_eventApiMap.put(anApi.getHost(), anApi.getEventApi());
			
		}
		_multiResourceApi = new MultiResourceApi(hqApis);
	}
	
	public void addHQApi(HQApi anApi) {
	    _eventApiMap.put(anApi.getHost(), anApi.getEventApi());
	    _multiResourceApi.addHQApi(anApi);
	}
	
	public void removeHQApi(HQApi anApi) {
	    _eventApiMap.remove(anApi.getHost());
	    _multiResourceApi.removeHQApi(anApi);
	}
	
    /**
     * Find {@link org.hyperic.hq.hqapi1.types.Event}s in multiple HQ.
     *
     * @param begin The beginning of the time window in epoch-millis.
     * @param end The end of the time window in epoch-millis.
     * @param type The type of event to search for, or null for all types.
     * @param status The maximum status to include in the search results.
     * @param count The maximum number of Events to return.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of Events are returned via
     * {@link org.hyperic.hq.hqapi1.types.EventsResponse#getEvent()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public MultiEventsResponse findEvents(long begin, long end,
                                          EventType type, EventStatus status,
                                          int count)
        throws IOException
    {
    	MultiEventsResponse resp = new MultiEventsResponse();
    	for(String host : _eventApiMap.keySet()) {
    		EventsResponse evResp = _eventApiMap.get(host).findEvents(begin, end, type, status, count);
    		resp.addResponse(host , evResp);
    	}
    	return resp;
    }
    
    /**
     * Find {@link org.hyperic.hq.hqapi1.types.Event}s in multiple HQ and
     * whose source matches a specific ResourcePrototype.
     *
     * @param begin The beginning of the time window in epoch-millis.
     * @param end The end of the time window in epoch-millis.
     * @param type The type of event to search for, or null for all types.
     * @param status The maximum status to include in the search results.
     * @param type The ResourcePrototype of the source of the events
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * a list of Events are returned via
     * {@link org.hyperic.hq.hqapi1.types.EventsResponse#getEvent()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */    
    public MultiEventsResponse findEvents(ResourcePrototype type, 
                                          long begin, long end, 
                                          EventType evType, EventStatus evStatus)
        throws IOException 
    {
        MultiEventsResponse resp = new MultiEventsResponse();
        for(String host : _eventApiMap.keySet()) {
            MultiResourcesResponse mrr = _multiResourceApi.getResources(type, false, false);
            for(Resource r : mrr.getAllResources()) {
                EventsResponse evResp = _eventApiMap.get(host).findEvents(r, begin, end);
                resp.addResponse(host, evResp);
            }
        }
        return resp;
    }
}
