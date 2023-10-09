package cn.oyzh.easyzk.domain;

import lombok.Data;

/**
 * zk设置
 *
 * @author oyzh
 * @since 2022/8/26
 */
@Data
public class ZKSetting {

    /**
     * 应用退出
     * 0 到系统托盘
     * 1 每次询问
     * 2 直接关闭程序
     */
    private Integer exitMode;

    /**
     * 记住页面大小
     * 0|null 不记住
     * 1 记住
     */
    private Integer pageInfo;

    // /**
    //  * 布局模式
    //  * 0|null 显示全部模块
    //  * 1 隐藏消息模块
    //  */
    // private Integer layout;

    /**
     * 记住页面拉伸
     * 0 不记住
     * 1|null 记住
     */
    private Integer rememberPageResize;

    /**
     * 记住页面位置
     * 0|null 不记住
     * 1 记住
     */
    private Integer rememberPageLocation;

    /**
     * 节点加载
     * 0|null 加载一级节点
     * 1 加载所有节点
     * 2 仅加载根节点
     */
    private Integer loadMode;

    /**
     * 节点认证
     * 0|null 自动认证
     * 1 不自动认证
     */
    private Integer authMode;

//    /**
//     * 分组展开
//     * 0 无需展开
//     * 1|null 默认展开
//     */
//    private Integer groupExpand;

    /**
     * 是否自动认证
     *
     * @return 结果
     */
    public boolean isAutoAuth() {
        return this.authMode == null || this.authMode == 0;
    }

    /**
     * 是否退出到系统托盘
     *
     * @return 结果
     */
    public boolean isExitTray() {
        return this.exitMode == null || this.exitMode == 0;
    }

    /**
     * 退出是否询问
     *
     * @return 结果
     */
    public boolean isExitAsk() {
        return this.exitMode != null && this.exitMode == 1;
    }

    /**
     * 是否直接退出
     *
     * @return 结果
     */
    public boolean isExitDirectly() {
        return this.exitMode != null && this.exitMode == 2;
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
    //  * 是否显示消息模块
    //  *
    //  * @return 结果
    //  */
    // public boolean isShowMsgPane() {
    //     return this.layout == null || this.layout == 0;
    // }

    /**
     * 是否记住页面大小
     *
     * @return 结果
     */
    public boolean isRememberPageSize() {
        return this.pageInfo != null && this.pageInfo == 1;
    }

    /**
     * 是否记住页面拉伸
     *
     * @return 结果
     */
    public boolean isRememberPageResize() {
        return this.rememberPageResize == null || this.rememberPageResize == 1;
    }

    /**
     * 是否记住页面位置
     *
     * @return 结果
     */
    public boolean isRememberPageLocation() {
        return this.rememberPageLocation != null && this.rememberPageLocation == 1;
    }

//    /**
//     * 是否展开分组
//     *
//     * @return 结果
//     */
//    public boolean isGroupExpand() {
//        return this.groupExpand == null || this.groupExpand == 1;
//    }
}
