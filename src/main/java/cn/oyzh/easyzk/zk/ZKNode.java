package cn.oyzh.easyzk.zk;

import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.fx.common.dto.FriendlyInfo;
import cn.oyzh.fx.common.util.CollectionUtil;
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.common.util.TextUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * zk节点信息
 *
 * @author oyzh
 * @since 2020/3/6
 */
@ToString(callSuper = true)
@Accessors(fluent = true, chain = true)
public class ZKNode implements Comparable<ZKNode> {

    /**
     * 配额属性
     */
    @Getter
    @Setter
    private StatsTrack quota;

    /**
     * acl权限属性
     */
    @Getter
    @Setter
    private List<ZKACL> acl;

    /**
     * 设置acl权限
     *
     * @param aclList acl权限
     */
    public void acl(List<? extends ACL> aclList) {
        if (CollectionUtil.isEmpty(aclList)) {
            this.acl = Collections.emptyList();
        } else {
            this.acl = new ArrayList<>(aclList.size());
            for (ACL acl : aclList) {
                if (ZKACLUtil.isOpenACL(acl)) {
                    this.acl.add(new ZKACL(ZKACLUtil.OPEN_ACL));
                } else {
                    this.acl.add(new ZKACL(acl));
                }
            }
        }
    }

    /**
     * 状态属性
     */
    @Getter
    @Setter
    private Stat stat;

    /**
     * 加载耗时
     */
    @Getter
    @Setter
    private short loadTime;

    /**
     * 节点路径
     */
    @Getter
    @Setter
    private String nodePath;

    /**
     * 节点数据属性
     */
    @Getter
    @Setter
    private byte[] nodeData;

    /**
     * 节点值字符串
     *
     * @param charset 字符集
     * @return 节点值字符串
     */
    public String nodeDataStr(String charset) {
        return this.nodeDataStr(TextUtil.getCharset(charset));
    }

    /**
     * 节点值字符串
     *
     * @param charset 字符集
     * @return 节点值字符串
     */
    public String nodeDataStr(Charset charset) {
        byte[] bytes = this.nodeData();
        if (bytes == null) {
            return null;
        }
        if (bytes.length == 0) {
            return "";
        }
        if (charset == null) {
            return new String(bytes);
        }
        return new String(bytes, charset);
    }

    /**
     * 复制节点
     *
     * @param node zk节点
     * @return 当前对象
     */
    public ZKNode copy(@NonNull ZKNode node) {
        this.acl(node.acl());
        this.stat(node.stat());
        this.nodePath = node.nodePath;
        this.nodeData(node.nodeData());
        return this;
    }

    /**
     * 解码的节点路径
     *
     * @return 解码的节点路径
     */
    public String decodeNodePath() {
        if (StringUtil.containsAny(this.nodePath, "%", "+")) {
            return URLDecoder.decode(this.nodePath, Charset.defaultCharset());
        }
        return this.nodePath;
    }

    /**
     * 解码的节点名称
     *
     * @return 解码的节点名称
     */
    public String decodeNodeName() {
        String nodeName = this.nodeName();
        if (StringUtil.containsAny(nodeName, "%", "+")) {
            return URLDecoder.decode(nodeName, Charset.defaultCharset());
        }
        return nodeName;
    }

    /**
     * 节点名称
     *
     * @return 节点名称
     */
    public String nodeName() {
        return ZKNodeUtil.getName(this.nodePath);
    }

    /**
     * 是否持久节点
     *
     * @return 结果
     */
    public boolean isPersistent() {
        return !this.isEphemeral();
    }

    /**
     * 是否临时节点
     *
     * @return 结果
     */
    public boolean isEphemeral() {
        return this.stat() != null && this.stat().getEphemeralOwner() > 0;
    }

    /**
     * 是否dubbo节点
     *
     * @return 结果
     */
    public boolean isDubbo() {
        return this.nodePath() != null && this.nodePath().startsWith("/dubbo");
    }

    /**
     * 是否父节点
     *
     * @return 结果
     */
    public boolean isParent() {
        return this.stat() != null && this.stat().getNumChildren() > 0;
    }

