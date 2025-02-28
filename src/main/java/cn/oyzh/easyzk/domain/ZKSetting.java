package cn.oyzh.easyzk.domain;

import cn.oyzh.fx.plus.domain.AppSetting;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * zk设置
 *
 * @author oyzh
 * @since 2022/8/26
 */
@Data
@Table("t_setting")
@EqualsAndHashCode(callSuper = true)
public class ZKSetting extends AppSetting {

    /**
     * 节点加载
     * 0|null 加载一级节点
     * 1 加载所有节点
     * 2 仅加载根节点
     */
    @Column
    private Byte loadMode;

    /**
     * 节点视图
     * 0|null 节点名称
     * 1 节点路径
     */
    @Column
    private Byte viewport;

    /**
     * 节点认证
     * 0|null 自动认证
     * 1 不自动认证
     */
    @Column
    private Byte authMode;

    /**
     * 节点加载限制
     * 0 无限制
     */
    @Column
    private Integer nodeLoadLimit;

    /**
     * 是否自动认证
     *
     * @return 结果
     */
    public boolean isAutoAuth() {
        return this.authMode == null || this.authMode == 0;
    }

    /**
     * 是否加载所有节点
     *
     * @return 结果
     */
    public boolean isLoadAll() {
        return this.loadMode != null && this.loadMode == 1;
    }

    /**
     * 是否显示节点路径
     *
     * @return 结果
     */
    public boolean isShowNodePath() {
        return this.viewport == null || this.viewport == 1;
    }

    /**
     * 是否加载一级节点
     *
     * @return 结果
     */
    public boolean isLoadFirst() {
        return this.loadMode == null || this.loadMode == 0;
    }

    /**
     * 是否仅加载根节点
     *
     * @return 结果
     */
    public boolean isLoadRoot() {
        return this.loadMode != null && this.loadMode == 2;
    }

    /**
     * 获取节点加载限制
     *
     * @return 节点加载限制
     */
    public int nodeLoadLimit() {
        return this.nodeLoadLimit == null ? 0 : this.nodeLoadLimit;
    }

    @Override
    public void copy(Object o) {
        super.copy(o);
        if (o instanceof ZKSetting setting) {
            this.loadMode = setting.loadMode;
            this.viewport = setting.viewport;
            this.authMode = setting.authMode;
            this.nodeLoadLimit = setting.nodeLoadLimit;
        }
    }
}
