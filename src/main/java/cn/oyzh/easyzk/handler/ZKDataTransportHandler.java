package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.common.util.TextUtil;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/10/15
 */
@Setter
public class ZKDataTransportHandler extends DataHandler {

    /**
     * 来源客户端
     */
    @Accessors(fluent = true, chain = true)
    protected ZKClient sourceClient;

    /**
     * 目标客户端
     */
    @Accessors(fluent = true, chain = true)
    protected ZKClient targetClient;

    /**
     * 节点存在时处理策略
     * 0 跳过
     * 1 更新
     */
    @Accessors(fluent = true, chain = true)
    private String existsPolicy;

    /**
     * 过滤内容列表
     */
    @Accessors(fluent = true, chain = true)
    private List<ZKFilter> filters;

    /**
     * 来源字符集
     */
    @Accessors(fluent = true, chain = true)
    private Charset sourceCharset;

    /**
     * 目标字符集
     */
    @Accessors(fluent = true, chain = true)
    private Charset targetCharset;

    /**
     * 执行传输
     */
    public void doTransport() throws Exception {
        this.doTransport("/");
    }

    /**
     * 执行传输
     *
     * @param path 节点路径
     * @throws InterruptedException 异常
     */
    private void doTransport(String path) throws InterruptedException {
        String decodePath = ZKNodeUtil.decodePath(path);
        try {
            // 检查中断
            this.checkInterrupt();
            // 获取节点
            Stat stat = this.sourceClient.checkExists(path);
            byte[] bytes = this.sourceClient.getData(path);

            // 节点查询失败
            if (stat == null || bytes == null) {
                this.message("node[" + decodePath + "] does not exist");
                this.processedDecr();
                return;
            }

            // 临时节点跳过
            if (stat.getEphemeralOwner() > 0) {
                this.message("node[" + decodePath + "] is ephemeral, skip it");
                this.processedSkip();
                return;
            }

            // 过滤处理
            if (ZKNodeUtil.isFiltered(decodePath, this.filters)) {
                this.message("node[" + decodePath + "] is filtered, skip it");
                this.processedSkip();
                return;
            }

            // 节点存在
            if (this.targetClient.exists(path)) {
                // 跳过
                if (StringUtil.equals(this.existsPolicy, "0")) {
                    this.message("node[" + decodePath + "] is exist, skip it");
                    this.processedSkip();
                } else if (StringUtil.equals(this.existsPolicy, "1")) { // 更新
                    bytes = TextUtil.changeCharset(bytes, this.sourceCharset, this.targetCharset);
                    this.targetClient.setData(path, bytes);
                    this.message("node[" + decodePath + "] is exist, update it");
                    this.processedIncr();
                }
            } else {// 创建
                bytes = TextUtil.changeCharset(bytes, this.sourceCharset, this.targetCharset);
                this.targetClient.createIncludeParents(path, bytes, CreateMode.PERSISTENT);
                this.message("node[" + decodePath + "] not exist, create it");
                this.processedIncr();
            }
            // 获取子节点
            List<String> subs = this.sourceClient.getChildren(path);
            // 递归传输节点
            for (String sub : subs) {
                this.checkInterrupt();
                this.doTransport(ZKNodeUtil.concatPath(path, sub));
            }
        } catch (InterruptedException ex) {
            throw ex;
        } catch (Exception ex) {
            this.message("node[" + decodePath + "] transport fail, error[" + ex.getMessage() + "]");
            this.processedDecr();
        }
    }
}

