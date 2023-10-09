package cn.oyzh.easyzk.dto;

import lombok.Data;

import java.util.Objects;

/**
 * zk命令行历史
 *
 * @author oyzh
 * @since 2023/5/29
 */
@Data
public class ZKCMDHistory {

    /**
     * 命令
     */
    private String cmd;

    /**
     * 保存时间
     */
    private long saveTime;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ZKCMDHistory history) {
            if (!Objects.equals(this.cmd, history.getCmd())) {
                return false;
            }
            return Objects.equals(this.saveTime, history.getSaveTime());
        }
        return false;
    }

}
