package cn.oyzh.easyzk.zk;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.client.ZKClientActionArgument;
import cn.oyzh.easyzk.util.ZKACLUtil;
import lombok.experimental.UtilityClass;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2025-01-02
 */
@UtilityClass
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
        List<ZKClientActionArgument> arguments = new ArrayList<>();
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
        List<ZKClientActionArgument> arguments = new ArrayList<>();
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
        List<ZKClientActionArgument> arguments = new ArrayList<>();
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
        List<ZKClientActionArgument> arguments = new ArrayList<>();
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
        List<ZKClientActionArgument> arguments = new ArrayList<>();
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
        List<ZKClientActionArgument> arguments = new ArrayList<>();
        if (watch) {
            arguments.add(ZKClientActionArgument.ofArgument("-w"));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "getAcl", arguments);
    }

    public static void forStatAction(String connectName, String path, boolean watch) {
        List<ZKClientActionArgument> arguments = new ArrayList<>();
        if (watch) {
            arguments.add(ZKClientActionArgument.ofArgument("-w"));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "stat", arguments);
    }

    public static void forGetEphemeralsAction(String connectName, String path) {
        List<ZKClientActionArgument> arguments = new ArrayList<>();
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "getEphemerals", arguments);
    }

    public static void forGetAllChildrenNumberAction(String connectName, String path) {
        List<ZKClientActionArgument> arguments = new ArrayList<>();
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "getAllChildrenNumber", arguments);
    }

    public static void forSyncAction(String connectName, String path) {
        List<ZKClientActionArgument> arguments = new ArrayList<>();
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "sync", arguments);
    }

    public static void forDeleteAction(String connectName, String path, Integer version) {
        List<ZKClientActionArgument> arguments = new ArrayList<>();
        if (version != null) {
            arguments.add(ZKClientActionArgument.ofArgument("-v", version));
        }
        arguments.add(ZKClientActionArgument.ofArgument(path));
        ZKEventUtil.clientAction(connectName, "sync", arguments);
    }
}
