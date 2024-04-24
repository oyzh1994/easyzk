package cn.oyzh.easyzk.domain;

import cn.oyzh.fx.common.Const;
import cn.oyzh.fx.common.util.ObjectComparator;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;

/**
 * zk数据历史
 *
 * @author oyzh
 * @since 2024/4/23
 */
public class ZKDataHistory implements ObjectComparator<ZKDataHistory> {

    /**
     * 内容
     */
    @Setter
    @Getter
    private byte[] data;

    /**
     * 数据大小
     */
    @Getter
    private String dataSize;

    /**
     * 保存时间
     */
    @Getter
    @Setter
    private long saveTime = System.currentTimeMillis();

    /**
     * 路径
     */
    @Setter
    @Getter
    private String path;

    /**
     * 连接信息id
     */
    @Setter
    @Getter
    private String infoId;

    @Override
    public boolean compare(ZKDataHistory t1) {
        if (t1 == null) {
            return false;
        }
        if (Objects.equals(this, t1)) {
            return true;
        }
        if (!Objects.equals(this.path, t1.path)) {
            return false;
        }
        return Objects.equals(this.infoId, t1.infoId);
    }

    /**
     * 复制对象
     *
     * @param history 过滤信息
     * @return 当前对象
     */
    public ZKDataHistory copy(@NonNull ZKDataHistory history) {
        this.data = history.data;
        this.path = history.path;
        this.infoId = history.infoId;
        this.dataSize = history.dataSize;
        this.saveTime = history.saveTime;
        return this;
    }

    public void dataSize(long length) {
        if (length < 1024) {
            this.dataSize = length + "b";
        } else if (length < 1024 * 1024) {
            this.dataSize = length / 1024 + "Kb";
        } else if (length < 1024 * 1024 * 1024) {
            this.dataSize = length / 1024 / 1024 + "Mb";
        }
    }


    public String getSaveTimeExt() {
        return Const.DATE_FORMAT.format(this.saveTime);
    }
}
