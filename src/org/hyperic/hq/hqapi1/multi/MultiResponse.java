package org.hyperic.hq.hqapi1.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.Response;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.ServiceError;

/**
 * Wrap response objects, their status, and errors
 * in one object with hosts as keys.
 *
 */
public abstract class MultiResponse {

    protected Map<String, Response> _multiMap;
    protected Map<String, ResponseStatus> _statusMap;
    protected Map<String, ServiceError> _errorMap;
    
    public MultiResponse() {
        _multiMap = new HashMap<String, Response>();
        _statusMap = new HashMap<String, ResponseStatus>();
        _errorMap = new HashMap<String, ServiceError>();
    }
    
    public List<String> getHosts() {
        return new ArrayList<String>(_statusMap.keySet());
    }
    
    public void addResponse(String host, Response resp) {
        _multiMap.put(host, resp);
        _statusMap.put(host, resp.getStatus());
        if(resp.getError() != null) {
            _errorMap.put(host, resp.getError());
        }
    }
    
    public ResponseStatus getResponseStatus(String host) {
        return _statusMap.get(host);
    }
    
    public Response getResponse(String host) {
        return _multiMap.get(host);
    }
    
    public ServiceError getError(String host) {
        return _errorMap.get(host);
    }
    
    
}
