package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/11/26
 */
@Setter
public class ZKDataExportHandler extends DataHandler {

    /**
     * 文件格式
     */
    @Accessors(fluent = true, chain = true)
    private String format;

    /**
     * 目标字符集
     */
    @Accessors(fluent = true, chain = true)
    private Charset charset;

    /**
     * 客户端
     */
    @Accessors(fluent = true, chain = true)
    private ZKClient client;

    /**
     * 节点路径
     */
    @Accessors(fluent = true, chain = true)
    private String nodePath;

    /**
     * 文件路径
     */
    @Accessors(fluent = true, chain = true)
    private File exportFile;

    /**
     * 过滤内容列表
     */
    @Accessors(fluent = true, chain = true)
    private List<ZKFilter> filters;

    /**
     * 执行传输
     */
    public void doExport() throws Exception {
        this.doExport("/");
    }

    /**
     * 执行传输
     *
     * @param path 节点路径
     * @throws InterruptedException 异常
     */
    private void doExport(String path) throws InterruptedException {
        String decodePath = ZKNodeUtil.decodePath(path);
        try {
            // 检查中断
            this.checkInterrupt();

        } catch (InterruptedException ex) {
            throw ex;
        } catch (Exception ex) {
            this.message("node[" + decodePath + "] transport fail, error[" + ex.getMessage() + "]");
            this.processedDecr();
        }
    }
}

