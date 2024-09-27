package cn.oyzh.easyzk.domain;

import cn.oyzh.fx.common.jdbc.Column;
import cn.oyzh.fx.common.jdbc.Table;
import cn.oyzh.fx.common.util.ObjectComparator;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * zk数据历史
 *
 * @author oyzh
 * @since 2024/4/23
 */
@Getter
@Table("t_data_history")
public class ZKDataHistory implements ObjectComparator<ZKDataHistory>, Serializable {

    /**
     * 内容
     */
    @Column
    private byte[] data;

    @Column
    @Setter
    private long dataLength;

    // /**
    //  * 数据大小
    //  */
    // private String dataSize;

    /**
     * 保存时间
     */
    @Setter
    @Column
    private long saveTime = System.currentTimeMillis();

    /**
     * 路径
     */
    @Setter
    @Column
    private String path;

    /**
     * 连接信息id
     */
    @Setter
    @Column
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
        // this.dataSize = history.dataSize;
        this.saveTime = history.saveTime;
        this.dataLength = history.dataLength;
        return this;
    }

    public String getDataSize() {
        long length = this.dataLength;
        if (length < 1024) {
            return length + "b";
        }
        if (length < 1024 * 1024) {
            return this.dataLength / 1024 + "Kb";
        }
        if (length < 1024 * 1024 * 1024) {
            return this.dataLength / 1024 / 1024 + "Mb";
        }
        return this.dataLength / 1024 / 1024 / 1024 + "Gb";
    }

    public void setData(byte[] data) {
        this.data = data;
        if (data != null) {
            this.dataLength = data.length;
        } else {
            this.dataLength = 0;
        }
    }

    // public String getSaveTimeExt() {
    //     return Const.DATE_FORMAT.format(this.saveTime);
    // }
}
