package cn.oyzh.easyzk.dto;

import cn.oyzh.fx.plus.i18n.I18nManager;
import lombok.Data;
import lombok.NonNull;
import org.apache.zookeeper.server.quorum.QuorumPeer;

import java.util.Locale;

/**
 * zk服务节点
 *
 * @author oyzh
 * @since 2023/1/6
 */
@Data
public class ZKServerNode {

    /**
     * id
     */
    private Long id;

    /**
     * 类型
     */
    private String type;

    /**
     * 交互地址
     */
    private String addr;

    /**
     * 权重
     */
    private Long weight;

    /**
     * 客户端连接地址
     */
    private String clientAddr;

    /**
     * 服务端选举地址
     */
    private String electionAddr;

    public ZKServerNode(@NonNull QuorumPeer.QuorumServer server) {
        this.id = server.id;
        if (I18nManager.currentLocale() == Locale.SIMPLIFIED_CHINESE) {
            this.type = server.type == QuorumPeer.LearnerType.PARTICIPANT ? "选举节点" : "观察节点";
        } else if (I18nManager.currentLocale() == Locale.TRADITIONAL_CHINESE) {
            this.type = server.type == QuorumPeer.LearnerType.PARTICIPANT ? "選舉節點" : "觀察節點";
        } else {
            this.type = server.type == QuorumPeer.LearnerType.PARTICIPANT ? "Participant" : "Observer";
        }
        this.addr = server.addr.toString();
        this.clientAddr = server.clientAddr.toString();
        this.electionAddr = server.electionAddr.toString();
    }

    public ZKServerNode(@NonNull String serverTxt) {
        String serverName = serverTxt.split(":")[0];
        serverName = serverName.substring(serverName.indexOf("=") + 1);
        this.weight = 1L;
        this.addr = serverName + ":" + serverTxt.split(":")[1];
        this.electionAddr = serverName + ":" + serverTxt.split(":")[2];
        this.id = Long.parseLong(serverTxt.substring(7, serverTxt.indexOf("=")));
        this.clientAddr = serverTxt.substring(serverTxt.indexOf(";") + 1);
        if (I18nManager.currentLocale() == Locale.SIMPLIFIED_CHINESE) {
            this.type = serverTxt.toLowerCase().contains("participant") ? "选举节点" : "观察节点";
        } else if (I18nManager.currentLocale() == Locale.TRADITIONAL_CHINESE) {
            this.type = serverTxt.toLowerCase().contains("participant") ? "選舉節點" : "觀察節點";
        } else {
            this.type = serverTxt.toLowerCase().contains("participant") ? "Participant" : "Observer";
        }
    }
}
