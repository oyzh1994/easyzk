package cn.oyzh.easyzk.util;

import cn.hutool.core.util.NumberUtil;
import cn.oyzh.easyzk.exception.ZKException;
import cn.oyzh.fx.common.dto.FriendlyInfo;
import cn.oyzh.fx.common.util.RegexUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * acl工具类
 *
 * @author oyzh
 * @since 2022/6/2
 */
@UtilityClass
public class ZKACLUtil {

    /**
     * 开放的acl权限
     */
    public static ACL OPEN_ACL = ZooDefs.Ids.OPEN_ACL_UNSAFE.get(0);

    /**
     * 是否开放的acl权限
     *
     * @param acl acl
     * @return 结果
     */
    public static boolean isOpenACL(ACL acl) {
        if (acl == null) {
            return false;
        }
        if (OPEN_ACL.getPerms() != acl.getPerms() || acl.getId() == null) {
            return false;
        }
        if (OPEN_ACL.getId() == acl.getId()) {
            return true;
        }
        return Objects.equals(OPEN_ACL.getId().getId(), acl.getId().getId()) && Objects.equals(OPEN_ACL.getId().getScheme(), acl.getId().getScheme());
    }

    /**
     * 解析id
     *
     * @param idVal id值
     * @return id信息
     */
    public static FriendlyInfo<ACL> parseId(@NonNull Id idVal) {
        FriendlyInfo<ACL> idInfo = new FriendlyInfo<>();
        idInfo.name("id");
        idInfo.friendlyName("标识");
        idInfo.value(idVal.getId());
        if (idVal.getScheme().equalsIgnoreCase("WORLD")) {
            idInfo.friendlyValue("所有人");
        } else {
            idInfo.friendlyValue(idVal.getId());
        }
        return idInfo;
    }

    /**
     * 解析认证方式
     *
     * @param schemeStr 认证方式字符
     * @return 认证方式信息
     */
    public static FriendlyInfo<ACL> parseScheme(@NonNull String schemeStr) {
        FriendlyInfo<ACL> scheme = new FriendlyInfo<>();
        scheme.name("scheme");
        scheme.friendlyName("认证");
        scheme.value(schemeStr);
        switch (schemeStr.toUpperCase()) {
            case "IP" -> scheme.friendlyValue("IP认证");
            case "WORLD" -> scheme.friendlyValue("开放认证");
            case "DIGEST" -> scheme.friendlyValue("摘要认证");
            default -> scheme.friendlyValue("未知");
        }
        return scheme;
    }

    /**
     * 解析权限
     *
     * @param permsVal 权限值
     * @return 权限信息
     */
    public static FriendlyInfo<ACL> parsePerms(int permsVal) {
        String permsString = toPermStr(permsVal);
        StringBuilder valueBuilder = new StringBuilder();
        StringBuilder friendlyValueBuilder = new StringBuilder();
        if (permsString.contains("a")) {
            valueBuilder.append("a");
            friendlyValueBuilder.append(",特权");
        }
        if (permsString.contains("w")) {
            valueBuilder.append("w");
            friendlyValueBuilder.append(",写入");
        }
        if (permsString.contains("r")) {
            valueBuilder.append("r");
            friendlyValueBuilder.append(",读取");
        }
        if (permsString.contains("d")) {
            valueBuilder.append("d");
            friendlyValueBuilder.append(",删除子节点");
        }
        if (permsString.contains("c")) {
            valueBuilder.append("c");
            friendlyValueBuilder.append(",创建子节点");
        }

        FriendlyInfo<ACL> perms = new FriendlyInfo<>();
        perms.name("perms");
        perms.friendlyName("权限");
        perms.value(valueBuilder.toString());
        if (friendlyValueBuilder.toString().startsWith(",")) {
            perms.friendlyValue(friendlyValueBuilder.substring(1));
        } else {
            perms.friendlyValue("");
        }
        return perms;
    }

    /**
     * 解析权限
     *
     * @param acl 权限内容
     * @return 权限列表
     */
    public List<ACL> parseAcl(String acl) {
        if (acl == null || acl.isEmpty()) {
            return ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }
        List<ACL> aclList = new ArrayList<>();
        for (String str : acl.split(",")) {
            if (str.isEmpty()) {
                continue;
            }
            String[] strings = str.split(":");
            Id id = new Id();
            ACL a = new ACL();
            if (strings.length > 0) {
                id.setScheme(strings[0]);
            }
            if (strings.length > 1) {
                id.setId(strings[1]);
            }
            if (strings.length > 2) {
                a.setPerms(toPermInt(strings[2]));
            }
            a.setId(id);
            aclList.add(a);
        }
        return aclList;
    }

    /**
     * 解析权限为int
     *
     * @param perms 权限
     * @return int值
     */
    public static int toPermInt(@NonNull String perms) {
        perms = perms.trim().toLowerCase();
        if (perms.isEmpty() || perms.length() > 5) {
            throw new ZKException("无效的ACL权限数据！");
        }
        int permsInt = 0;
        for (char c : perms.toCharArray()) {
            if (c == 'a') {
                permsInt += ZooDefs.Perms.ADMIN;
            } else if (c == 'r') {
                permsInt += ZooDefs.Perms.READ;
            } else if (c == 'w') {
                permsInt += ZooDefs.Perms.WRITE;
            } else if (c == 'c') {
                permsInt += ZooDefs.Perms.CREATE;
            } else if (c == 'd') {
                permsInt += ZooDefs.Perms.DELETE;
            } else {
                throw new ZKException("[" + c + "]是无效的Acl权限！");
            }
        }
        return permsInt;
    }

    /**
     * 解析权限为字符串
     *
     * @param permInt int值权限
     * @return 字符串权限
     */
    public static String toPermStr(int permInt) {
        StringBuilder permStr = new StringBuilder(NumberUtil.getBinaryStr(permInt));
        int i = 5 - permStr.length();
        if (i > 5 || i < 0) {
            throw new ZKException("无效的Acl权限值！");
        }
        while (i-- > 0) {
            permStr.insert(0, "0");
        }
        StringBuilder builder = new StringBuilder();
        if (permStr.charAt(0) == '1') {
            builder.append("a");
        }
        if (permStr.charAt(1) == '1') {
            builder.append("d");
        }
        if (permStr.charAt(2) == '1') {
            builder.append("c");
        }
        if (permStr.charAt(3) == '1') {
            builder.append("w");
        }
        if (permStr.charAt(4) == '1') {
            builder.append("r");
        }
        return builder.toString();
    }

    /**
     * 检查ip
     *
     * @param ip ip数据
     */
    public static void checkIP(@NonNull String ip) {
        String[] segments = ip.split("/");
        if (!RegexUtil.isIPV4(segments[0])) {
            throw new ZKException(segments[0] + "是无效的IPV4地址，格式应为a.x.x.x（a范围1~255，x范围0~255）");
        }
        if (segments.length == 2 && !segments[0].endsWith("0")) {
            throw new ZKException(segments[0] + "是无效的IPv4网段，格式应为a.x.x.0（a范围1~255，x范围0~255）");
        }
    }
}
