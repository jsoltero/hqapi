package org.hyperic.hq.hqapi1.test;

import java.util.List;

import org.hyperic.hq.hqapi1.multi.MultiResourceApi;
import org.hyperic.hq.hqapi1.multi.MultiResourcesResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResourceResponse;

public class MultiResourcesFind_test extends MultiHQApiTestBase {

    MultiResourceApi multiRes;
    
    public MultiResourcesFind_test(String name) {
        super(name);
        multiRes = getMultiHQApi().getMultiResourceApi();
    }
    
    public void testValidFindPrototypes() throws Exception {
        List<ResourcePrototype> allTypes = multiRes.getAllResourcePrototypes();
        assertEquals(false, allTypes.isEmpty());
        List<ResourcePrototype> suppTypes = multiRes.getResourcePrototypes();
        assertTrue(allTypes.size() > suppTypes.size());
        boolean found = false;
        for(ResourcePrototype t : allTypes) {
            if(t.getName().equals("CPU")) {
                found = true;
            }
        }
        assertTrue(found);
    }
    
    public void testValidFindResource() throws Exception {
        
        MultiResourcesResponse mrr = multiRes.getResources("JBoss 4.2", true, true);
        assertTrue(mrr.getAllResources().size() >= 1);
        ResourcePrototype jboss = null;
        for(ResourcePrototype type : multiRes.getResourcePrototypes()) {
            if(type.getName().equals("JBoss 4.2")) {
                jboss = type;
            }
        }
        assertNotNull(jboss);
        List<Resource> jbossRes = mrr.getResources(jboss, HOST);
        assertTrue(jbossRes.size() >= 1);
        //now lets find one of them again
        assertNotNull(jbossRes.get(0)); 
        ResourceResponse findResp = multiRes.getResource(HOST, jbossRes.get(0).getId());
        // check that they are of the same type
        assertEquals(findResp.getResource().getResourcePrototype().getName(), jboss.getName());
        
    }
    

}
