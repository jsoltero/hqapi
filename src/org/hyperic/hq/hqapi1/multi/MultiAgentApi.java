package org.hyperic.hq.hqapi1.multi;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.AgentApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.AgentResponse;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;

public class MultiAgentApi {

    private Map<String, AgentApi> _agentApiMap;
    
    public MultiAgentApi(Collection<HQApi> hqApis) {
        _agentApiMap = new HashMap<String, AgentApi>();
        for(HQApi anApi : hqApis) {
            _agentApiMap.put(anApi.getHost(), anApi.getAgentApi());
        }
    }
    
    public void addHQApi(HQApi anApi) {
        _agentApiMap.put(anApi.getHost(), anApi.getAgentApi());
    }
    
    public void removeHQApi(HQApi anApi) {
        _agentApiMap.remove(anApi.getHost());
    }
    
    public AgentResponse getAgent(String address, int port)
        throws IOException
    {
        AgentResponse resp = null;
        for(AgentApi api : _agentApiMap.values()) {
            resp = api.getAgent(address, port);
            if(resp.getStatus().equals(ResponseStatus.SUCCESS)) {
                return resp;
            }
        }
        return resp;
    }
    
    /**
     * Get all agents
     * 
     */
    public MultiAgentsResponse getAgents() throws IOException {
        MultiAgentsResponse mar = new MultiAgentsResponse();
        for(String host : _agentApiMap.keySet()) {
            mar.addResponse(host, _agentApiMap.get(host).getAgents());
        }
        return mar;
    }
    
    /**
     * Ping an agent on a specified HQ host
     */
    public PingAgentResponse pingAgent(String host, Agent anAgent)
        throws IOException {
        return _agentApiMap.get(host).pingAgent(anAgent);
    }

}
