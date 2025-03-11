package kd.cus.action;

import kd.bos.context.RequestContextCreator;
import kd.bos.dataentity.utils.StringUtils;
import kd.cus.entity.OperateEnmu;

public interface MQMainTemplate {
    public String dispose(OperateEnmu operateEnmu, Object data) throws Exception;
}
