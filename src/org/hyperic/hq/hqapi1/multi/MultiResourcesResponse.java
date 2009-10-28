package org.hyperic.hq.hqapi1.multi;

import java.util.ArrayList;
import java.util.List;

import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Response;

public class MultiResourcesResponse extends MultiResponse {
	
	
	public static final String RESOURCE_HOST_NOT_FOUND = "Resource Host Not Found";
	
	public MultiResourcesResponse() {
		super();
	}
	
	public List<Resource> getAllResources() {
		List<Resource> aList = new ArrayList<Resource>();
		for(Response resp : _multiMap.values()) {
			aList.addAll(((ResourcesResponse)resp).getResource());
		}
		return aList;
	}
	
	public List<Resource> getAllResources(ResourcePrototype pt) {
		List<Resource> aList = new ArrayList<Resource>();
		for(Response resp: _multiMap.values()) {
			for(Resource r : ((ResourcesResponse)resp).getResource()) {
			    if(r.getResourcePrototype().equals(pt)) {
			        aList.add(r);
			    }
			}
		}
		return aList;
	}
	
	public  List<Resource> getResources(ResourcePrototype type, String host) {
	    List<Resource> resList = new ArrayList<Resource>();
	    for(String aHost : _multiMap.keySet()) {
	        if(aHost.equals(host)) {
	            ResourcesResponse resp = (ResourcesResponse)_multiMap.get(host);
	            for(Resource r: resp.getResource()) {
	                if(r.getResourcePrototype().getName().equals(type.getName())) {
	                    resList.add(r);
	                }
	            }
	        }
	    }
	    return resList;
	}
	
    /**
     * Get the source API host for a given resource instance
     * @param aEvent
     * @return
     */
    public String getHostForResource(Resource aResource) {
        for(String host : _multiMap.keySet()) {
            if(((ResourcesResponse)_multiMap.get(host))
                    .getResource().contains(aResource)) {
                return host;
            }
        }
        return RESOURCE_HOST_NOT_FOUND;
    }
	
}
