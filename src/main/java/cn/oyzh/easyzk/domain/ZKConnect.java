package cn.oyzh.easyzk.domain;

import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import cn.oyzh.common.util.BooleanUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.ObjectComparator;
import cn.oyzh.common.util.StringUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * zk连接
 *
 * @author oyzh
 * @since 2020/3/6
 */
@Setter
@ToString
@Table("t_connect")
public class ZKConnect implements Comparable<ZKConnect>, ObjectComparator<ZKConnect>, Serializable {

    /**
     * 数据id
     */
    @Getter
    @Column
    @PrimaryKey
    private String id;

    /**
     * 连接地址
     */
    @Getter
    @Column
    private String host;

    /**
     * 名称
     */
    @Getter
    @Column
    private String name;

    /**
     * 备注信息
     */
    @Getter
    @Column
    private String remark;

    /**
     * 只读模式
     */
    @Getter
    @Column
    private Boolean readonly;

    /**
     * 分组id
     */
    @Getter
    @Column
    private String groupId;

    /**
     * 兼容模式
     * null: 无
     * 1: 兼容3.4.x版本
     */
    @Getter
    @Column
    private Integer compatibility;

    /**
     * 监听节点
     * false: 否
     * null|true: 是
     */
    @Column
    private Boolean listen;

    /**
     * 认证列表
     */
    @Getter
    private List<ZKAuth> auths;

    /**
     * 收藏的节点
     */
    @Getter
    private List<String> collects;

    /**
     * 过滤列表
     */
    @Getter
    private List<ZKFilter> filters;

    /**
     * 会话超时时间
     */
    @Column
    private Integer sessionTimeOut;

    /**
     * 连接超时时间
     */
    @Column
    private Integer connectTimeOut;

    /**
     * 是否开启ssh转发
     */
    @Getter
    @Column
    private Boolean sshForward;

    /**
     * ssh信息
     */
    @Getter
    private ZKSSHConnect sshConnect;

    /**
     * 复制对象
     *
     * @param zkInfo zk信息
     * @return 当前对象
     */
    public ZKConnect copy(@NonNull ZKConnect zkInfo) {
        this.name = zkInfo.name;
        this.host = zkInfo.host;
        this.remark = zkInfo.remark;
        this.listen = zkInfo.listen;
        this.groupId = zkInfo.groupId;
        this.readonly = zkInfo.readonly;
        this.collects = zkInfo.collects;
        this.sshForward = zkInfo.sshForward;
        this.sshConnect = zkInfo.sshConnect;
        this.compatibility = zkInfo.compatibility;
        this.connectTimeOut = zkInfo.connectTimeOut;
        this.sessionTimeOut = zkInfo.sessionTimeOut;
        return this;
    }

    /**
     * 是否ssh转发
     *
     * @return 结果
     */
    public boolean isSSHForward() {
        return BooleanUtil.isTrue(this.sshForward);
    }

    /**
     * 是否只读模式
     *
     * @return 结果
     */
    public boolean isReadonly() {
        return BooleanUtil.isTrue(this.readonly);
    }

    /**
     * 是否被收藏
     *
     * @param path 路径
     * @return 结果
     */
    public boolean isCollect(@NonNull String path) {
        return CollectionUtil.isNotEmpty(this.collects) && this.collects.contains(path);
    }

    /**
     * 添加收藏
     *
     * @param path 路径
     */
    public void addCollect(@NonNull String path) {
        if (this.collects == null) {
            this.collects = new ArrayList<>();
        }
        if (!this.collects.contains(path)) {
            this.collects.add(path);
        }
    }

    /**
     * 取消收藏
     *
     * @param path 路径
     * @return 结果
     */
    public boolean removeCollect(@NonNull String path) {
        if (this.collects != null) {
            return this.collects.remove(path);
        }
        return false;
    }

    /**
     * 获取会话超时
     *
     * @return 会话超时时间
     */
    public Integer getSessionTimeOut() {
        return this.sessionTimeOut == null || this.sessionTimeOut < 1 ? 30 : this.sessionTimeOut;
    }

    /**
     * 获取会话超时毫秒值
     *
     * @return 会话超时时间毫秒值
     */
    public int sessionTimeOutMs() {
        return this.getSessionTimeOut() * 60 * 1000;
    }

    /**
     * 获取连接超时
     *
     * @return 连接超时时间
     */
    public Integer getConnectTimeOut() {
        return this.connectTimeOut == null || this.connectTimeOut < 1 ? 5 : this.connectTimeOut;
    }

    /**
     * 是否监听节点
     *
     * @return 结果
     */
    public Boolean getListen() {
        return this.listen == null || this.listen;
    }

    /**
     * 获取连接超时毫秒值
     *
     * @return 连接超时时间毫秒值
     */
    public int connectTimeOutMs() {
        return this.getConnectTimeOut() * 1000;
    }

    @Override
    public int compareTo(ZKConnect o) {
        if (o == null) {
            return 1;
        }
        return this.name.compareToIgnoreCase(o.getName());
    }

    /**
     * 是否兼容3.4.x版本
     *
     * @return 结果
     */
    public boolean compatibility34() {
        return Objects.equals(1, this.compatibility);
    }

    /**
     * 获取连接ip
     *
     * @return 连接ip
     */
    public String hostIp() {
        if (StringUtil.isNotBlank(this.host) && this.host.contains(":")) {
            return this.host.split(":")[0];
        }
        return "";
    }

    /**
     * 获取连接端口
     *
     * @return 连接端口
     */
    public int hostPort() {
        try {
            if (StringUtil.isNotBlank(this.host) && !this.host.contains(",") && this.host.contains(":")) {
                return Integer.parseInt(this.host.split(":")[1]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean compare(ZKConnect t1) {
        if (t1 == null) {
            return false;
        }
        return StringUtil.equals(this.name, t1.name);
    }
}
