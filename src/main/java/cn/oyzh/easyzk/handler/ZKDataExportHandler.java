package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.file.FileColumns;
import cn.oyzh.easyzk.file.FileRecord;
import cn.oyzh.easyzk.file.ZKDataExportConfig;
import cn.oyzh.easyzk.file.ZKFileHelper;
import cn.oyzh.easyzk.file.ZKTypeFileWriter;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

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
    private String fileType;

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
     * 导出配置
     */
    private ZKDataExportConfig config = new ZKDataExportConfig();

    /**
     * 执行导出
     *
     * @throws Exception 异常
     */
    public void doExport() throws Exception {
        this.message("Export Starting");
        FileColumns columns = new FileColumns();
        columns.addColumn("path");
        columns.addColumn("data");
        // 获取写入器
        ZKTypeFileWriter writer = ZKFileHelper.initWriter(this.fileType, this.config, this.exportFile.getPath(), columns);
        if (writer != null) {
            try {
                // 写入头
                writer.writeHeader();
                // 节点过滤
                Predicate<String> filter = path -> {
                    if (ZKNodeUtil.isFiltered(path, this.filters)) {
                        this.message("Node:" + path + " Filtered");
                        this.processedSkip();
                        return false;
                    }
                    return true;
                };
                // 获取节点成功
                BiConsumer<String, ZKNode> success = (path, node) -> {
                    try {
                        this.checkInterrupt();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        FileRecord record = new FileRecord();
                        record.put(0, node.nodePath());
                        record.put(1, node.nodeDataStr(StandardCharsets.UTF_8));
                        writer.writeRecord(record);
                        this.message("Export Node:" + path + " Success");
                        this.processedIncr();
                    } catch (Exception ex) {
                        this.message("Write Node:" + path + " Failed");
                        this.processedDecr();
                    }
                };
                // 获取节点失败
                BiConsumer<String, Exception> error = (path, ex) -> {
                    if (ex instanceof RuntimeException) {
                        ex = (Exception) ex.getCause();
                    }
                    // 针对中断异常不处理
                    if (ex instanceof InterruptedException) {
                        return;
                    }
                    this.message("Export Node:" + path + " Failed");
                    this.processedDecr();
                    ex.printStackTrace();
                };
                // 递归获取节点
                ZKNodeUtil.loopNode(this.client, this.nodePath, filter, success, error);
            } finally {
                // 写入尾
                writer.writeTrial();
                writer.close();
                this.message("Exported To -> " + this.exportFile.getPath());
            }
        }
        this.message("Export Finished");
    }

    public void txtIdentifier(String txtIdentifier) {
        this.config.txtIdentifier(txtIdentifier);
    }
}

