package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.multi.MultiAgentApi;
import org.hyperic.hq.hqapi1.multi.MultiAgentsResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.AgentResponse;
import org.hyperic.hq.hqapi1.types.PingAgentResponse;

public class MultiAgentFind_test extends MultiHQApiTestBase {

    MultiAgentApi multiAgent;
    
    public MultiAgentFind_test(String name) {
        super(name);
        multiAgent = getMultiHQApi().getMultiAgentApi();
    }
    
    public void testValidFindAgents() throws Exception {
        AgentResponse agr = multiAgent.getAgent(HOST, 2144);
        assertNotNull(agr.getAgent());
        hqAssertSuccess(agr);
        
        MultiAgentsResponse magr = multiAgent.getAgents();
        hqAssertSuccess(magr);
    }
    
    public void testInvalidFindAgent() throws Exception {
        AgentResponse agr = multiAgent.getAgent("SomeOtherHost", 2144);
        hqAssertFailureObjectNotFound(agr);
    }
    
    public void testPingAgent() throws Exception {
        PingAgentResponse agr = multiAgent.pingAgent(HOST, getRunningAgent());
        hqAssertSuccess(agr);
    }
    
    public void testMultiPing() throws Exception {
        MultiAgentsResponse magr = multiAgent.getAgents();
        for(String host : magr.getHosts()) {
            for(Agent agent : magr.getAgentsByHost(host)) {
                PingAgentResponse agr = multiAgent.pingAgent(host, agent);
                hqAssertSuccess(agr);
            }
        }
    }
}
