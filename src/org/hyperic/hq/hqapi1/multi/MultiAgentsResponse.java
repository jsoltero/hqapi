package org.hyperic.hq.hqapi1.multi;

import java.util.ArrayList;
import java.util.List;

import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.AgentsResponse;
import org.hyperic.hq.hqapi1.types.Response;

public class MultiAgentsResponse extends MultiResponse {

    
    public MultiAgentsResponse() {
        super();
    }
    
    public List<Agent> getAllAgents() {
        List<Agent> aList = new ArrayList<Agent>();
        for(Response resp : _multiMap.values()) {
            aList.addAll(((AgentsResponse)resp).getAgent());
        }
        return aList;
    }
    
    public List<Agent> getAgentsByHost(String host) {
        List<Agent> aList = new ArrayList<Agent>();
        AgentsResponse r = (AgentsResponse)_multiMap.get(host);
        aList.addAll(r.getAgent());
        return aList;
    }
 
    public List<String> getHostsForAgent(Agent anAgent) {
        List<String> agList = new ArrayList<String>();
        for(String host : _multiMap.keySet()) {
            if(((AgentsResponse)_multiMap.get(host))
                    .getAgent().contains(anAgent)) {
                agList.add(host);
            }
        }
        return agList;
    }
    
}
