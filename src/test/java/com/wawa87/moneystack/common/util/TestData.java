package com.wawa87.moneystack.common.util;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.user.model.User;
import com.wawa87.moneystack.user.model.UserRequest;
import com.wawa87.moneystack.user.model.UserResponse;
import com.wawa87.moneystack.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    private static AppContext ctx;
    private static UserService userService;

    private static User devUser;
    private static User cosmo;
    private static User jerry;
    private static User george;

    public static void createTestUsers() throws BadRequestException, InvalidUsernameException {
        ctx = new AppContext();
        userService = ctx.getUserService();

        devUser = new User();
        devUser.setUsername("dev");
        devUser.setPassword("dev");
        devUser.setFirstName("Dev");
        devUser.setLastName("User");
        devUser.setEmails(new ArrayList<>(List.of("dev@test.com")));
        devUser.setPhoneNumber("17602221111");

        devUser = UserResponse.convertResponseToUser(userService.register(UserRequest.convertUsertoRequest(devUser)));

        cosmo = new User();
        cosmo.setUsername("ckramer");
        cosmo.setPassword("yoyoma");
        cosmo.setFirstName("Cosmo");
        cosmo.setLastName("Kramer");
        cosmo.setEmails(new ArrayList<>(List.of("cosmo@seinfeld.com")));
        cosmo.setPhoneNumber("17602222222");

        cosmo = UserResponse.convertResponseToUser(userService.register(UserRequest.convertUsertoRequest(cosmo)));

        jerry = new User();
        jerry.setUsername("jseinfeld");
        jerry.setPassword("thatsashame");
        jerry.setFirstName("Jerry");
        jerry.setLastName("Seinfeld");
        jerry.setEmails(new ArrayList<>(List.of("jerry@seinfeld.com")));
        jerry.setPhoneNumber("17602223333");

        jerry = UserResponse.convertResponseToUser(userService.register(UserRequest.convertUsertoRequest(jerry)));

        george = new User();
        george.setUsername("gcostanza");
        george.setPassword("bosco");
        george.setFirstName("George");
        george.setLastName("Costanza");
        george.setEmails(new ArrayList<>(List.of("george@seinfeld.com")));
        george.setPhoneNumber("17602224444");

        george = UserResponse.convertResponseToUser(userService.register(UserRequest.convertUsertoRequest(george)));
    }

    public static void cleanTestUsers() {
        userService.deleteUser(devUser);
        userService.deleteUser(cosmo);
        userService.deleteUser(jerry);
        userService.deleteUser(george);
    }
}
