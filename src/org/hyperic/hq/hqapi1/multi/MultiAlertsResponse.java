package org.hyperic.hq.hqapi1.multi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hyperic.hq.hqapi1.types.Alert;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Response;

public class MultiAlertsResponse extends MultiResponse {

    public static final String ALERTS_HOST_NOT_FOUND = "Alert Host Not Found";
    
    public MultiAlertsResponse() {
        super();
    }
    
    public List<Alert> getAllAlerts() {
        List<Alert> alerts = new ArrayList<Alert>();
        for(Response resp : _multiMap.values()) {
            alerts.addAll(((AlertsResponse)resp).getAlert());
        }
        Collections.sort(alerts, new Comparator<Alert>() {
            public int compare(Alert e1, Alert e2) {
                return (int) (e2.getCtime() - e1.getCtime());
            }
        });
        return alerts;
    }
    
    
}
