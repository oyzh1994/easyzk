package cn.oyzh.easyzk.dto;

import lombok.Data;
import org.apache.zookeeper.Quotas;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

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

    private byte[] data;

    private String message;

    private boolean success;

    private List<ACL> aCLList;

    private List<String> nodes;

}
