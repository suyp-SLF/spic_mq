package kd.cus.register;

import kd.cus.action.MQMainTemplate;
import kd.cus.entity.OperateEnmu;

public class DisposeBtnMessage implements MQMainTemplate {

    @Override
    public String dispose(OperateEnmu operateEnmu, Object data) {
        return null;
    }
}
