package kd.cus.register;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.cus.action.MQMainTemplate;
import kd.cus.entity.OperateEnmu;
import kd.cus.entity.RoleResMenuMessage;
import kd.cus.entity.UserRoleMessage;

public class DisposeRoleRecMenuMessage implements MQMainTemplate {
    @Override
    public String dispose(OperateEnmu operateEnmu, Object data) {

        RoleResMenuMessage roleResMenuMessage = JSON.parseObject(((JSONObject) data).toJSONString(), RoleResMenuMessage.class);
        return null;
    }
}
