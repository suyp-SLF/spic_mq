package kd.cus.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 角色绑定按钮资源相关操作 - 事件内容
 * 角色关联按钮资源MQ消息对象
 */

public class RoleResBtnMessage implements Serializable {
    private static final long serialVersionUID = -2626734820446481228L;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 按钮编码集合
     */
    private List<String> btnCodes;

    public static long getSerialVersionUID() {
        return RoleResBtnMessage.serialVersionUID;
    }

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(final String roleCode) {
        this.roleCode = roleCode;
    }

    public String getMenuCode() {
        return this.menuCode;
    }

    public void setMenuCode(final String menuCode) {
        this.menuCode = menuCode;
    }

    public List<String> getBtnCodes() {
        return this.btnCodes;
    }

    public void setBtnCodes(final List<String> btnCodes) {
        this.btnCodes = btnCodes;
    }
}
