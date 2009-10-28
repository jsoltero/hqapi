package org.hyperic.hq.hqapi1.multi;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.AlertApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.types.AlertsResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.ServiceError;
import org.hyperic.hq.hqapi1.types.StatusResponse;

public class MultiAlertApi {

    private Map<String, AlertApi> _alertApiMap;
    private MultiResourceApi _multiResourceApi;
    
    public MultiAlertApi(Collection<HQApi> hqApis) {
        _alertApiMap = new HashMap<String, AlertApi>();
        for(HQApi anApi : hqApis) {
            _alertApiMap.put(anApi.getHost(), anApi.getAlertApi());
            
        }
        _multiResourceApi = new MultiResourceApi(hqApis);
    }
    
    public void addHQApi(HQApi anApi) {
        _alertApiMap.put(anApi.getHost(), anApi.getAlertApi());
        _multiResourceApi.addHQApi(anApi);
    }
    
    public void removeHQApi(HQApi anApi) {
        _alertApiMap.remove(anApi.getHost());
        _multiResourceApi.removeHQApi(anApi);
    }
    
    public MultiAlertsResponse findAlerts(long begin, long end, int count,
                                          int severity, Boolean inEscalation,
                                          Boolean notFixed)
        throws IOException
    {
        MultiAlertsResponse mar = new MultiAlertsResponse();
        for(String host : _alertApiMap.keySet()) {
            AlertsResponse aResp = _alertApiMap.get(host).findAlerts(begin, 
                                                                     end, 
                                                                     count, 
                                                                     severity, 
                                                                     inEscalation, 
                                                                     notFixed);
            mar.addResponse(host, aResp);
        }
        return mar;
    }
    
    public MultiAlertsResponse findAlerts(ResourcePrototype type, 
                                          long begin, long end, int count,
                                          int severity, Boolean inEscalation,
                                          Boolean notFixed)
        throws IOException
    {
        MultiAlertsResponse mar = new MultiAlertsResponse();
        for(String host : _alertApiMap.keySet()) {
            MultiResourcesResponse mrr = _multiResourceApi.getResources(type, false, false);
            for(Resource r : mrr.getAllResources()) {
                AlertsResponse aResp = _alertApiMap.get(host).findAlerts(r, begin, end, count, 
                                                                          severity, inEscalation, 
                                                                          notFixed);
                mar.addResponse(host, aResp);
            }
        }
        return mar;
    }
    
    public AlertsResponse fixAlerts(String host, Integer[] alertIds) 
        throws IOException {
        AlertApi anApi = _alertApiMap.get(host);
        if(anApi == null) {
            AlertsResponse aResp = new AlertsResponse();
            ServiceError err = new ServiceError();
            err.setErrorCode("HQ host not found in MultiAPI Map");
            err.setReasonText("HQ Host not found in MultiAPI Map");
            aResp.setError(err);
            aResp.setStatus(ResponseStatus.FAILURE);
            return aResp;
        }
        else {
            return _alertApiMap.get(host).fixAlerts(alertIds);
        }
    }
    
    public AlertsResponse ackAlerts(String host, Integer[] alertIds, 
                                  String reason, long pause)
        throws IOException {
        AlertApi anApi = _alertApiMap.get(host);
        if(anApi == null) {
            AlertsResponse aResp = new AlertsResponse();
            ServiceError err = new ServiceError();
            err.setErrorCode("HQ host not found in MultiAPI Map");
            err.setReasonText("HQ Host not found in MultiAPI Map");
            aResp.setError(err);
            aResp.setStatus(ResponseStatus.FAILURE);
            return aResp;
        }
        else {
            return _alertApiMap.get(host).ackAlerts(alertIds, reason, pause);
        }
    }
    
    public StatusResponse deleteAlerts(String host, Integer[] ids) 
        throws IOException {
        AlertApi anApi = _alertApiMap.get(host);
        if(anApi == null) {
            StatusResponse aResp = new StatusResponse();
            ServiceError err = new ServiceError();
            err.setErrorCode("HQ host not found in MultiAPI Map");
            err.setReasonText("HQ Host not found in MultiAPI Map");
            aResp.setError(err);
            aResp.setStatus(ResponseStatus.FAILURE);
            return aResp;
        }
        else {
            return _alertApiMap.get(host).delete(ids);
        }
    }
}
