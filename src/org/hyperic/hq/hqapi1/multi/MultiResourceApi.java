package org.hyperic.hq.hqapi1.multi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypesResponse;
import org.hyperic.hq.hqapi1.types.ResourceResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;

public class MultiResourceApi {
	
	private Map<String, ResourceApi> _resourceApiMap;
	
	public MultiResourceApi(Collection<HQApi> apis) {
		_resourceApiMap = new HashMap<String, ResourceApi>();
		for(HQApi anApi : apis) {
			_resourceApiMap.put(anApi.getHost(), anApi.getResourceApi());
		}
	}
	
	public void addHQApi(HQApi anApi) {
	    _resourceApiMap.put(anApi.getHost(), anApi.getResourceApi());
	}
	
	public void removeHQApi(HQApi anApi) {
	    _resourceApiMap.remove(anApi.getHost());
	}
	
	public MultiResourcesResponse getResources(ResourcePrototype pt, 
	                                           boolean verbose, boolean children) 
		throws IOException {
		
		MultiResourcesResponse resp = new MultiResourcesResponse();
		for(String host : _resourceApiMap.keySet()) {
		    resp.addResponse(host, _resourceApiMap.get(host).getResources(pt, verbose, children));
		}
		return resp;
	}
	
	/**
	 * Get ResourcePrototypes supported by ALL available HQ servers
	 * 
	 * @return List does not include prototypes not found on all servers
	 * @throws IOException 
	 */
	public List<ResourcePrototype> getAllResourcePrototypes() throws IOException {
		List<ResourcePrototype> types = new ArrayList<ResourcePrototype>();
		List<String> typeNames = new ArrayList<String>();
		for(String host: _resourceApiMap.keySet()) {
			ResourcePrototypesResponse typeResp = _resourceApiMap.get(host).getAllResourcePrototypes();
			for(ResourcePrototype aType : typeResp.getResourcePrototype()) {
			    if(!typeNames.contains(aType.getName())) {
			        typeNames.add(aType.getName());
			        types.add(aType);
			    }
			}
			
		}
		return types;
	}

	/**
     * Get ResourcePrototypes supported by ALL available HQ servers that have 
     * at least 1 resource in inventory
     * 
     * @return List does not include prototypes not found on all servers
     * @throws IOException 
     */
    public List<ResourcePrototype> getResourcePrototypes() throws IOException {
        List<ResourcePrototype> types = new ArrayList<ResourcePrototype>();
        List<String> typeNames = new ArrayList<String>();
        for(String host: _resourceApiMap.keySet()) {
            ResourcePrototypesResponse typeResp = _resourceApiMap.get(host).getResourcePrototypes();
            for(ResourcePrototype aType : typeResp.getResourcePrototype()) {
                if(!typeNames.contains(aType.getName())) {
                    typeNames.add(aType.getName());
                    types.add(aType);
                }
            }
            
        }
        return types;
    }
	
	/**
	 * Get resources of a specified type name. 
	 * @param prototypeName
	 * @param verbose
	 * @param children
	 * @return
	 * @throws IOException
	 */
	public MultiResourcesResponse getResources(String prototypeName, 
	                                           boolean verbose, 
	                                           boolean children) 
	    throws IOException {
	    
	    MultiResourcesResponse resp = new MultiResourcesResponse();
	    for(String host : _resourceApiMap.keySet()) {
	        ResourceApi api = _resourceApiMap.get(host);
	        ResourcePrototypeResponse typeResp = api.getResourcePrototype(prototypeName);
	        if(typeResp.getStatus().equals(ResponseStatus.SUCCESS)) {
	            resp.addResponse(host, api.getResources(typeResp.getResourcePrototype(),
	                                                    verbose, 
	                                                    children));
	        }
	    }
	    return resp;
	}
	
	public ResourceResponse getResource(String host, int id)
	    throws IOException {
	    return getResource(host, id, false, false);
	}
	
	public ResourceResponse getResource(String host, int id, boolean verbose, boolean children) 
	    throws IOException {
	    return _resourceApiMap.get(host).getResource(id, verbose, children);
	}
	
}
