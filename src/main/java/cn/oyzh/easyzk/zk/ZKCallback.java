package cn.oyzh.easyzk.zk;// package cn.oyzh.easyzk.zk;
//
// import lombok.Getter;
// import lombok.NonNull;
// import lombok.Setter;
// import lombok.experimental.Accessors;
// import org.apache.curator.framework.CuratorFramework;
// import org.apache.curator.framework.api.BackgroundCallback;
// import org.apache.curator.framework.api.CuratorEvent;
// import org.apache.curator.framework.api.CuratorEventType;
//
// /**
//  * zk回调处理
//  *
//  * @author oyzh
//  * @since 2023/4/27
//  */
// @Accessors(fluent = true, chain = true)
// public class ZKCallback implements BackgroundCallback {
//
//     /**
//      * 权限查询
//      */
//     @Setter
//     @Getter
//     private Boolean acl;
//
//     /**
//      * 状态查询
//      */
//     @Setter
//     @Getter
//     private Boolean stat;
//
//     /**
//      * 数据查询
//      */
//     @Setter
//     @Getter
//     private Boolean data;
//
//     /**
//      * zk节点
//      */
//     private final ZKNode node;
//
//     /**
//      * 结束处理
//      */
//     private final Runnable onFinish;
//
//     public ZKCallback(@NonNull ZKNode node, @NonNull Runnable onFinish) {
//         this.node = node;
//         this.onFinish = onFinish;
//     }
//
//     @Override
//     public void processResult(CuratorFramework client, CuratorEvent event) {
//         if (this.acl != null && event.getType() == CuratorEventType.GET_ACL) {
//             this.acl = null;
//             this.node.acl(event.getACLList());
//         }
//         if (this.stat != null && event.getType() == CuratorEventType.EXISTS) {
//             this.stat = null;
//             this.node.stat(event.getStat());
//         }
//         if (this.data != null && event.getType() == CuratorEventType.GET_DATA) {
//             this.data = null;
//             this.node.nodeData(event.getData());
//         }
//         if ((this.stat == null || !this.stat) && (this.acl == null || !this.acl) && (this.data == null || !this.data)) {
//             try {
//                 this.onFinish.run();
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//             }
//         }
//     }
// }
