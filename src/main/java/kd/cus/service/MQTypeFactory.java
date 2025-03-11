package kd.cus.service;

import kd.cus.action.MQMainTemplate;
import kd.cus.entity.*;
import kd.cus.register.*;

public class MQTypeFactory {
    private static final String MENU_CODE = "menu";
    private static final String BTN_CODE = "btn";
    private static final String ROLE_CODE = "role";
    private static final String ROLE_USER_CODE = "role_user";
    private static final String USER_ROLE_CODE = "user_role";
    private static final String ROLE_REC_BTN_CODE = "role_rec_btn";
    private static final String ROLE_REC_MENU_CODE = "role_rec_menu";

    public static MQMainTemplate getType(String type){
        switch (type){
            case MENU_CODE:
                return new DisposeMenuMessage();
            case BTN_CODE:
                return new DisposeBtnMessage();
            case ROLE_CODE:
                return new DisposeRoleMessage();
            case ROLE_USER_CODE:
                return new DisposeRoleUserMessage();
            case USER_ROLE_CODE:
                return new DisposeUserRoleMessage();
            case ROLE_REC_BTN_CODE:
                return new DisposeRoleRecBtnMessage();
            case ROLE_REC_MENU_CODE:
                return new DisposeRoleRecMenuMessage();
        }
        return null;
    }
}
