package cn.oyzh.easyzk.action;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.util.ZKACLUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2025-01-02
 */

public class ZKClientActionUtil {

    // public static void forAction(Record record) {
    //     String connectName = ThreadLocalUtil.getVal("connectName");
    //     if (connectName != null) {
    //         if (record instanceof SetDataRequest request) {
    //             ZKClientActionArgument arg1 = ZKClientActionArgument.ofArgument("-v", request.getVersion());
    //             ZKClientActionArgument arg2 = ZKClientActionArgument.ofArgument(request.getPath());
    //             ZKClientActionArgument arg3 = ZKClientActionArgument.ofArgument(new String(request.getData()));
    //             ZKEventUtil.clientAction(connectName, "set", arg1, arg2, arg3);
    //         } else if (record instanceof GetDataRequest request) {
    //             if (request.getWatch()) {
    //                 ZKClientActionArgument arg1 = ZKClientActionArgument.ofArgument("-v", request.getWatch());
    //                 ZKClientActionArgument arg2 = ZKClientActionArgument.ofArgument(request.getPath());
    //                 ZKEventUtil.clientAction(connectName, "get", arg1, arg2);
    //             } else {
    //                 ZKClientActionArgument arg2 = ZKClientActionArgument.ofArgument(request.getPath());
    //                 ZKEventUtil.clientAction(connectName, "get", arg2);
    //             }
    //         } else if (record instanceof CreateRequest request) {
    //         }
    //     }
    // }

    public static void forAction(String connectName, String action) {
        ZKEventUtil.clientAction(connectName, action);
    }

    public static void forCreateAction(String connectName, String path, byte[] data, CreateMode createMode, List<? extends ACL> aclList, Long ttl) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(12);
        if (createMode.isSequential()) {
            arguments.add(ZKClientActionArgument.ofArgument("-s"));
        }
        if (createMode.isEphemeral()) {
            arguments.add(ZKClientActionArgument.ofArgument("-e"));
        }
        if (createMode.isContainer()) {
            arguments.add(ZKClientActionArgument.ofArgument("-c"));
        }
        if (ttl != null) {
            arguments.add(ZKClientActionArgument.ofArgument("-t", ttl));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        arguments.add(ZKClientActionArgument.ofArgument(new String(data)));
        arguments.add(ZKClientActionArgument.ofArgument(ZKACLUtil.toAclStr(aclList)));
        ZKEventUtil.clientAction(connectName, "create", arguments);
    }

    public static void forSetAction(String connectName, String path, byte[] data, Integer version, boolean stat) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(12);
        if (stat) {
            arguments.add(ZKClientActionArgument.ofArgument("-s"));
        }
        if (version != null) {
            arguments.add(ZKClientActionArgument.ofArgument("-v", version));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        arguments.add(ZKClientActionArgument.ofArgument(new String(data)));
        arguments.add(ZKClientActionArgument.ofArgument(new String(data)));
        ZKEventUtil.clientAction(connectName, "set", arguments);
    }

    public static void forLsAction(String connectName, String path, boolean stat, boolean watch, boolean recursion) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(12);
        if (stat) {
            arguments.add(ZKClientActionArgument.ofArgument("-s"));
        }
        if (watch) {
            arguments.add(ZKClientActionArgument.ofArgument("-w"));
        }
        if (recursion) {
            arguments.add(ZKClientActionArgument.ofArgument("-R"));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "ls", arguments);
    }

    public static void forGetAction(String connectName, String path, boolean stat, boolean watch) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(12);
        if (stat) {
            arguments.add(ZKClientActionArgument.ofArgument("-s"));
        }
        if (watch) {
            arguments.add(ZKClientActionArgument.ofArgument("-w"));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "get", arguments);
    }

    public static void forSetAclAction(String connectName, String path, boolean stat, boolean recursion, Integer version, List<? extends ACL> aclList) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(12);
        if (stat) {
            arguments.add(ZKClientActionArgument.ofArgument("-s"));
        }
        if (version != null) {
            arguments.add(ZKClientActionArgument.ofArgument("-v", version));
        }
        if (recursion) {
            arguments.add(ZKClientActionArgument.ofArgument("-R"));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        arguments.add(ZKClientActionArgument.ofArgument(ZKACLUtil.toAclStr(aclList)));
        ZKEventUtil.clientAction(connectName, "setAcl", arguments);
    }

    public static void forGetAclAction(String connectName, String path, boolean watch) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(12);
        if (watch) {
            arguments.add(ZKClientActionArgument.ofArgument("-w"));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "getAcl", arguments);
    }

    public static void forStatAction(String connectName, String path, boolean watch) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(12);
        if (watch) {
            arguments.add(ZKClientActionArgument.ofArgument("-w"));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "stat", arguments);
    }

    public static void forGetEphemeralsAction(String connectName, String path) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(12);
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "getEphemerals", arguments);
    }

    public static void forGetAllChildrenNumberAction(String connectName, String path) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(4);
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "getAllChildrenNumber", arguments);
    }

    public static void forSyncAction(String connectName, String path) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(4);
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "sync", arguments);
    }

    public static void forDeleteAction(String connectName, String path, Integer version) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(4);
        if (version != null) {
            arguments.add(ZKClientActionArgument.ofArgument("-v", version));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "sync", arguments);
    }

    public static void forListQuotaAction(String connectName, String path) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(4);
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "listquota", arguments);
    }

    public static void forDelQuotaAction(String connectName, String path, boolean bytes, boolean count) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(8);
        if (bytes) {
            arguments.add(ZKClientActionArgument.ofArgument("-b"));
        }
        if (count) {
            arguments.add(ZKClientActionArgument.ofArgument("-n"));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "delquota", arguments);
    }

    public static void forSetQuotaAction(String connectName, String path, long bytes, long count) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(8);
        if (bytes >= 0) {
            arguments.add(ZKClientActionArgument.ofArgument("-b", bytes));
        }
        if (count >= 0) {
            arguments.add(ZKClientActionArgument.ofArgument("-n", count));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "setquota", arguments);
    }

    public static void forAddAuthAction(String connectName, String scheme, String auth) {
        List<ZKClientActionArgument> arguments = new ArrayList<>(4);
        arguments.add(ZKClientActionArgument.ofArgument(scheme));
        arguments.add(ZKClientActionArgument.ofArgument(auth));
        ZKEventUtil.clientAction(connectName, "addauth", arguments);
    }
}
