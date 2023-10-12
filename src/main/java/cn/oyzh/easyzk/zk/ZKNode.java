package cn.oyzh.easyzk.zk;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.parser.ZKStatParser;
import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.fx.common.dto.FriendlyInfo;
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
    private ObjectProperty<StatsTrack> quotaProperty;

    /**
     * 获取配额属性
     *
     * @return 配额属性
     */
    public ObjectProperty<StatsTrack> quotaProperty() {
        if (this.quotaProperty == null) {
            this.quotaProperty = new SimpleObjectProperty<>();
        }
        return this.quotaProperty;
    }

    /**
     * 获取配额
     *
     * @return 配额
     */
    public StatsTrack quota() {
        return this.quotaProperty == null ? null : quotaProperty.get();
    }

    /**
     * 设置配额
     *
     * @param track 配额
     */
    public void quota(StatsTrack track) {
        this.quotaProperty().set(track);
    }

    /**
     * acl权限属性
     */
    private ObjectProperty<List<ZKACL>> aclProperty;

    /**
     * 获取acl权限属性
     *
     * @return acl权限属性
     */
    public ObjectProperty<List<ZKACL>> aclProperty() {
        if (this.aclProperty == null) {
            this.aclProperty = new SimpleObjectProperty<>();
        }
        return this.aclProperty;
    }

    /**
     * 获取acl权限
     *
     * @return acl权限
     */
    public List<ZKACL> acl() {
        return this.aclProperty == null ? Collections.emptyList() : aclProperty.get();
    }

    /**
     * 设置acl权限
     *
     * @param aclList acl权限
     */
    public void acl(List<? extends ACL> aclList) {
        if (CollUtil.isEmpty(aclList)) {
            this.aclProperty().set(Collections.emptyList());
        } else {
            List<ZKACL> list = new ArrayList<>(aclList.size());
            for (ACL acl : aclList) {
                if (ZKACLUtil.isOpenACL(acl)) {
                    list.add(new ZKACL(ZKACLUtil.OPEN_ACL));
                } else {
                    list.add(new ZKACL(acl));
                }
            }
            this.aclProperty().set(list);
        }
    }

    /**
     * 状态属性
     */
    private ObjectProperty<Stat> statProperty;

    /**
     * 获取状态属性
     *
     * @return 状态属性
     */
    public ObjectProperty<Stat> statProperty() {
        if (this.statProperty == null) {
            this.statProperty = new SimpleObjectProperty<>();
        }
        return this.statProperty;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public Stat stat() {
        return this.statProperty == null ? null : statProperty.get();
    }

    /**
     * 设置状态
     *
     * @param stat 状态
     */
    public void stat(Stat stat) {
        this.statProperty().set(stat);
    }

    /**
     * 节点路径
     */
    @Getter
    @Setter
    private String nodePath;

    /**
     * 节点数据属性
     */
    private ObjectProperty<byte[]> nodeDataProperty;

    /**
     * 获取节点数据属性
     *
     * @return 节点数据属性
     */
    public ObjectProperty<byte[]> nodeDataProperty() {
        if (this.nodeDataProperty == null) {
            this.nodeDataProperty = new SimpleObjectProperty<>();
        }
        return this.nodeDataProperty;
    }

    /**
     * 获取节点数据
     *
     * @return 节点数据
     */
    public byte[] nodeData() {
        return this.nodeDataProperty == null ? null : nodeDataProperty.get();
    }

    /**
     * 设置节点数据
     *
     * @param bytes 节点数据
     */
    public void nodeData(byte[] bytes) {
        this.nodeDataProperty().set(bytes);
    }

    /**
     * 获取节点数据字符串
     *
     * @return 节点数据字符串
     */
    public String nodeDataStr() {
        return this.nodeDataStr(Charset.defaultCharset());
    }

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
     * 节点数据是否加载
     *
     * @return 结果
     */
    public boolean nodeDataLoaded() {
        return this.nodeDataProperty != null;
    }

    /**
     * 复制对象
     *
     * @param node zk节点
     * @return 当前对象
     */
    public ZKNode copy(@NonNull ZKNode node) {
        this.stat(node.stat());
        this.acl(node.acl());
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
        if (StrUtil.containsAny(this.nodePath, "%", "+")) {
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
        if (StrUtil.containsAny(nodeName, "%", "+")) {
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
    public boolean persistent() {
        return !this.ephemeral();
    }

    /**
     * 是否临时节点
     *
     * @return 结果
     */
    public boolean ephemeral() {
        return this.stat() != null && this.stat().getEphemeralOwner() > 0;
    }

    /**
     * 是否dubbo节点
     *
     * @return 结果
     */
    public boolean dubbo() {
        return this.nodePath() != null && this.nodePath().startsWith("/dubbo");
    }

    /**
     * 是否父节点
     *
     * @return 结果
     */
    public boolean parentNode() {
        return this.stat() != null && this.stat().getNumChildren() > 0;
    }

    /**
     * 是否子节点
     *
     * @return 结果
     */
    public boolean subNode() {
        return !this.parentNode();
    }

    /**
     * 是否根节点
     *
     * @return 结果
     */
    public boolean rootNode() {
        return "/".equals(this.nodePath());
    }

    /**
     * 友好状态信息
     *
     * @return 友好状态信息
     */
    public List<FriendlyInfo<Stat>> statInfos() {
        return this.stat() == null ? Collections.emptyList() : ZKStatParser.INSTANCE.parse(this.stat());
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
        return CollUtil.isEmpty(this.acl());
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
        if (!this.aclEmpty()) {
            type = type.toLowerCase();
            for (ZKACL acl : this.acl()) {
                if (acl.schemeVal().equals(type)) {
                    return true;
                }
            }
        }
        return false;
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
}
