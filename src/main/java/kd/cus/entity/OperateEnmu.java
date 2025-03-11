package kd.cus.entity;

import javax.naming.Name;

public enum OperateEnmu {
    ADD("添加","add"),
    ADDORUPDATE("添加或者更新","addOrUpate"),
    UPDATE("更新","update"),
    DEL("删除","del");

    private String name;
    private String code;

    private OperateEnmu(String name, String code){
        this.name = name;
        this.code = code;
    }

    public static OperateEnmu getOperate(String operate){
        switch (operate){
            case "add":
                return ADD;
            case "addOrUpate":
                return ADDORUPDATE;
            case "update":
                return UPDATE;
            case "del":
                return DEL;
        }
        return null;
    }
}
