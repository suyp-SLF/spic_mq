package kd.cus.register;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.db.tx.TX;
import kd.bos.db.tx.TXHandle;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.permission.cache.DataPermissionCache;
import kd.bos.permission.cache.FieldPermissionCache;
import kd.bos.permission.cache.util.PermCommonUtil;
import kd.bos.permission.formplugin.BizRoleUtil;
import kd.bos.permission.formplugin.util.PermFormCommonUtil;
import kd.bos.service.DispatchService;
import kd.bos.service.business.datamodel.DynamicFormModelProxy;
import kd.bos.service.lookup.ServiceLookup;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.license.LicenseServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.org.OrgUnitServiceHelper;
import kd.bos.servicehelper.permission.PermissionServiceHelper;
import kd.cus.action.MQMainTemplate;
import kd.cus.entity.OperateEnmu;
import kd.cus.entity.RoleUserMessage;
import kd.cus.entity.UserRoleMessage;
import kd.cus.utils.SelectUtils;
import kd.cus.utils.entity.FilterEntity;

import java.util.*;
import java.util.stream.Collectors;

public class DisposeRoleUserMessage implements MQMainTemplate {
    private static final String BOS_USER_LOGO = "bos_user";
    private static final String BIZ_ROLE_LOGO = "perm_bizrole";
    private static final String ORG_LOGO = "bos_org";
    private static final String BIZROLE_SIGNUSER = "perm_bizroleassignuser";

    @Override
    public String dispose(OperateEnmu operateEnmu, Object data) {
        RoleUserMessage roleUserMessage = JSON.parseObject(((JSONObject) data).toJSONString(), RoleUserMessage.class);
        String codeIds = roleUserMessage.getRoleCode();
        String orgNumber = codeIds.split("_")[0];
//        String roleNumber = codeIds.split("_")[1];
        QFilter[] qFilters = new QFilter[]{new QFilter("number", QCP.equals,codeIds)};
        DynamicObject bizRole_dy = BusinessDataServiceHelper.loadSingleFromCache(BIZ_ROLE_LOGO,qFilters);
        DispatchService service = ServiceLookup.lookup(DispatchService.class,"base");
        DynamicObject org_dynamicObject = BusinessDataServiceHelper.loadSingle(ORG_LOGO, "id", new QFilter[]{new QFilter("number", QCP.equals, orgNumber)});
        if(null != org_dynamicObject){
            long orgId = Long.parseLong(org_dynamicObject.getPkValue().toString());
            switch (operateEnmu){
                case ADD:
                    if(null != bizRole_dy){
                        Long bizRoleId = Long.parseLong(bizRole_dy.getPkValue().toString());
                        String dimType = "DIM_ORG";
                        Map<Long, List<Long>> assignInfo = new HashMap<>();
                        List<FilterEntity> filterEntities = new ArrayList<>();
                        List<String> userCodes = roleUserMessage.getUserCodes();
                        userCodes.forEach(roleCode->{
                            filterEntities.add(new FilterEntity(BOS_USER_LOGO, "number", roleCode));
                        });
                        Map<String, DynamicObject> list = SelectUtils.loadAll(new HashMap<>(), filterEntities);
                        List<Long> userIds = list.entrySet().stream().map(Map.Entry::getValue).map(DynamicObject::getPkValue).map(i->(long)i).collect(Collectors.toList());

                        Map<Class<?>, Object> services = new HashMap<>();
                        DynamicFormModelProxy model = new DynamicFormModelProxy(BIZROLE_SIGNUSER, UUID.randomUUID().toString(), services);
                        model.createNewData();
                        DynamicObject bizroleassignuser_dy = model.getDataEntity(true);

                       return save(bizRoleId,userIds,new ArrayList<>());
//                        bizroleassignuser_dy.set("bizrole",bizRole_dy);
//                        bizroleassignuser_dy.set("usertype","1");
//                        bizroleassignuser_dy.set("userentry",bizRole_dy);
//                        bizroleassignuser_dy.set("bizrole_id",(Long)bizRole_dy.getPkValue());


//                        DynamicObjectCollection user_entry_colDys = bizroleassignuser_dy.getDynamicObjectCollection("user_entry");
//                        for (DynamicObject userDy:userDys){
//                            DynamicObject user_entry_colDy = new DynamicObject(user_entry_colDys.getDynamicObjectType());
//                            user_entry_colDy.set("user",userDy);
//                            user_entry_colDy.set("user_id",(long)userDy.getPkValue());
//                            user_entry_colDys.add(user_entry_colDy);
//                        }
//                        assignInfo.put(orgId,userIds);
//                        Map<Long, Boolean> orgInfo = null;
//                        Boolean ifAdd = true;
//                        String source = "2";
//                        Long bizRoleId = null;
                        //////////////////////////////////////////////////////////////////////////////////////////////////
                        //添加新的角色
//                        Boolean addSuccess = (Boolean)service.invoke("kd.bos.service.ServiceFactory","PermissionService","roleAssignOrgUser",roleId,dimType,assignInfo,orgInfo,ifAdd,source,bizRoleId);
//                        BizRoleUtil.saveRoleUserRelation(this_dy, bizRoleId, userIds, new ArrayList<>());
//                        OperationResult res = OperationServiceHelper.executeOperate("save", "perm_bizroleassignuser", new DynamicObject[]{bizroleassignuser_dy}, OperateOption.create());
                    }else {

                    }
                    break;
                case UPDATE:
                    break;
                case DEL:
                    if(null != bizRole_dy){
                        Long bizRoleId = Long.parseLong(bizRole_dy.getPkValue().toString());
                        String dimType = "DIM_ORG";
                        Map<Long, List<Long>> assignInfo = new HashMap<>();
                        List<FilterEntity> filterEntities = new ArrayList<>();
                        List<String> userCodes = roleUserMessage.getUserCodes();
                        userCodes.forEach(userCode->{
                            filterEntities.add(new FilterEntity(BOS_USER_LOGO, "number", userCode));
                        });
                        Map<String, DynamicObject> list = SelectUtils.loadAll(new HashMap<>(), filterEntities);
                        List<Long> userIds = list.entrySet().stream().map(Map.Entry::getValue).map(DynamicObject::getPkValue).map(i->Long.parseLong(i.toString())).collect(Collectors.toList());
                        return save(bizRoleId,new ArrayList<>(),userIds);
//                        assignInfo.put(orgId,userIds);
//                        Map<Long, Boolean> orgInfo = null;
//                        Boolean ifAdd = false;
//                        String source = "2";
//                        Long bizRoleId = null;
                        //////////////////////////////////////////////////////////////////////////////////////////////////
                        //添加新的角色
//                        PermissionServiceHelper.newRole()
//                        Boolean addSuccess = (Boolean)service.invoke("kd.bos.service.ServiceFactory","PermissionService","roleAssignOrgUser",roleId,dimType,assignInfo,orgInfo,ifAdd,source,bizRoleId);
//                        BizRoleUtil.saveRoleUserRelation(bizRole_dy, bizRoleId, new ArrayList<>(), userIds);
                    }else {
                        return "为找到该业务角色";
                    }
            }
        }else {
            //未存在该组织NUMBER
            return "未存在该组织NUMBER";
        }
        return null;
    }

