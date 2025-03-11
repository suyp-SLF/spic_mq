package kd.cus.entity;

import java.io.Serializable;

/**
 * 按钮操作 - 事件内容对象
 * 按钮的MQ消息体对象
 */

public class BtnMessage implements Serializable {

//    private static final long serialVersionUID = 223970290838435980L;

    /**
     * 菜单编码(公共按钮该字段没有内容)
     */
    private String menuCode;

    /**
     * 按钮编码
     */
    private String btnCode;

    /**
     * 按钮名称
     */
    private String btnName;

    public String getMenuCode() {
        return this.menuCode;
    }

    public void setMenuCode(final String menuCode) {
        this.menuCode = menuCode;
    }

    public String getBtnCode() {
        return this.btnCode;
    }

    public void setBtnCode(final String btnCode) {
        this.btnCode = btnCode;
    }

    public String getBtnName() {
        return this.btnName;
    }

    public void setBtnName(final String btnName) {
        this.btnName = btnName;
    }
}
