package kd.cus.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 角色绑定菜单资源相关操作 - 事件内容
 * 角色关联菜单资源MQ消息对象
 */

public class RoleResMenuMessage implements Serializable {

//    private static final long serialVersionUID = -7010003381437032731L;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 菜单编码集合
     */
    private List<String> menuCodes;

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(final String roleCode) {
        this.roleCode = roleCode;
    }

    public List<String> getMenuCodes() {
        return this.menuCodes;
    }

    public void setMenuCodes(final List<String> menuCodes) {
        this.menuCodes = menuCodes;
    }
}
