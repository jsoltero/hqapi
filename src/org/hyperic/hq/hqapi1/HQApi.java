/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008, 2009], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

package org.hyperic.hq.hqapi1;

import java.io.IOException;

/**
 * The Hyperic HQ API.
 *
 * This is the main entry point into the HQ Api.
 */
public class HQApi {
    
    private final HQConnection       _connection;

    private final UserApi            _userApi;
    private final RoleApi            _roleApi;
    private final MetricApi          _metricApi;
    private final GroupApi           _groupApi;
    private final EscalationApi      _escalationApi;
    private final AutodiscoveryApi   _autodiscoveryApi;
    private final ResourceApi        _resourceApi;
    private final AgentApi           _agentApi;
    private final AlertDefinitionApi _alertDefinitionApi;
    private final MaintenanceApi     _maintenanceApi;
    private final ResourceEdgeApi    _resourceEdgeApi;
    private final ServerConfigApi    _serverConfigApi;
    private final AlertApi           _alertApi;
    private final MetricDataApi      _metricDataApi;
    private final EventApi           _eventApi;
    private final ControlApi         _controlApi;
    private final ApplicationApi     _applApi;

    /**
     * @param host The hostname of the HQ Server to connect to.
     * @param port The port on the HQ server to connect to.
     * @param isSecure Set to true if connecting via SSL.
     * @param user The user to connect as.
     * @param password The password for the given user.
     */
    public HQApi(String host, int port, boolean isSecure, String user,
                 String password) {
        _connection = new HQConnection(host, port, isSecure,
                                                   user, password);
        _userApi          = new UserApi(_connection);
        _roleApi          = new RoleApi(_connection);
        _groupApi         = new GroupApi(_connection);
        _metricApi        = new MetricApi(_connection);
        _escalationApi    = new EscalationApi(_connection);
        _autodiscoveryApi = new AutodiscoveryApi(_connection);
        _resourceApi      = new ResourceApi(_connection);
        _agentApi         = new AgentApi(_connection);
        _alertDefinitionApi = new AlertDefinitionApi(_connection);
        _maintenanceApi   = new MaintenanceApi(_connection);
        _resourceEdgeApi  = new ResourceEdgeApi(_connection);
        _serverConfigApi  = new ServerConfigApi(_connection);
        _alertApi         = new AlertApi(_connection);
        _metricDataApi    = new MetricDataApi(_connection);
        _eventApi         = new EventApi(_connection);
        _controlApi       = new ControlApi(_connection);
        _applApi          = new ApplicationApi(_connection);
    }

    /**
     * Add, remove and update users.
     * 
     * @return The API for operating on users.
     */
    public UserApi getUserApi() {
        return _userApi;
    }

    /**
     * Add, remove and list resource and resource prototypes.
     *
     * @return The API for operating on resource and resource prototypes.
     */
    public ResourceApi getResourceApi() {
        return _resourceApi;
    }
    
    /**
     * Add, remove and update roles.
     *
     * @return The API for operating on roles.
     */
    public RoleApi getRoleApi() {
        return _roleApi;
    }

    /**
     * Add, remove and modify groups.
     *
     * @return The API for operating on groups.
     */
    public GroupApi getGroupApi() {
        return _groupApi;
    }

    /**
     * Import, export measurement data and modify measurement schedules.
     *
     * @return The API for operating on measurement data and schedules.
     */
    public MetricApi getMetricApi() {
        return _metricApi;
    }

    /**
     * Import or Export Metric data
     *
     * @return The API for querying or reporting Metric information.
     */
    public MetricDataApi getMetricDataApi() {
        return _metricDataApi;
    }


    /**
     * Add, remove and assign escalations.
     *
     * @return The API for operating on esclations.
     */
    public EscalationApi getEscalationApi() {
        return _escalationApi;
    }

    /**
     * List and approve auto-discovered resources.
     *
     * @return The API for operating on auto-discovered resources.
     */
    public AutodiscoveryApi getAutodiscoveryApi() {
        return _autodiscoveryApi;
    }

    /**
     * Get and ping agents.
     *
     * @return The API for operating on agent resources.
     */
    public AgentApi getAgentApi() {
        return _agentApi;
    }

    /**
     * Add, remove and list alert definitions.
     *
     * @return The API for operating on alert definitions.
     */
    public AlertDefinitionApi getAlertDefinitionApi() {
        return _alertDefinitionApi;
    }

    /**
     * Schedule maintenance for groups in HQ.
     *
     * @return The API for operating on alert definitions.
     */
    public MaintenanceApi getMaintenanceApi() {
        return _maintenanceApi;
    }
    
    /**
     * List and update the relationships between resources.
     *
     * @return The API for operating on resource relationships.
     */
    public ResourceEdgeApi getResourceEdgeApi() {
        return _resourceEdgeApi;
    }

    /**
     * Manipulate HQ server configuration settings
     *
     * @return The API for modifying HQ server settings
     */
    public ServerConfigApi getServerConfigApi() {
        return _serverConfigApi;
    }

    /**
     * Manage Alerts
     *
     * @return The API for finding and managing Alerts.
     */
    public AlertApi getAlertApi() {
        return _alertApi;
    }

    /**
     * List events in HQ
     *
     * @return The API for finding Events.
     */
    public EventApi getEventApi() {
        return _eventApi;
    }

    /**
     * View actions, control history and issue commands on Resources.
     *
     * @return The API for viewing and issuing control actions.
     */
    public ControlApi getControlApi() {
        return _controlApi;
    }

    /*
     * List, create and update Applications.
     *
     * @return The API for operating on Applications.
     */
    public ApplicationApi getApplicationApi() {
        return _applApi;
    }

    /**
     * Get the hostname of this API object
     * @return the hostname of this API instance
     */
    public String getHost() {
        return _connection.getHost();
    }

    /**
     * Test that the HQConnection is valid 
     * @return true if this API instance is connected
     * to a valid HQApi host
     */
    public boolean testConnection() {
        boolean connected = false;
        try {
            // test by looking up the user who connected
            _userApi.getUser(_connection.getUser());
            connected = true;
        } catch (IOException e) {
            // Log?
        }
        return connected;        
    }
}
