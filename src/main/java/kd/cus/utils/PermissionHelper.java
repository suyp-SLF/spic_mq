package kd.cus.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityType;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.orm.query.QFilter;
import kd.bos.orm.util.CollectionUtils;
import kd.bos.permission.cache.DataPermissionCache;
import kd.bos.permission.cache.FieldPermissionCache;
import kd.bos.permission.cache.RolePermissionCache;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.permission.constant.entity.NormalConst;
import kd.bos.servicehelper.permission.constant.entity.RolePermConst;
import kd.bos.util.StringUtils;

public class PermissionHelper {

	/**
	 * 给角色分配功能权限
	 * @param roleId   角色ID
	 * @param appEntityPermItemMap  应用ID，实体编码和权限项ID的映射 
	 * @param addOrRemove true 为增加权限项， false为删除权限项
	 * @param processInfo 处理信息
	 * @return 如果分配失败，则返回false，否则为true。
	 */
	public static boolean roleAssignFuncPerm(String roleId, Map<String, Map<String, List<String>>> appEntityPermItemMap,  
			boolean addOrRemove, StringBuilder processInfo) {

		if (StringUtils.isEmpty(roleId)) {
			processInfo.append(ResManager.loadKDString("roleId不能为空", "RoleCache_13", "bos-permission-cache"));
			return false;
		}

		String selProp = new StringBuilder()
				.append(NormalConst.ID).append(",")
				.append(RolePermConst.ENTRY_PROP_BIZAPP).append(",")
				.append(RolePermConst.ENTRY_PROP_ENTITYTYPE).append(",")
				.append(RolePermConst.ENTRY_PROP_PERMITEM).append(",")
				.toString();

		DynamicObject[] rolePermDoc = BusinessDataServiceHelper.load(
				RolePermConst.MAIN_ENTITY_TYPE, 
				selProp, 
				new QFilter[]{new QFilter(RolePermConst.PROP_ROLE, "=", roleId)});

		DynamicObject rolePermObj = null;
		if(rolePermDoc!=null && rolePermDoc.length>0) {
			rolePermObj = (DynamicObject)rolePermDoc[0];
		} else {
			rolePermObj = BusinessDataServiceHelper.newDynamicObject(RolePermConst.MAIN_ENTITY_TYPE);	
			rolePermObj.set(RolePermConst.PROP_ROLE, roleId);
		}

		DynamicObjectCollection permItemCol = rolePermObj.getDynamicObjectCollection(RolePermConst.ENTRY_ROLEPERM);
		DynamicObject permItemObj = null;

		IDataEntityType entryType = permItemCol.getDynamicObjectType();

		if(addOrRemove) {  //授予权限项
			//遍历已有的权限项，把准备分配的内容移除掉(避免重复授权)
			for (DynamicObject dObj : permItemCol) {
				String bizappid   = dObj.getString(RolePermConst.ENTRY_PROP_BIZAPP + ".id");

				String entityNum  = dObj.getString(RolePermConst.ENTRY_PROP_ENTITYTYPE + ".id");
				String permItemId = dObj.getString(RolePermConst.ENTRY_PROP_PERMITEM + ".id");	

				Map<String, List<String>> entPermItemMap = appEntityPermItemMap.get(bizappid);
				if(entPermItemMap!=null) {
					List<String> permItemIds = entPermItemMap.get(entityNum);
					if(!CollectionUtils.isEmpty(permItemIds)) {
						if(permItemIds.contains(permItemId)) {
							permItemIds.remove(permItemId);

							if(permItemIds.size()==0) {
								entPermItemMap.remove(entityNum);
								if(entPermItemMap.size()==0) {
									appEntityPermItemMap.remove(bizappid);
								}
							}
						}
					}
				}
			}


			// 遍历即将授权的权限项，准备好新数据。
			Map<String, List<String>> appEntity = new HashMap<>();
			for (Map.Entry<String, Map<String, List<String>>> entry : appEntityPermItemMap.entrySet()) {
				String appId = entry.getKey();
				Map<String, List<String>> entityPermItemMap = entry.getValue();
				for (Map.Entry<String, List<String>> subEntry : entityPermItemMap.entrySet()) {
					String entityNum = subEntry.getKey();
					List<String> permItemIds = subEntry.getValue();

					for (String permItemId : permItemIds) {
						permItemObj = new DynamicObject((DynamicObjectType)entryType);	

						permItemObj.set(RolePermConst.ENTRY_PROP_BIZAPP,     appId);
						permItemObj.set(RolePermConst.ENTRY_PROP_ENTITYTYPE, entityNum);
						permItemObj.set(RolePermConst.ENTRY_PROP_PERMITEM,   permItemId);				
						permItemCol.add(permItemObj);
					}

					appEntity.computeIfAbsent(appId, k -> new ArrayList<>()).add(entityNum);
				}
			};

		} else {  //收回权限项
			//遍历已有的权限项，把准备分配的内容移除掉(避免重复授权)
			for (Iterator<DynamicObject> iterator = permItemCol.iterator(); iterator.hasNext();) {
				DynamicObject oldPermItemObj =  iterator.next();

				String bizappid   = oldPermItemObj.getString(RolePermConst.ENTRY_PROP_BIZAPP + ".id");
				String entityNum  = oldPermItemObj.getString(RolePermConst.ENTRY_PROP_ENTITYTYPE + ".id");
				String permItemId = oldPermItemObj.getString(RolePermConst.ENTRY_PROP_PERMITEM + ".id");

				Map<String, List<String>> entPermItemMap = appEntityPermItemMap.get(bizappid);
				if(entPermItemMap!=null) {
					List<String> permItemIds = entPermItemMap.get(entityNum);
					if(!CollectionUtils.isEmpty(permItemIds)) {
						if(permItemIds.contains(permItemId)) {
							iterator.remove();
						}
					}
				}
			}
		}

		// 保存角色功能权限
		SaveServiceHelper.save(new DynamicObject[]{rolePermObj});

		//		// 同步分配许可
		//		List<Long> userIDs = PermCommonUtil.getUserByRole(roleId);
		//		if (userIDs != null && userIDs.size() > 0 && appEntity != null && appEntity.size() > 0) {
		//			LicenseServiceHelper.addUsersLicGroupByBizAppAndBizObj(userIDs, appEntity);
		//		}

		processInfo.append(ResManager.loadKDString("保存成功", "RoleCache_4", "bos-permission-cache"));

		RolePermissionCache.reloadCache(roleId);
		//		List<Object> ids = new ArrayList<Object>();
		//		ids.add(roleId);

		//	TODO 以下这句要转移到PermCommonUtil
		//		PermFormCommonUtil.refreshUserAppCache(ids);

		FieldPermissionCache.removeAllCache();
		DataPermissionCache.removeAllCache();

		return true;
	}

//	public static boolean roleAssignFuncPerm(String roleId, Map<String, List<String>> appEntityPermItemMap,
//											 boolean addOrRemove, StringBuilder processInfo) {
//
//		Iterator<Map.Entry<String, List<String>>> appEntityPermItemMapIt = appEntityPermItemMap.entrySet().iterator();
//		while (appEntityPermItemMapIt.hasNext()){
//			appEntityPermItemMapEX
//		}
//		Map<String, Map<String, List<String>>> appEntityPermItemMapEX = new HashMap<>();
//		return roleAssignFuncPerm(roleId, appEntityPermItemMapEX, addOrRemove, processInfo);
//	}
}
