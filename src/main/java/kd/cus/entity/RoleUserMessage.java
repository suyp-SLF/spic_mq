package kd.cus.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 角色绑定/解绑用户操作 - 事件内容
 * 角色关联用户的MQ消息体对象
 */

public class RoleUserMessage implements Serializable {

//    private static final long serialVersionUID = -4299960489117697127L;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 用户编码集合
     */
    private List<String> userCodes;

    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(final String roleCode) {
        this.roleCode = roleCode;
    }

    public List<String> getUserCodes() {
        return this.userCodes;
    }

    public void setUserCodes(final List<String> userCodes) {
        this.userCodes = userCodes;
    }
}
