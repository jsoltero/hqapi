package org.hyperic.hq.hqapi1.test;

import java.util.ArrayList;
import java.util.List;

import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.multi.MultiHQApi;

public abstract class MultiHQApiTestBase extends HQApiTestBase {
    
    private MultiHQApi myMultiApi;
    
    public MultiHQApiTestBase(String name) {
        super(name);
    }
    
    protected HQApi getBadHQApi() {
        return new HQApi("somebadhost.com", 7080, false, "demo", "demo");
    }
    
    protected List<HQApi> getTestHQApis() {
        List<HQApi> apis = new ArrayList<HQApi>();
        apis.add(new HQApi("10.2.0.227", 7080, false, "hqadmin", "hqadmin"));
        apis.add(new HQApi("localhost", 7080, false, "hqadmin", "hqadmin"));
        return apis;
    }

    protected MultiHQApi getMultiHQApi() {
        if(myMultiApi == null) {
            myMultiApi = new MultiHQApi(getTestHQApis());
        }
        return myMultiApi;
    }
    
    
    
}
