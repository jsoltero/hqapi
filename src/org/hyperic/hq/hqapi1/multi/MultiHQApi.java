package org.hyperic.hq.hqapi1.multi;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.hqapi1.HQApi;

/**
 * This is the main entry point to the Multi-HQ API
 * which spans more than one HQ instance.
 * 
 * Currently single threaded model for calling a list of
 * HQApi objects sequentially and assembling requests in 
 * response objects that contain maps of <host, response> entries.
 * 
 *
 */
public class MultiHQApi {
	
	private Map<String, HQApi> _apiMap;
	
	private final MultiEventApi _multiEventApi;
	private final MultiResourceApi _multiResourceApi;
	private final MultiAlertApi _multiAlertApi;
	private final MultiAgentApi _multiAgentApi;
	
	private Log _log = LogFactory.getLog(MultiHQApi.class);
	
	/**
	 * Constructor for MultiHQApi. 
	 * Any HQApi that fails a basic connection test at time
	 * of construction will not be used.
	 * @param apiList
	 */
	public MultiHQApi(List<HQApi> apiList) {
		_apiMap = new HashMap<String, HQApi>();
		for(HQApi anApi : apiList) {
		    if(anApi.testConnection()) {
                _apiMap.put(anApi.getHost(), anApi);
            } else {
                _log.error("Failed to connect to: " + anApi.getHost() 
                        + ". Skipping this server.");
            }
		}
		_multiEventApi = new MultiEventApi(_apiMap.values());
		_multiResourceApi = new MultiResourceApi(_apiMap.values());
		_multiAlertApi = new MultiAlertApi(_apiMap.values());
		_multiAgentApi = new MultiAgentApi(_apiMap.values());
	}
	
	public void addHQApi(HQApi anApi) {
		_apiMap.put(anApi.getHost(), anApi);
		_multiEventApi.addHQApi(anApi);
		_multiResourceApi.addHQApi(anApi);
	}
	
	public void removeHQApi(HQApi anApi) {
		_apiMap.remove(anApi.getHost());
		_multiEventApi.removeHQApi(anApi);
		_multiResourceApi.removeHQApi(anApi);
	}
	
	public MultiEventApi getMultiEventApi() {
		return _multiEventApi;
	}
	
	public MultiResourceApi getMultiResourceApi() {
	    return _multiResourceApi;
	}
	
	public MultiAlertApi getMultiAlertApi() {
	    return _multiAlertApi;
	}
	
	public MultiAgentApi getMultiAgentApi() {
	    return _multiAgentApi;
	}
	
	public Collection<String> getHosts() {
		return _apiMap.keySet();
	}
	
	public HQApi getHQApi(String host) {
	    return _apiMap.get(host);
	}
	
	public int getHostCount() {
	    return _apiMap.keySet().size();
	}
	
}
