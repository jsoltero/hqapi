package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EventApi.EventStatus;
import org.hyperic.hq.hqapi1.EventApi.EventType;
import org.hyperic.hq.hqapi1.multi.MultiEventApi;
import org.hyperic.hq.hqapi1.multi.MultiEventsResponse;

public class MultiEventsFind_test extends MultiHQApiTestBase {

    MultiEventApi multiEv;
    
    public MultiEventsFind_test(String name) {
        super(name);
        multiEv = getMultiHQApi().getMultiEventApi();
    }

    public void testValidFind() throws Exception {
        MultiEventsResponse mer = multiEv.findEvents(0, System.currentTimeMillis(), 
                                                     EventType.ANY, 
                                                     EventStatus.ANY, 100);
        assertEquals(false, mer.getAllEvents().isEmpty());
        assertEquals(false, mer.getEventsByHost("localhost").isEmpty());
        assertEquals(false, mer.getHosts().isEmpty());
    }
    
    public void testFindAlertEventType() throws Exception {
        MultiEventsResponse mer = multiEv.findEvents(0, System.currentTimeMillis(), 
                                                     EventType.ALERT, 
                                                     EventStatus.ANY, 100);
        assertEquals(false, mer.getAllEvents().isEmpty());
    }
    
    public void testInvalidFind() throws Exception {
        MultiEventsResponse mer = multiEv.findEvents(System.currentTimeMillis(), 
                                                     System.currentTimeMillis() + 1000000, 
                                                     EventType.ANY, EventStatus.ANY, 100);
        assertTrue(mer.getAllEvents().isEmpty());
    }
    
}
