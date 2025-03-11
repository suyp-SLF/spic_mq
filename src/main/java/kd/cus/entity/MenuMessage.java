package kd.cus.entity;

import java.io.Serializable;

/**
 * 菜单操作 - 事件内容对象
 * 菜单的MQ消息体对象
 */

public class MenuMessage implements Serializable {
//    private static final long serialVersionUID = -5392803351997802273L;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父级菜单编码
     */
    private String parentMenuCode;

    /**
     * 菜单是否启用(0 - 未启用，1 - 已启用)
     */
    private boolean enable;

    /**
     * 菜单路径
     */
    private String menuPath;

    /**
     * 菜单排序
     */
    private int menuOrder;

    public String getMenuCode() {
        return this.menuCode;
    }

    public void setMenuCode(final String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(final String menuName) {
        this.menuName = menuName;
    }

    public String getParentMenuCode() {
        return this.parentMenuCode;
    }

    public void setParentMenuCode(final String parentMenuCode) {
        this.parentMenuCode = parentMenuCode;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(final boolean enable) {
        this.enable = enable;
    }

    public String getMenuPath() {
        return this.menuPath;
    }

    public void setMenuPath(final String menuPath) {
        this.menuPath = menuPath;
    }

    public int getMenuOrder() {
        return this.menuOrder;
    }

    public void setMenuOrder(final int menuOrder) {
        this.menuOrder = menuOrder;
    }
}
