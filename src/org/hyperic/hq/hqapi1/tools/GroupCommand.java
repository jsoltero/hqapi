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

package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.GroupApi;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.ResourcePrototypeResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
@Component
public class GroupCommand extends AbstractCommand {

    private static String CMD_LIST   = "list";
    private static String CMD_SYNC   = "sync";
    private static String CMD_DELETE = "delete";

    private static String[] COMMANDS = { CMD_LIST, CMD_SYNC, CMD_DELETE };

    private static String OPT_ID     = "id";
    private static String OPT_COMPAT = "compatible";
    private static String OPT_MIXED  = "mixed";

    // Additional sync commands when syncing via command line options.
    private static String OPT_NAME          = "name";
    private static String OPT_PROTOTYPE     = "prototype";
    private static String OPT_REGEX         = "regex";
    private static String OPT_DELETEMISSING = "deleteMissing";
    private static String OPT_DESC          = "description";

    private void printUsage() {
        System.err.println("One of " + Arrays.toString(COMMANDS) + " required");
    }
    
    public String getName() {
        return "group";
     }

    public int handleCommand(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return 1;
        }

        if (args[0].equals(CMD_LIST)) {
            list(trim(args));
        } else if (args[0].equals(CMD_SYNC)) {
            sync(trim(args));
        } else if (args[0].equals(CMD_DELETE)) {
            delete(trim(args));
        } else {
            printUsage();
            return 1;
        }
        return 0;
    }

    private void list(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_COMPAT, "List only compatible groups");
        p.accepts(OPT_MIXED, "List only mixed groups");

        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);
        GroupApi groupApi = api.getGroupApi();

        if (options.has(OPT_COMPAT) && options.has(OPT_MIXED)) {
            System.err.println("Only one of " + OPT_COMPAT + " and " + OPT_MIXED +
                               " is allowed.");
            System.exit(-1);
        }

        GroupsResponse groups;

        if (options.has(OPT_COMPAT)) {
            groups = groupApi.getCompatibleGroups();
        } else if (options.has(OPT_MIXED)) {
            groups = groupApi.getMixedGroups();
        } else {
            groups = groupApi.getGroups();
        }

        XmlUtil.serialize(groups, System.out, Boolean.TRUE);
    }

    private void sync(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_NAME, "The group name to sync").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_PROTOTYPE, "The resource type to query for group membership").
                withRequiredArg().ofType(String.class);
        p.accepts(OPT_REGEX, "The regular expression to apply to the " + OPT_PROTOTYPE +
                  " flag").withRequiredArg().ofType(String.class);
        p.accepts(OPT_DELETEMISSING, "Remove resources in the group not included in " +
                  "the " + OPT_PROTOTYPE + " and " + OPT_REGEX);
        p.accepts(OPT_COMPAT, "If specified, attempt to make the group compatible");
        p.accepts(OPT_DESC, "If specified, set the description for the group").
                withRequiredArg().ofType(String.class);

        OptionSet options = getOptions(p, args);

        if (options.hasArgument(OPT_NAME)) {
            syncViaCommandLineArgs(options);
            return;
        }

        HQApi api = getApi(options);

        GroupApi groupApi = api.getGroupApi();

        InputStream is = getInputStream(options);

        GroupsResponse resp = XmlUtil.deserialize(GroupsResponse.class, is);
        List<Group> groups = resp.getGroup();

        GroupsResponse syncResponse = groupApi.syncGroups(groups);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + groups.size() + " groups.");
    }

    private void syncViaCommandLineArgs(OptionSet s) throws Exception
    {
        // Required args
        String name = (String)getRequired(s, OPT_NAME);
        String prototype = (String)getRequired(s, OPT_PROTOTYPE);
        
        // Optional
        String regex = (String)s.valueOf(OPT_REGEX);
        boolean deleteMissing = s.has(OPT_DELETEMISSING);
        boolean compatible = s.has(OPT_COMPAT);

        HQApi api = getApi(s);

        // Get prototype
        ResourcePrototypeResponse protoResponse =
                api.getResourceApi().getResourcePrototype(prototype);
        checkSuccess(protoResponse);

        // Query resources
        ResourcesResponse resourceResponse = api.getResourceApi().
                getResources(protoResponse.getResourcePrototype(), false, false);
        checkSuccess(resourceResponse);

        List<Resource> resources = resourceResponse.getResource();

        // Filter based on regex, if given.
        if (regex != null) {
            Pattern pattern = Pattern.compile(regex);
            for (Iterator<Resource> i = resources.iterator(); i.hasNext(); ) {
                Resource r = i.next();
                Matcher m = pattern.matcher(r.getName());
                if (!m.matches()) {
                    i.remove();
                }
            }
        }

        System.out.println(name + ": Found " + resources.size() + " matching resources");

        // Check for existing group
        Group group;
        GroupResponse groupResponse = api.getGroupApi().getGroup(name);
        if (groupResponse.getStatus().equals(ResponseStatus.SUCCESS)) {
            group = groupResponse.getGroup();
            System.out.println(name + ": Syncing existing group (" +
                               group.getResource().size() + " members)");

            if (deleteMissing) {
                System.out.println(name + ": Clearing existing members");
                group.getResource().clear();
            }
        } else {
            group = new Group();
            group.setName(name);
            if (compatible) {
                group.setResourcePrototype(protoResponse.getResourcePrototype());
            }
            System.out.println(name + ": Creating new group");
        }

        if (s.hasArgument(OPT_DESC)) {
            group.setDescription((String)s.valueOf(OPT_DESC));
        }
        
        group.getResource().addAll(resources);
        List<Group> groups = new ArrayList<Group>();
        groups.add(group);
        GroupsResponse syncResponse = api.getGroupApi().syncGroups(groups);
        checkSuccess(syncResponse);

        System.out.println(name + ": Success (" +
                           syncResponse.getGroup().get(0).getResource().size() + " members)");
    }

    private void delete(String[] args) throws Exception {

        OptionParser p = getOptionParser();

        p.accepts(OPT_ID, "The resource id to delete").
                withRequiredArg().ofType(Integer.class);

        OptionSet options = getOptions(p, args);

        if (!options.has(OPT_ID)) {
            System.err.println("Required argument " + OPT_ID + " not given");
            System.exit(-1);
        }

        GroupApi groupApi = getApi(options).getGroupApi();

        Integer id = (Integer)options.valueOf(OPT_ID);

        StatusResponse response = groupApi.deleteGroup(id);
        checkSuccess(response);

        System.out.println("Successfully deleted group id " + id);
    }
}
