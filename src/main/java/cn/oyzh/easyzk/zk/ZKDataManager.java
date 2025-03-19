//package cn.oyzh.easyzk.zk;
//
//import cn.oyzh.common.thread.ThreadUtil;
//import cn.oyzh.common.util.StringUtil;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.UtilityClass;
//
//import java.util.Iterator;
//import java.util.Queue;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.atomic.AtomicBoolean;
//
///**
// * @author oyzh
// * @since 2025-01-23
// */
//
//public class ZKDataManager {
//
//    private static final Queue<ZKDataTask> QUEUE = new ConcurrentLinkedQueue<>();
//
//    private static final AtomicBoolean WORKING = new AtomicBoolean(false);
//
//    public static void addTask(String iid, String path, Runnable runnable) {
//        QUEUE.add(new ZKDataTask(iid, path, runnable));
//        doWorker();
//    }
//
//    public static void removeTask(String iid, String path) {
////        QUEUE.remove(new ZKDataTask(iid, path));
//        QUEUE.removeIf(task -> StringUtil.equals(task.getIid(), iid) && StringUtil.equals(task.getPath(), path));
//    }
//
//    public static void removeTask(String iid) {
//        QUEUE.removeIf(task -> StringUtil.equals(task.getIid(), iid));
//    }
//
//    private static void doWorker() {
//        if (!WORKING.get() && !QUEUE.isEmpty()) {
//            WORKING.set(true);
//            ThreadUtil.start(() -> {
//                try {
//                    do {
//                        ZKDataTask task = QUEUE.poll();
//                        if (task != null) {
//                            try {
//                                System.out.println(task.iid + ":" + task.getPath());
//                                task.runnable.run();
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//                    } while (!QUEUE.isEmpty());
//                } finally {
//                    WORKING.set(false);
//                }
//            });
//        }
//    }
//
//    @Data
//    public static class ZKDataTask {
//
//        private String iid;
//
//        private String path;
//
//        private Runnable runnable;
//
//        public ZKDataTask(String iid, String path, Runnable runnable) {
//            this.iid = iid;
//            this.path = path;
//            this.runnable = runnable;
//        }
//
//        public ZKDataTask(String iid, String path) {
//            this.iid = iid;
//            this.path = path;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (o instanceof ZKDataTask task) {
//                return StringUtil.equals(this.iid, task.iid) && StringUtil.equals(this.path, task.path);
//            }
//            return false;
//        }
//    }
//}
