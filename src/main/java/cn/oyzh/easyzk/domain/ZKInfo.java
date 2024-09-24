package cn.oyzh.easyzk.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.ssh.SSHConnectInfo;
import cn.oyzh.fx.common.util.ObjectComparator;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * zk信息
 *
 * @author oyzh
 * @since 2020/3/6
 */
@ToString
public class ZKInfo implements Comparable<ZKInfo>, ObjectComparator<ZKInfo>, Serializable {

    /**
     * 数据id
     */
    @Getter
    @Setter
    private String id;

    /**
     * 连接地址
     */
    @Getter
    @Setter
    private String host;

    /**
     * 名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 备注信息
     */
    @Getter
    @Setter
    private String remark;

    /**
     * 只读模式
     */
    @Setter
    @Getter
    private Boolean readonly;

    /**
     * 分组id
     */
    @Getter
    @Setter
    private String groupId;

    /**
     * 兼容模式
     * null: 无
     * 1: 兼容3.4.x版本
     */
    @Getter
    @Setter
    private Integer compatibility;

    /**
     * 监听节点
     * false: 否
     * null|true: 是
     */
    @Setter
    private Boolean listen;

    // /**
    //  * 集群模式
    //  * 0|null: 否
    //  * 1: 是
    //  */
    // @Setter
    // @Getter
    // private Boolean cluster;

    /**
     * 收藏的节点
     */
    @Getter
    @Setter
    private List<String> collects;

    /**
     * 会话超时时间
     */
    @Setter
    private Integer sessionTimeOut;

    /**
     * 连接超时时间
     */
    @Setter
    private Integer connectTimeOut;

    /**
     * 是否开启ssh转发
     */
    @Setter
    @Getter
    private Boolean sshForward;

    /**
     * ssh信息
     */
    @Setter
    @Getter
    private SSHConnectInfo sshInfo;

    /**
     * 复制对象
     *
     * @param zkInfo zk信息
     * @return 当前对象
     */
    public ZKInfo copy(@NonNull ZKInfo zkInfo) {
        this.name = zkInfo.name;
        this.host = zkInfo.host;
        this.remark = zkInfo.remark;
        this.listen = zkInfo.listen;
        // this.cluster = zkInfo.cluster;
        this.groupId = zkInfo.groupId;
        this.sshInfo = zkInfo.sshInfo;
        this.readonly = zkInfo.readonly;
        this.collects = zkInfo.collects;
        this.sshForward = zkInfo.sshForward;
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
        return CollUtil.isNotEmpty(this.collects) && this.collects.contains(path);
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

    // /**
    //  * 是否集群模式
    //  *
    //  * @return 结果
    //  */
    // public boolean isCluster() {
    //     return BooleanUtil.isTrue(this.cluster) || StrUtil.count(this.host, ":") > 1;
    // }

    /**
     * 获取连接超时毫秒值
     *
     * @return 连接超时时间毫秒值
     */
    public int connectTimeOutMs() {
        return this.getConnectTimeOut() * 1000;
    }

    @Override
    public int compareTo(ZKInfo o) {
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
        if (StrUtil.isNotBlank(this.host) && this.host.contains(":")) {
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
            if (StrUtil.isNotBlank(this.host) && !this.host.contains(",") && this.host.contains(":")) {
                return Integer.parseInt(this.host.split(":")[1]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean compare(ZKInfo t1) {
        if (t1 == null) {
            return false;
        }
        return StrUtil.equals(this.name, t1.name);
    }
}
