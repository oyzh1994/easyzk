package cn.oyzh.easyzk.domain;

import cn.oyzh.fx.common.jdbc.Column;
import cn.oyzh.fx.common.jdbc.Table;
import cn.oyzh.fx.plus.domain.Setting;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * zk设置
 *
 * @author oyzh
 * @since 2022/8/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_setting")
public class ZKSetting extends Setting {

    /**
     * 节点加载
     * 0|null 加载一级节点
     * 1 加载所有节点
     * 2 仅加载根节点
     */
    @Column
    private Byte loadMode;

    /**
     * 节点认证
     * 0|null 自动认证
     * 1 不自动认证
     */
    @Column
    private Byte authMode;

    // /**
    //  * 搜索-更多-展开状态
    //  * 0|null 不展开
    //  * 1 展开
    //  */
    // private Byte searchMoreExpand;

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
     * 是否仅加载根节点
     *
     * @return 结果
     */
    public boolean isLoadRoot() {
        return this.loadMode != null && this.loadMode == 2;
    }

    // /**
    //  * 是否展开搜索-更多
    //  *
    //  * @return 结果
    //  */
    // public boolean isSearchMoreExpand() {
    //     return this.searchMoreExpand != null && this.searchMoreExpand == 1;
    // }

    @Override
    public void copy(Object t1) {
        super.copy(t1);
        if (t1 instanceof ZKSetting t2) {
            this.loadMode = t2.loadMode;
            this.authMode = t2.authMode;
        }
    }
}
