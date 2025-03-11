package kd.cus.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 用户绑定角色操作 - 事件内容
 * 用户关联角色MQ消息对象
 */

public class UserRoleMessage implements Serializable {

//    private static final long serialVersionUID = -7589225346066125335L;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 角色编码集合
     */
    private List<String> roleCodes;

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(final String userCode) {
        this.userCode = userCode;
    }

    public List<String> getRoleCodes() {
        return this.roleCodes;
    }

    public void setRoleCodes(final List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }
}