    private String save(Long bizRoleId, List<Long> adduserDys, List<Long> deluserDys){
        DynamicObject bizRole;// = (DynamicObject)this.getModel().getValue("bizrole");
//        Long bizRoleId = bizRole.getLong("id");

        try {
            bizRole = BusinessDataServiceHelper.loadSingle(bizRoleId, "perm_bizrole");
        } catch (Exception var26) {
//            logger.error("ID为" + bizRoleId + "业务角色分配用户失败", var26);
//            this.getView().showMessage(ResManager.loadKDString("当前业务角色已被删除，请重新选择！", "BizRoleAssignUserPlugin_0", "bos-permission-formplugin", new Object[0]));
            return "当前业务角色已被删除，请重新选择！";
        }

//        DynamicObjectCollection userCol = this.getModel().getEntryEntity("user_entry");
        List<Long> userList = new ArrayList();
        List<Long> userListForLic = new ArrayList();
        List<Long> userListForDel = new ArrayList();

        userList = adduserDys;
        userListForDel = deluserDys;
//        Iterator var7 = userCol.iterator();

//        while(var7.hasNext()) {
//            DynamicObject entry = (DynamicObject)var7.next();
//            DynamicObject user = (DynamicObject)entry.get("user");
//            if (user != null) {
//                userList.add(user.getLong("id"));
//                if (user.getBoolean("enable")) {
//                    userListForLic.add(user.getLong("id"));
//                }
//            }
//        }
        userListForLic = userList;
//        for (DynamicObject user : userDys){
//            if (user != null) {
//                userList.add(user.getLong("id"));
////                if (user.getBoolean("enable")) {
//                    userListForLic.add(user.getLong("id"));
////                }
//            }
//        }

//        QFilter filter = new QFilter("bizrole", "=", bizRoleId);
//        DynamicObject[] relationArr = BusinessDataServiceHelper.load("perm_userbizrole", "user", new QFilter[]{filter});
//        DynamicObject user;
        Long userId;
//        int index;
//        if (relationArr != null && relationArr.length > 0) {
//            for(index = 0; index < relationArr.length; ++index) {
//                user = relationArr[index].getDynamicObject("user");
//                userId = user.getLong("id");
//                userListForDel.add(userId);
//            }
//
//            for(index = 0; index < userListForDel.size(); ++index) {
//                if (userList.contains(userListForDel.get(index))) {
//                    userListForDel.remove(index);
//                    --index;
//                }
//            }
//        }
//
//        if (relationArr != null && relationArr.length > 0) {
//            for(index = 0; index < relationArr.length; ++index) {
//                user = relationArr[index].getDynamicObject("user");
//                userId = user.getLong("id");
//                if (userList.contains(userId)) {
//                    userList.remove(userId);
//                }
//            }
//        }

        List<DynamicObject> list = new ArrayList(500);
        Iterator var31 = userList.iterator();

        while(var31.hasNext()) {
            userId = (Long)var31.next();
            DynamicObject dynamicObj = BusinessDataServiceHelper.newDynamicObject("perm_userbizrole");
            dynamicObj.set("bizrole", bizRoleId);
            dynamicObj.set("user", userId);
            list.add(dynamicObj);
        }

        if (list != null && !list.isEmpty() || userListForDel != null && !userListForDel.isEmpty()) {
            boolean saveSuccess = false;
            String errorMsg = "";
            TXHandle h = TX.required();
            Throwable var13 = null;

            try {
                try {
                    if (userListForDel != null && userListForDel.size() > 0) {
                        DeleteServiceHelper.delete("perm_userbizrole", new QFilter[]{new QFilter("user", "in", userListForDel), new QFilter("bizrole", "=", bizRoleId)});
                    }

                    if (list != null && list.size() > 0) {
                        SaveServiceHelper.save(((DynamicObject)list.get(0)).getDataEntityType(), list.toArray());
                    }

                    BizRoleUtil.saveRoleUserRelation(bizRole, bizRoleId, userList, userListForDel);
                    BizRoleUtil.saveUserFuncPerm(bizRole, bizRoleId, userList, userListForDel);
                    BizRoleUtil.saveUserDisFuncPerm(bizRole, bizRoleId, userList, userListForDel);
                    Map<String, List<String>> appEntity = PermCommonUtil.getAppEntityByBizRole(bizRoleId);
                    if (appEntity != null && !appEntity.isEmpty() && userListForLic != null && !userListForLic.isEmpty()) {
                        LicenseServiceHelper.addUsersLicGroupByBizAppAndBizObj(userListForLic, appEntity);
                    }

                    FieldPermissionCache.removeAllCache();
                    DataPermissionCache.removeAllCache();
                    saveSuccess = true;
                } catch (Exception var24) {
//                    logger.error(var24);
                    h.markRollback();
                    saveSuccess = false;
                    errorMsg = var24.getMessage();
                }
            } catch (Throwable var25) {
                var13 = var25;
                throw var25;
            } finally {
                if (h != null) {
                    if (var13 != null) {
                        try {
                            h.close();
                        } catch (Throwable var23) {
                            var13.addSuppressed(var23);
                        }
                    } else {
                        h.close();
                    }
                }

            }
            if (saveSuccess) {
                PermFormCommonUtil.addLog(ResManager.loadKDString("保存", "BizRoleAssignUserPlugin_4", "bos-permission-formplugin", new Object[0]), ResManager.loadKDString("业务角色分配用户成功", "BizRoleAssignUserPlugin_5", "bos-permission-formplugin", new Object[0]), "perm_userbizrole");
//                this.getView().showSuccessNotification(ResManager.loadKDString("保存成功！", "BizRoleAssignUserPlugin_3", "bos-permission-formplugin", new Object[0]), 3000);
                return "业务角色分配用户成功";
            } else {
                PermFormCommonUtil.addLog(ResManager.loadKDString("保存", "BizRoleAssignUserPlugin_4", "bos-permission-formplugin", new Object[0]), ResManager.loadKDString("业务角色分配用户失败", "BizRoleAssignUserPlugin_6", "bos-permission-formplugin", new Object[0]), "perm_userbizrole");
//                this.getView().showErrorNotification(ResManager.loadKDString("保存失败！", "BizRoleAssignUserPlugin_7", "bos-permission-formplugin", new Object[0]) + errorMsg);
                return "业务角色分配用户失败";
            }

        } else {
            return "";
//            this.getView().showSuccessNotification(ResManager.loadKDString("保存成功！", "BizRoleAssignUserPlugin_3", "bos-permission-formplugin", new Object[0]));
        }
    }
}
