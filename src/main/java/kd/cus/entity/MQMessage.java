package kd.cus.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * MQ消息对象
 */
public class MQMessage<T> implements Serializable {

//    private static final long serialVersionUID = -542040321470115712L;

    /**
     * 消息唯一标识(采用MongoDB ObjectId生成算法)
     */
    private String msgId;

    /**
     * 系统编码
     */
    private String sysCode;

    /**
     * 事件类型
     */
    private String type;

    /**
     * 事件操作
     */
    private String operation;

    /**
     * 创建时间
     */
    private LocalDateTime dateTime;

    /**
     * 事件内容（泛型 - 根据事件类型绑定不同的消息对象）
     */
    private T body;

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(final String msgId) {
        this.msgId = msgId;
    }

    public String getSysCode() {
        return this.sysCode;
    }

    public void setSysCode(final String sysCode) {
        this.sysCode = sysCode;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(final String operation) {
        this.operation = operation;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public T getBody() {
        return this.body;
    }

    public void setBody(final T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        String str = "" +
                "------MQLOG-------------------------------------------------------------------------" +
                "";
        return super.toString();
    }
}
