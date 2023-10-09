package cn.oyzh.easyzk.dto;

import cn.oyzh.fx.common.dto.FriendlyInfo;
import cn.oyzh.easyzk.util.ZKACLUtil;
import lombok.NonNull;
import org.apache.zookeeper.data.ACL;

/**
 * zk权限
 *
 * @author oyzh
 * @since 2022/6/7
 */

public class ZKACL extends ACL {

    public ZKACL() {
    }

    public ZKACL(@NonNull ACL acl) {
        this.setId(acl.getId());
        this.setPerms(acl.getPerms());
    }

    public void setPerms(@NonNull String perms) {
        super.setPerms(ZKACLUtil.toPermInt(perms));
    }

    /**
     * 获取id友好对象
     *
     * @return id友好对象
     */
    public FriendlyInfo<ACL> idFriend() {
        return ZKACLUtil.parseId(this.getId());
    }

    /**
     * 获取权限友好对象
     *
     * @return 权限友好对象
     */
    public FriendlyInfo<ACL> permsFriend() {
        return ZKACLUtil.parsePerms(this.getPerms());
    }

    /**
     * 获取协议友好对象
     *
     * @return 协议友好对象
     */
    public FriendlyInfo<ACL> schemeFriend() {
        return ZKACLUtil.parseScheme(this.schemeVal());
    }

    /**
     * id值
     *
     * @return id值
     */
    public String idVal() {
        return this.getId().getId();
    }

    /**
     * 协议值
     *
     * @return 协议值
     */
    public String schemeVal() {
        return this.getId().getScheme();
    }

    /**
     * 是否有权限
     *
     * @param perm 权限类型
     * @return 结果
     */
    public boolean hasPerm(@NonNull String perm) {
        String permStr = ZKACLUtil.toPermStr(this.getPerms());
        return permStr.contains(perm);
    }

    /**
     * 是否有读取权限
     *
     * @return 结果
     */
    public boolean hasReadPerm() {
        return this.hasPerm("r");
    }

    /**
     * 是否有写入权限
     *
     * @return 结果
     */
    public boolean hasWritePerm() {
        return this.hasPerm("w");
    }

    /**
     * 是否有删除权限
     *
     * @return 结果
     */
    public boolean hasDeletePerm() {
        return this.hasPerm("d");
    }

    /**
     * 是否有创建子节点权限
     *
     * @return 结果
     */
    public boolean hasCreatePerm() {
        return this.hasPerm("c");
    }

    /**
     * 是否有特殊权限
     *
     * @return 结果
     */
    public boolean hasAdminPerm() {
        return this.hasPerm("a");
    }

    /**
     * 是否ip权限
     *
     * @return 结果
     */
    public boolean isIPACL() {
        return "ip".equals(this.schemeVal());
    }

    /**
     * 是否digest权限
     *
     * @return 结果
     */
    public boolean isDigestACL() {
        return "digest".equals(this.schemeVal());
    }

    /**
     * 是否只读权限
     *
     * @return 结果
     */
    public boolean isReadOnly() {
        return this.idVal().endsWith(":x");
    }

    /**
     * 是否world权限
     *
     * @return 结果
     */
    public boolean isWorldACL() {
        return "world".equals(this.schemeVal());
    }

    /**
     * 获取摘要用户名
     *
     * @return 摘要用户名
     */
    public String digestUser() {
        if (this.isDigestACL()) {
            return this.idVal().split(":")[0];
        }
        return "";
    }
}
