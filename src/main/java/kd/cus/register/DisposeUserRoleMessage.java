package kd.cus.register;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.db.tx.TX;
import kd.bos.db.tx.TXHandle;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.permission.api.HasPermOrgResult;
import kd.bos.permission.cache.DataPermissionCache;
import kd.bos.permission.cache.FieldPermissionCache;
import kd.bos.permission.cache.util.PermCommonUtil;
import kd.bos.permission.formplugin.BizRoleUtil;
import kd.bos.permission.formplugin.util.PermFormCommonUtil;
import kd.bos.service.DispatchService;
import kd.bos.service.lookup.ServiceLookup;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.license.LicenseServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.org.OrgUnitServiceHelper;
import kd.bos.servicehelper.permission.PermissionServiceHelper;
import kd.cus.action.MQMainTemplate;
import kd.cus.entity.OperateEnmu;
import kd.cus.entity.RoleMessage;
import kd.cus.entity.UserRoleMessage;
import kd.cus.utils.SelectUtils;
import kd.cus.utils.entity.FilterEntity;

import java.util.*;
import java.util.stream.Collectors;

public class DisposeUserRoleMessage implements MQMainTemplate {
    private static final String BOS_USER_LOGO = "bos_user";
    private static final String BIZ_ROLE_LOGO = "perm_bizrole";
    private static final String ORG_LOGO = "bos_org";

    private static String fetchGroupKey(String str) {
        return str.split("_")[0];
    }

