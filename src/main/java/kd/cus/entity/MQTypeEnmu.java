package kd.cus.entity;

public enum MQTypeEnmu {

    MENU_CODE("菜单", "menu"),
    BTN_CODE("按钮","btn"),
    ROLE_CODE("角色","role"),
    ROLE_USER_CODE("角色绑定用户","role_user"),
    USER_ROLE_CODE("用户绑定角色","user_role"),
    ROLE_REC_BTN_CODE("角色绑定按钮","role_rec_btn"),
    ROLE_REC_MENU_CODE("角色绑定菜单","role_rec_menu");

    private String name;
    private String code;
    private MQTypeEnmu(String name, String code){
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }
}
