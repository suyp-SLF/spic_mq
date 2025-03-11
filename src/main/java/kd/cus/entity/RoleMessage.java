package kd.cus.entity;

import java.io.Serializable;

/**
 * 角色操作 - 事件内容对象
 * 角色的MQ消息体对象
 */

public class RoleMessage implements Serializable {
//    private static final long serialVersionUID = 4698930841518618716L;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(final String roleCode) {
        this.roleCode = roleCode;
    }
}