    /**
     * 是否子节点
     *
     * @return 结果
     */
    public boolean isChildren() {
        return !this.isParent();
    }

    /**
     * 是否根节点
     *
     * @return 结果
     */
    public boolean isRoot() {
        return "/".equals(this.nodePath());
    }

    /**
     * 友好状态信息
     *
     * @return 友好状态信息
     */
    public List<FriendlyInfo<Stat>> statInfos() {
        return this.stat() == null ? Collections.emptyList() : ZKStatParser.INSTANCE.apply(this.stat());
    }

    @Override
    public int compareTo(ZKNode node) {
        if (node == null || node.nodePath() == null) {
            return -1;
        }
        return this.nodePath().compareToIgnoreCase(node.nodePath());
    }

    /**
     * 是否有权限
     *
     * @param perm 权限名称
     * @return 结果
     */
    public boolean hasPerm(@NonNull String perm) {
        if (!this.aclEmpty()) {
            for (ZKACL zkacl : this.acl()) {
                if (zkacl.isDigestACL() && zkacl.isReadOnly()) {
                    return false;
                }
                return zkacl.hasPerm(perm);
            }
        }
        return false;
    }

    /**
     * acl是否为空
     *
     * @return 结果
     */
    public boolean aclEmpty() {
        return CollectionUtil.isEmpty(this.acl());
    }

    /**
     * 是否缺失权限
     *
     * @return 结果
     */
    public boolean lackPerm() {
        return !this.hasReadPerm() || !this.hasWritePerm() || !this.hasCreatePerm() || !this.hasAdminPerm();
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
     * 是否有权限
     *
     * @param type 权限类型
     * @return 结果
     */
    public boolean hasACL(@NonNull String type) {
        return this.acl().parallelStream().anyMatch(a -> a.schemeVal().equalsIgnoreCase(type));
    }

    /**
     * 按照类型获取权限列表
     *
     * @param type 类型
     * @return 权限列表
     */
    public List<ZKACL> getACLByType(@NonNull String type) {
        if (!this.aclEmpty()) {
            type = type.toLowerCase();
            List<ZKACL> aclList = new ArrayList<>();
            for (ZKACL acl : this.acl()) {
                if (acl.schemeVal().equals(type)) {
                    aclList.add(acl);
                }
            }
            return aclList;
        }
        return Collections.emptyList();
    }

    /**
     * 是否有开放权限
     *
     * @return 结果
     */
    public boolean hasWorldACL() {
        return this.hasACL("world");
    }

    /**
     * 是否有IP权限
     *
     * @return 结果
     */
    public boolean hasIPACL() {
        return this.hasACL("ip");
    }

    /**
     * 是否存在IP权限
     *
     * @param ip ip内容
     * @return 结果
     */
    public boolean existIPACL(@NonNull String ip) {
        if (this.hasIPACL()) {
            List<ZKACL> acLs = this.getACLByType("ip");
            for (ZKACL acL : acLs) {
                if (acL.idVal().equals(ip)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否有摘要权限
     *
     * @return 结果
     */
    public boolean hasDigestACL() {
        return this.hasACL("digest");
    }

    /**
     * 是否存在摘要权限
     *
     * @param digest 摘要
     * @return 结果
     */
    public boolean existDigestACL(@NonNull String digest) {
        if (this.hasDigestACL()) {
            for (ZKACL acl : this.getDigestACLs()) {
                if (Objects.equals(acl.idVal(), digest)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取摘要权限列表
     *
     * @return 摘要权限列表
     */
    public List<ZKACL> getDigestACLs() {
        return this.getACLByType("digest");
    }

    /**
     * 获取子节点数量
     *
     * @return 子节点数量
     */
    public int getNumChildren() {
        return this.stat() == null ? 0 : this.stat().getNumChildren();
    }

    /**
     * 是否有子节点
     *
     * @return 结果
     */
    public boolean hasChildren() {
        return this.getNumChildren() > 0;
    }

    /**
     * 节点比较
     *
     * @param node 目标节点
     * @return 结果
     */
    public boolean nodeEquals(ZKNode node) {
        return node != null && StringUtil.equals(this.nodePath(), node.nodePath());
    }
}
