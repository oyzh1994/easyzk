package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 连接管理
 *
 * @author oyzh
 * @since 2023/5/12
 */
public interface ZKConnectManager {

    /**
     * 添加连接
     *
     * @param zkInfo 连接信息
     */
    void addConnect(@NonNull ZKConnect zkInfo);

    // /**
    //  * 删除多个连接
    //  *
    //  * @param zkConnects 连接列表
    //  */
    // default void addConnects(List<ZKConnect> zkConnects) {
    //     if (CollectionUtil.isNotEmpty(zkConnects)) {
    //         for (ZKConnect zkConnect : zkConnects) {
    //             this.addConnect(zkConnect);
    //         }
    //     }
    // }

    /**
     * 添加连接节点
     *
     * @param item 连接节点
     */
    void addConnectItem(@NonNull ZKConnectTreeItem item);

    /**
     * 添加多个连接节点
     *
     * @param items 连接节点列表
     */
    void addConnectItems(@NonNull List<ZKConnectTreeItem> items);

    /**
     * 删除连接节点
     *
     * @param item 连接节点
     * @return 结果
     */
    boolean delConnectItem(@NonNull ZKConnectTreeItem item);

    /**
     * 获取连接节点
     *
     * @return 连接节点
     */
    List<ZKConnectTreeItem> getConnectItems();

    /**
     * 获取已连接的连接节点
     *
     * @return 已连接的连接节点
     */
    default List<ZKConnectTreeItem> getConnectedItems() {
        return this.getConnectItems().parallelStream().filter(ZKConnectTreeItem::isConnected).collect(Collectors.toList());
    }
}