    @Override
    public String dispose(OperateEnmu operateEnmu, Object data) {

        switch (operateEnmu) {
            case ADD:
                break;
            case UPDATE:
                UserRoleMessage userRoleMessage = JSON.parseObject(((JSONObject) data).toJSONString(), UserRoleMessage.class);
                String number = userRoleMessage.getUserCode();
                QFilter[] qFilters = new QFilter[]{new QFilter("number", QCP.equals, number)};
                DynamicObject this_dy = BusinessDataServiceHelper.loadSingleFromCache(BOS_USER_LOGO, qFilters);
                if (null != this_dy) {
                    DispatchService service = ServiceLookup.lookup(DispatchService.class, "base");
                    Long userId = (Long) this_dy.getPkValue();
                    String dimType = "DIM_ORG";
                    Map<Long, List<String>> assignInfo = new HashMap<>();
                    List<FilterEntity> filterEntities = new ArrayList<>();
                    List<String> codeIds = userRoleMessage.getRoleCodes();
                    for(String codeId : codeIds) {
                        filterEntities.add(new FilterEntity(BIZ_ROLE_LOGO, "number", codeId));

                    }
                    Map<String, DynamicObject> list = SelectUtils.loadAll(new HashMap<>(), filterEntities);
                    StringBuffer res = new StringBuffer();
                    list.entrySet().forEach(item->{
                        res.append(save((long)item.getValue().getPkValue(), userId));
                    });
                    return res.toString();
//                    Map<String, List<String>> cateMap = codeIds.stream().collect(Collectors.groupingBy(e -> fetchGroupKey(e)));
//                    Iterator<Map.Entry<String, List<String>>> cateMapIt = cateMap.entrySet().iterator();
//                    if (cateMap.size() > 0) {
//                        while (cateMapIt.hasNext()) {
//                            Map.Entry<String, List<String>> item = cateMapIt.next();
//                            List<String> values = new ArrayList<>();
//                            String orgNumber = item.getKey();
//                            DynamicObject org_dynamicObject = BusinessDataServiceHelper.loadSingle(ORG_LOGO, "id", new QFilter[]{new QFilter("number", QCP.equals, orgNumber)});
//                            if (null != org_dynamicObject) {
//                                long orgId = Long.parseLong(org_dynamicObject.getPkValue().toString());
//                                for (String codeId : item.getValue()) {
//                                    String roleNumber = codeId.split("_")[1];
//                                    filterEntities.add(new FilterEntity(ROLE_LOGO, "number", roleNumber));
//                                }
//                                Map<String, DynamicObject> list = SelectUtils.loadAll(new HashMap<>(), filterEntities);
//                    list.entrySet().forEach(dy->{
//                        roleIds.add(dy.getValue().getPkValue().toString());
//                    });
//                                List<String> roleIds = list.entrySet().stream().map(Map.Entry::getValue).map(DynamicObject::getPkValue).map(i -> i.toString()).collect(Collectors.toList());
//
//                                assignInfo.put(orgId, roleIds);
//                                Map<Long, Boolean> orgInfo = null;
//                                Boolean ifAdd = true;
//                                String source = "2";
//                                Long bizRoleId = null;
//                                //////////////////////////////////////////////////////////////////////////////////////////////////
//                                //获得所有权限角色，并清除所有角色(待定逻辑)
//                                Map<Object, DynamicObject> allRoles = BusinessDataServiceHelper.loadFromCache(ROLE_LOGO, new QFilter[]{QFilter.isNotNull("number")});
//                                List<String> allRolesList = allRoles.entrySet().stream().map(Map.Entry::getValue).map(DynamicObject::getPkValue).map(i -> i.toString()).collect(Collectors.toList());
//                                Map<Long, List<String>> allAssignInfo = new HashMap<>();
//                                allAssignInfo.put(orgId, allRolesList);
//                                Boolean delSuccess = (Boolean) service.invoke("kd.bos.service.ServiceFactory", "PermissionService", "userAssignRole", userId, dimType, allAssignInfo, orgInfo, false, source, bizRoleId);
//                    List<String> hasPermAppIds = (List<String>)service.invoke("kd.bos.service.ServiceFactory", "PermissionService", "getUserBizApps", userId);
//                                //添加新的角色
//                                Boolean addSuccess = (Boolean) service.invoke("kd.bos.service.ServiceFactory", "PermissionService", "userAssignRole", userId, dimType, assignInfo, orgInfo, ifAdd, source, bizRoleId);
//                                return number;
//                            }
//                        }
//                    } else {
//                        Set<String> rolesByUsers = PermissionServiceHelper.getRolesByUser(userId);
//                        filterEntities = new ArrayList<>();
//                        for (String rolesByUser : rolesByUsers) {
//                            filterEntities.add(new FilterEntity(BIZ_ROLE_LOGO, "id", rolesByUser));
//                        }
//                        Map<String, DynamicObject> list = SelectUtils.loadAll(new HashMap<>(), filterEntities);
//                            HasPermOrgResult hasPermOrgResult =(HasPermOrgResult) service.invoke("kd.bos.service.ServiceFactory", "PermissionService", "getUserOrgs", );//getUserHasPermOrgs

                        //清除所有权限
//                    }
                } else {
                    //没找到人员
                    return "没找到人员";
                }
            case DEL:
                break;
        }
        return "";
    }

    private String save(Long bizRoleId,Long tuserId) {
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
//        Iterator var7 = userCol.iterator();
        userList.add(tuserId);
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
        userListForLic.add(tuserId);

        QFilter filter = new QFilter("bizrole", "=", bizRoleId);
        DynamicObject[] relationArr = BusinessDataServiceHelper.load("perm_userbizrole", "user", new QFilter[]{filter});
        DynamicObject user;
        Long userId;
        int index;
        if (relationArr != null && relationArr.length > 0) {
            for(index = 0; index < relationArr.length; ++index) {
                user = relationArr[index].getDynamicObject("user");
                userId = user.getLong("id");
                userListForDel.add(userId);
            }

            for(index = 0; index < userListForDel.size(); ++index) {
                if (userList.contains(userListForDel.get(index))) {
                    userListForDel.remove(index);
                    --index;
                }
            }
        }

        if (relationArr != null && relationArr.length > 0) {
            for(index = 0; index < relationArr.length; ++index) {
                user = relationArr[index].getDynamicObject("user");
                userId = user.getLong("id");
                if (userList.contains(userId)) {
                    userList.remove(userId);
                }
            }
        }

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
