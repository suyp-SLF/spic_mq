package kd.cus.register;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.context.RequestContext;
import kd.bos.context.RequestContextCreator;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.db.tx.TX;
import kd.bos.db.tx.TXHandle;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.service.business.datamodel.DynamicFormModelProxy;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.cus.action.MQMainTemplate;
import kd.cus.entity.OperateEnmu;
import kd.cus.entity.RoleMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisposeRoleMessage implements MQMainTemplate {

    private static final String ROLE_LOGO = "perm_role";//通用角色
    private static final String BIZ_ROLE_LOGO = "perm_bizrole";//业务角色
    private static final String ORG_LOGO = "bos_org";//组织

    @Override
    public String dispose(OperateEnmu operateEnmu, Object data) throws Exception {
        RoleMessage roleMessage = JSON.parseObject(((JSONObject) data).toJSONString(), RoleMessage.class);
        DynamicObject role_this_dy;
        DynamicObject bizRole_this_dy;
        OperationResult res;
        QFilter[] qFilters;
        String[] codeIds;
        String roleName;
        codeIds = roleMessage.getRoleCode().split("_");
        roleName = roleMessage.getRoleName();
        if (codeIds.length == 2 && StringUtils.isNotBlank(roleName)) {
            String bizRoleName = roleMessage.getRoleName();
            String orgNumber = codeIds[0];
            String roleNumber = "Role-000001";
            String bizRoleNumber = roleMessage.getRoleCode();

            DynamicObject role_dy;// = BusinessDataServiceHelper.loadSingleFromCache(ROLE_LOGO, new QFilter[]{new QFilter("number", QCP.equals, roleNumber)});
            DynamicObject bizRole_dy;// = BusinessDataServiceHelper.loadSingleFromCache(BIZ_ROLE_LOGO, new QFilter[]{new QFilter("number", QCP.equals, bizRoleNumber)});
            DynamicObject org_dy = BusinessDataServiceHelper.loadSingleFromCache(ORG_LOGO, new QFilter[]{new QFilter("number", QCP.equals, orgNumber)});

            if (null != org_dy) {
                switch (operateEnmu) {
                    case ADD:
//                        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(ROLE_LOGO, "id", new QFilter[]{new QFilter("number", QCP.equals, roleNumber)});
//                        if (null == dynamicObject) {
                        Map<Class<?>, Object> services = new HashMap<>();
                        DynamicFormModelProxy model = new DynamicFormModelProxy(ROLE_LOGO, UUID.randomUUID().toString(), services);
//                            model.createNewData();
//                            role_this_dy = model.getDataEntity(true);

                        model = new DynamicFormModelProxy(BIZ_ROLE_LOGO, UUID.randomUUID().toString(), services);
                        model.createNewData();
                        bizRole_this_dy = model.getDataEntity(true);

//                DynamicObject this_dy = BusinessDataServiceHelper.newDynamicObject(ROLE_LOGO);
//                            role_this_dy.set("name", roleName);
//                            role_this_dy.set("number", roleNumber);
//                            OperationServiceHelper.executeOperate("save", ROLE_LOGO, new DynamicObject[]{role_this_dy}, null);
                        role_dy = BusinessDataServiceHelper.loadSingleFromCache(ROLE_LOGO, new QFilter[]{new QFilter("number", QCP.equals, roleNumber)});
                        bizRole_this_dy.set("name", bizRoleName);
                        bizRole_this_dy.set("number", bizRoleNumber);
                        bizRole_this_dy.set("usertype", "1");
                        DynamicObjectCollection role_colDy = bizRole_this_dy.getDynamicObjectCollection("bizrolecomrole");
                        DynamicObjectCollection org_colDy = bizRole_this_dy.getDynamicObjectCollection("bizroleorg");
//                            role_colDy.clear();
//                            role_colDy.clear();
//                            DynamicObject bizroleorg_dy = BusinessDataServiceHelper.newDynamicObject("perm_bizroleorg");
//                            bizroleorg_dy.set("org",org_dy);
//                            bizroleorg_dy.set("dimtype","bos_org");
//                            bizroleorg_dy.set("org_id",(long)org_dy.getPkValue());
//                            Object[] dys = SaveServiceHelper.save(new DynamicObject[]{bizroleorg_dy});
//                            DynamicObject com_role_dy = new DynamicObject(role_colDy.getDynamicObjectType());
//                            com_role_dy.set("role_visible",role_dy);
//                            com_role_dy.set("isenable_visible",true);
//                            com_role_dy.set("role_visible_id",role_dy.getPkValue());
//                            role_colDy.add(com_role_dy);
//                            DynamicObject com_org_colDy = new DynamicObject(org_colDy.getDynamicObjectType());
//                            com_org_colDy.set("org_visible",org_colDy);
//                            com_org_colDy.set("isincludesuborg_visible",false);
//                            com_org_colDy.set("org_visible_id",Long.parseLong(org_dy.getPkValue().toString()));
//                            org_colDy.add(com_org_colDy);
                        DynamicObject com_role_dy = new DynamicObject(role_colDy.getDynamicObjectType());
                        com_role_dy.set("role", role_dy);
                        com_role_dy.set("isenable", true);
                        com_role_dy.set("role_id", role_dy.getPkValue());
                        role_colDy.add(com_role_dy);
                        DynamicObject com_org_colDy = new DynamicObject(org_colDy.getDynamicObjectType());
                        com_org_colDy.set("org", org_dy);
                        com_org_colDy.set("isincludesuborg", false);
                        com_org_colDy.set("dimtype", "bos_org");
                        com_org_colDy.set("org_id", org_dy.getPkValue());
                        org_colDy.add(com_org_colDy);
                        OperationResult a = OperationServiceHelper.executeOperate("save", BIZ_ROLE_LOGO, new DynamicObject[]{bizRole_this_dy}, OperateOption.create());
                        return a.isSuccess() ? "" : a.getMessage();
//                            System.out.println(a);
//                        }
//                        break;
                    case UPDATE:
//                        role_this_dy = BusinessDataServiceHelper.loadSingleFromCache(ROLE_LOGO, new QFilter[]{new QFilter("number", QCP.equals, roleNumber)});
                        bizRole_this_dy = BusinessDataServiceHelper.loadSingleFromCache(BIZ_ROLE_LOGO, new QFilter[]{new QFilter("number", QCP.equals, bizRoleNumber)});

                        if (null != bizRole_this_dy) {
//                            role_this_dy.set("name", roleName);
                            bizRole_this_dy.set("name", bizRoleName);
//                            OperationResult a = OperationServiceHelper.executeOperate("save", ROLE_LOGO, new DynamicObject[]{role_this_dy});
                            OperationResult b = OperationServiceHelper.executeOperate("save", BIZ_ROLE_LOGO, new DynamicObject[]{bizRole_this_dy});
                            return b.isSuccess() ? "" : b.getMessage();
//                            System.out.println(b);
                        }
                        break;
                    case DEL:
                        //将无法删除
//                qFilters = new QFilter[]{new QFilter("number", QCP.equals, roleMessage.getRoleCode())};
//                this_dy = BusinessDataServiceHelper.loadSingleFromCache(ROLE_LOGO, qFilters);
//                DeleteServiceHelper deleteServiceHelper = new DeleteServiceHelper();
//                res = deleteServiceHelper.deleteOperate(ROLE_LOGO, new Object[]{this_dy.getPkValue()}, OperateOption.create());
                        break;
                }
            } else {
                //组织参数未找到
                return "组织参数未找到";
            }
        } else {
            //输出格式不正确
            return "输出格式不正确";
        }
        return "";
    }
}
