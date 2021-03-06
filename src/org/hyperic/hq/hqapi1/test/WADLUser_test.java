package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.wadl.*;

public class WADLUser_test extends WADLTestBase {

    public void testUserList() throws Exception {

        Endpoint.UserListHqu userList = new Endpoint.UserListHqu();

        UsersResponse users = userList.getAsUsersResponse();
        hqAssertSuccess(users);
    }

    public void testUserGet() throws Exception {

        Endpoint.UserGetHqu userGet = new Endpoint.UserGetHqu();

        UserResponse user = userGet.getAsUserResponse("guest");
        hqAssertSuccess(user);
    }

    public void testUserCreateDeleteSyncChangePassword() throws Exception {

        Endpoint.UserCreateHqu userCreate = new Endpoint.UserCreateHqu();
        Endpoint.UserDeleteHqu userDelete = new Endpoint.UserDeleteHqu();
        Endpoint.UserChangePasswordHqu userChangePassword =
                new Endpoint.UserChangePasswordHqu();
        Endpoint.UserSyncHqu userSync = new Endpoint.UserSyncHqu();

        // Test User Sync - Required Atts
        UserResponse responseReq = userCreate.getAsUserResponse("testWadl",
                                                                "testWadl",
                                                                "Test",
                                                                "WADL",
                                                                "testwadl@springsource.com");
        hqAssertSuccess(responseReq);

        // Test User Change password
        StatusResponse changeResponse =
                userChangePassword.getAsStatusResponse(responseReq.getUser().getName(),
                                                       "newPassword");
        hqAssertSuccess(changeResponse);

        // Test User Delete
        StatusResponse deleteReq = userDelete.getAsStatusResponse(responseReq.getUser().getName());
        hqAssertSuccess(deleteReq);

        // Test User Create Required + Optional attributes
        UserResponse responseOpt = userCreate.getAsUserResponse("testWadl",
                                                                "testWadl",
                                                                "Test",
                                                                "WADL",
                                                                "testwadl@springsource.com",
                                                                true,
                                                                true,
                                                                "WADL Dept",
                                                                "415-555-5555",
                                                                "testwadl@springsource.com");
        hqAssertSuccess(responseOpt);

        // Test User Sync
        User u = responseOpt.getUser();
        u.setFirstName("New First Name");
        UsersRequest r = new UsersRequest();
        r.getUser().add(u);
        StatusResponse syncResponse = userSync.postAsStatusResponse(r);
        hqAssertSuccess(syncResponse);

        StatusResponse deleteOpt = userDelete.getAsStatusResponse(responseOpt.getUser().getName());
        hqAssertSuccess(deleteOpt);
    }
}
