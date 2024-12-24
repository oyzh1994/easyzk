package cn.oyzh.easyzk.vo;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.dto.ZKEnvNode;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

/**
 * redis信息属性项目
 *
 * @author oyzh
 * @since 2023/08/01
 */
public class ZKServerInfo {

    /**
     * 服务id
     */
    private SimpleStringProperty zxidProperty;

    /**
     * 服务类型
     */
    private SimpleStringProperty modeProperty;

    /**
     * 服务版本
     */
    private SimpleStringProperty versionProperty;

    /**
     * 延迟信息，单位毫秒
     * 最小/平均/最大
     */
    private SimpleStringProperty latencyInfoProperty;

    /**
     * 节点数量
     */
    private SimpleStringProperty nodeCountProperty;

    /**
     * 已连接客户端
     */
    private SimpleStringProperty connectionsProperty;

    /**
     * 命令信息
     * 已接收/已发送/等待中
     */
    private SimpleStringProperty commandInfoProperty;

    public void update(List<ZKEnvNode> envNodes) {
        String sent = null;
        String received = null;
        String outstanding = null;
        for (ZKEnvNode envNode : envNodes) {
            if (StringUtil.equalsIgnoreCase(envNode.getName(), "Zxid")) {
                this.setZxid(envNode.getValue());
            } else if (StringUtil.equalsIgnoreCase(envNode.getName(), "Mode")) {
                this.setMode(envNode.getValue());
            } else if (StringUtil.containsIgnoreCase(envNode.getName(), "version")) {
                this.setVersion(envNode.getValue());
            } else if (StringUtil.equalsIgnoreCase(envNode.getName(), "Connections")) {
                this.setConnections(envNode.getValue());
            } else if (StringUtil.equalsIgnoreCase(envNode.getName(), "Node count")) {
                this.setNodeCount(envNode.getValue());
            } else if (StringUtil.containsIgnoreCase(envNode.getName(), "Latency")) {
                this.setLatencyInfo(envNode.getValue());
            } else if (StringUtil.equalsIgnoreCase(envNode.getName(), "Received")) {
                received = envNode.getValue();
            } else if (StringUtil.equalsIgnoreCase(envNode.getName(), "Sent")) {
                sent = envNode.getValue();
            } else if (StringUtil.equalsIgnoreCase(envNode.getName(), "Outstanding")) {
                outstanding = envNode.getValue();
            }
        }

        StringBuilder commandInfo = new StringBuilder();
        if (received != null) {
            commandInfo.append(received.trim()).append("/");
        } else {
            commandInfo.append("N/");
        }
        if (sent != null) {
            commandInfo.append(sent.trim()).append("/");
        } else {
            commandInfo.append("N/");
        }
        if (outstanding != null) {
            commandInfo.append(outstanding.trim());
        } else {
            commandInfo.append("N");
        }
        this.setCommandInfo(commandInfo.toString());
    }

    public SimpleStringProperty commandInfoProperty() {
        if (this.commandInfoProperty == null) {
            this.commandInfoProperty = new SimpleStringProperty();
        }
        return commandInfoProperty;
    }

    public void setCommandInfo(String commandInfo) {
        this.commandInfoProperty().set(commandInfo);
    }

    public String getCommandInfo() {
        return commandInfoProperty == null ? "N/A" : commandInfoProperty.getValue();
    }

    public SimpleStringProperty connectionsProperty() {
        if (this.connectionsProperty == null) {
            this.connectionsProperty = new SimpleStringProperty();
        }
        return connectionsProperty;
    }

    public void setConnections(String connections) {
        this.connectionsProperty().set(connections);
    }

    public String getConnections() {
        return connectionsProperty == null ? "N/A" : connectionsProperty.getValue();
    }

    public SimpleStringProperty nodeCountProperty() {
        if (this.nodeCountProperty == null) {
            this.nodeCountProperty = new SimpleStringProperty();
        }
        return nodeCountProperty;
    }

    public void setNodeCount(String nodeCount) {
        this.nodeCountProperty().set(nodeCount);
    }

    public String getNodeCount() {
        return nodeCountProperty == null ? "N/A" : nodeCountProperty.getValue();
    }

    public SimpleStringProperty latencyInfoProperty() {
        if (this.latencyInfoProperty == null) {
            this.latencyInfoProperty = new SimpleStringProperty();
        }
        return latencyInfoProperty;
    }

    public void setLatencyInfo(String latencyInfo) {
        this.latencyInfoProperty().set(latencyInfo);
    }

    public String getLatencyInfo() {
        return latencyInfoProperty == null ? "N/A" : latencyInfoProperty.getValue();
    }

    public SimpleStringProperty zxidProperty() {
        if (this.zxidProperty == null) {
            this.zxidProperty = new SimpleStringProperty();
        }
        return zxidProperty;
    }

    public void setZxid(String zxid) {
        this.zxidProperty().set(zxid);
    }

    public String getZxid() {
        return zxidProperty == null ? "N/A" : zxidProperty.getValue();
    }

    public SimpleStringProperty modeProperty() {
        if (this.modeProperty == null) {
            this.modeProperty = new SimpleStringProperty();
        }
        return modeProperty;
    }

    public void setMode(String zxid) {
        this.modeProperty().set(zxid);
    }

    public String getMode() {
        return modeProperty == null ? "N/A" : modeProperty.getValue();
    }

    public SimpleStringProperty versionProperty() {
        if (this.versionProperty == null) {
            this.versionProperty = new SimpleStringProperty();
        }
        return versionProperty;
    }

    public void setVersion(String version) {
        if (version != null) {
            version = version.split("-")[0];
            this.versionProperty().set(version);
        }
    }

    public String getVersion() {
        return versionProperty == null ? "N/A" : versionProperty.getValue();
    }

}
