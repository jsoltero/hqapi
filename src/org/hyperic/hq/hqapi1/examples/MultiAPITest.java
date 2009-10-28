package org.hyperic.hq.hqapi1.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.EventApi.EventStatus;
import org.hyperic.hq.hqapi1.EventApi.EventType;
import org.hyperic.hq.hqapi1.multi.MultiAgentApi;
import org.hyperic.hq.hqapi1.multi.MultiAgentsResponse;
import org.hyperic.hq.hqapi1.multi.MultiAlertApi;
import org.hyperic.hq.hqapi1.multi.MultiAlertsResponse;
import org.hyperic.hq.hqapi1.multi.MultiEventApi;
import org.hyperic.hq.hqapi1.multi.MultiEventsResponse;
import org.hyperic.hq.hqapi1.multi.MultiHQApi;
import org.hyperic.hq.hqapi1.multi.MultiResourceApi;
import org.hyperic.hq.hqapi1.multi.MultiResourcesResponse;
import org.hyperic.hq.hqapi1.types.Agent;
import org.hyperic.hq.hqapi1.types.AgentResponse;
import org.hyperic.hq.hqapi1.types.Event;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourceResponse;

public class MultiAPITest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MultiHQApi multiHQ = new MultiHQApi(getTestApis());
		MultiEventApi multiEvent = multiHQ.getMultiEventApi();
		MultiAlertApi multiAlert = multiHQ.getMultiAlertApi();
		MultiAgentApi multiAgent = multiHQ.getMultiAgentApi();
		
		MultiEventsResponse events;
		try {
			assert(multiEvent != null);
			System.out.println("Getting first 100 Events");
			// get first 100 events
			events = multiEvent.findEvents(0, 
					System.currentTimeMillis(), EventType.ANY, EventStatus.ANY, 100);
			for(Event ev : events.getAllEvents()) {
				System.out.println("Here's one - Date: " + new Date(ev.getCtime()) 
						+ " Host: " + events.getHostForEvent(ev) 
				        + " Details: " + ev.getDetail());
			}
			assert(!events.getAllEvents().isEmpty());
			System.out.println("Got events from: " + events.getHosts().size() + " HQApi host(s)");
			// get 100 alerts in the last hour
			System.out.println("Getting 100 Alerts");
			events = multiEvent.findEvents(0, System.currentTimeMillis(), 
										   EventType.ALERT, 
										   EventStatus.ANY, 
										   100);
			for(Event ev : events.getAllEvents()) {
				System.out.println("Here's one from: " + events.getHostForEvent(ev) 
				        + " : " + ev.getDetail());	
			}
			System.out.println("Got alerts from: " + events.getHosts().size() + " HQApi hosts");
			assert(!events.getAllEvents().isEmpty());
			
			// Get all resource prototypes
			MultiResourceApi multiResource = multiHQ.getMultiResourceApi();
			System.out.println("Getting prototype list");
			for(ResourcePrototype pt : multiResource.getAllResourcePrototypes()) {
			    System.out.println("Got a prototype: " + pt.getName());
			}
			
			// Get  resource prototypes for existing inventory 
            System.out.println("Getting existing inventory prototype list");
            for(ResourcePrototype pt : multiResource.getResourcePrototypes()) {
                System.out.println("Got a prototype: " + pt.getName());
            }
            
			
			System.out.println("Getting all resources");
			
			
			
			MultiResourcesResponse mrr = multiResource.getResources("JBoss 4.2", 
			                                                       true, 
			                                                       false);
			for(Resource r : mrr.getAllResources()) {
			    System.out.println("JBoss 4.2 Resource from: " + mrr.getHostForResource(r)
			            + " : " + r);    
			    
			}
			
			ResourcePrototype jboss = null;
			for(ResourcePrototype type : multiResource.getResourcePrototypes()) {
			    if(type.getName().equals("JBoss 4.2")) {
			        jboss = type;
			    }
			}
			mrr = multiResource.getResources(jboss, true, false);
			System.out.println("Found: " + mrr.getAllResources().size());
			
		  ResourceResponse findResp = multiResource.getResource("localhost", mrr.getAllResources().get(0).getId());
		  System.out.println("Founs? " + findResp.getResource());
		  
			
			
			
			System.out.println("Getting alerts");
			MultiAlertsResponse mar = multiAlert.findAlerts(0, System.currentTimeMillis(), 100, 1, false, true);
			System.out.println("Found: " + mar.getAllAlerts().size());
			
			// Lookup Tomcat 6.0
			for(ResourcePrototype type : multiResource.getResourcePrototypes()) {
			    if(type.getName().equals("Tomcat 6.0")) {
			        mar = multiAlert.findAlerts(type, 0, System.currentTimeMillis(), 100, 1, false, true);
			        System.out.println("Found: " + mar.getAllAlerts().size() + " alerts for Tomcat 6.0");
			    }
			}
			
			AgentResponse agr = multiAgent.getAgent("localhost", 2144);
			System.out.println("Agent: " + agr.getAgent());
			
			MultiAgentsResponse magr = multiAgent.getAgents();
			System.out.println("Found: " + magr.getAllAgents().size() + " Agents.");
			
			for(Agent agent : magr.getAllAgents()) {
			    System.out.println("Pinging agent: " + agent.getAddress() 
			            + " : " + multiAgent.pingAgent(
			                    magr.getHostsForAgent(agent).get(0), agent).isUp());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static List<HQApi> getTestApis() {
		List<HQApi> apis = new ArrayList<HQApi>();
		apis.add(new HQApi("10.2.0.227", 7080, false, "hqadmin", "hqadmin"));
		apis.add(new HQApi("somebadhost.com", 7080, false, "springdemo", "javaone"));
		apis.add(new HQApi("localhost", 7080, false, "hqadmin", "hqadmin"));
		return apis;
	}

}
