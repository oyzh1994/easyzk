package cn.oyzh.easyzk.query;

import cn.oyzh.i18n.I18nHelper;
import lombok.Data;
import org.apache.zookeeper.data.Stat;

/**
 * zk查询结果
 *
 * @author oyzh
 * @since 2025/01/20
 */
@Data
public class ZKQueryResult {

    private long cost;

    private Stat stat;

    private Object result;

    private String message;

    private boolean success;

    public String costSeconds() {
        return String.format("%.2f"+ I18nHelper.seconds(), this.cost / 1000.0);
    }
}
