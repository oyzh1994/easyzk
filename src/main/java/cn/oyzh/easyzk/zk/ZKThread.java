package cn.oyzh.easyzk.zk;

import cn.oyzh.fx.plus.thread.BackgroundService;
import lombok.NonNull;

/**
 * zk任务线程
 *
 * @author oyzh
 * @since 2023/4/27
 */
public class ZKThread extends Thread {

    /**
     * 执行业务
     */
    private final Runnable task;

    public ZKThread(@NonNull Runnable task) {
        this.task = task;
    }

    @Override
    public void run() {
        BackgroundService.submit(task);
    }
}
