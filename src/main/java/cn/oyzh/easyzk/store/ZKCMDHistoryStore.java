// package cn.oyzh.easyzk.store;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.hutool.core.io.FileUtil;
// import cn.hutool.core.util.StrUtil;
// import cn.oyzh.easyzk.ZKConst;
// import cn.oyzh.easyzk.dto.ZKCMDHistory;
// import cn.oyzh.fx.common.util.FileStore;
// import com.alibaba.fastjson.JSON;
// import lombok.NonNull;
// import lombok.extern.slf4j.Slf4j;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * zk命令行历史存储
//  *
//  * @author oyzh
//  * @since 2023/5/29
//  */
// @Slf4j
// public class ZKCMDHistoryStore extends FileStore<ZKCMDHistory> {
//
//     /**
//      * 当前实例
//      */
//     public static final ZKCMDHistoryStore INSTANCE = new ZKCMDHistoryStore();
//
//     /**
//      * 数据列表
//      */
//     private final List<ZKCMDHistory> histories;
//
//     {
//         this.filePath(ZKConst.STORE_PATH + "zk_cmd_history.json");
//         log.info("ZKCMDHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
//         this.histories = this.load();
//     }
//
//     @Override
//     public List<ZKCMDHistory> load() {
//         if (this.histories == null) {
//             String text = FileUtil.readString(this.storeFile(), this.charset());
//             if (StrUtil.isBlank(text)) {
//                 return new ArrayList<>();
//             }
//             return JSON.parseArray(text, ZKCMDHistory.class);
//         }
//         return this.histories;
//     }
//
//     @Override
//     public boolean add(@NonNull ZKCMDHistory history) {
//         try {
//             List<ZKCMDHistory> list = this.load();
//             ZKCMDHistory last = CollUtil.getLast(list);
//             if (last != null && last.getCmd().equals(history.getCmd())) {
//                 last.setSaveTime(System.currentTimeMillis());
//             } else {
//                 history.setSaveTime(System.currentTimeMillis());
//                 this.histories.add(history);
//             }
//             // 更新数据
//             return this.save(this.histories);
//         } catch (Exception e) {
//             log.warn("add error,err:{}", e.getMessage());
//         }
//         return false;
//     }
//
//     @Override
//     public boolean update(@NonNull ZKCMDHistory cmdHistory) {
//         return false;
//     }
//
//     @Override
//     public boolean delete(@NonNull ZKCMDHistory zkcmdHistory) {
//         return false;
//     }
//
//     /**
//      * 清除数据
//      */
//     public void clear() {
//         try {
//             List<ZKCMDHistory> list = this.load();
//             // 更新数据
//             if (this.histories.removeAll(list)) {
//                 this.save(this.histories);
//             }
//         } catch (Exception ex) {
//             ex.printStackTrace();
//         }
//     }
// }
