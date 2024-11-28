package cn.oyzh.easyzk.handler;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.common.util.TextUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.store.file.FileColumns;
import cn.oyzh.store.file.FileHelper;
import cn.oyzh.store.file.FileReadConfig;
import cn.oyzh.store.file.FileRecord;
import cn.oyzh.store.file.TypeFileReader;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/11/26
 */
@Setter
@Accessors(fluent = true, chain = false)
public class ZKDataImportHandler extends DataHandler {

    /**
     * 文件格式
     */
    private String fileType;

    /**
     * 客户端
     */
    private ZKClient client;

    /**
     * 批量处理大小
     */
    private int batchSize = 50;

    /**
     * 存在时忽略
     */
    private boolean ignoreExist;

    /**
     * 导出配置
     */
    private FileReadConfig config = new FileReadConfig();

    /**
     * 执行导出
     *
     * @throws Exception 异常
     */
    public void doImport() throws Exception {
        this.message("Import Starting");
        FileColumns columns = new FileColumns();
        columns.addColumn("path", 0);
        columns.addColumn("data", 1);
        // 获取写入器
        TypeFileReader reader = FileHelper.initReader(this.fileType, this.config, columns);
        if (reader != null) {
            try {
                while (true) {
                    this.checkInterrupt();
                    List<FileRecord> records = reader.readRecords(this.batchSize);
                    if (CollectionUtil.isEmpty(records)) {
                        break;
                    }
                    for (FileRecord record : records) {
                        String path = "";
                        try {
                            path = (String) record.get(0);
                            if (StringUtil.isBlank(path)) {
                                this.message("node[" + path + "] is invalid");
                                this.processedSkip();
                                continue;
                            }
                            // 节点状态
                            boolean exists = this.client.exists(path);
                            // 跳过
                            if (this.ignoreExist && exists) {
                                this.message("node[" + path + "] is exists, skip it");
                                this.processedSkip();
                                continue;
                            }
                            String data = record.size() < 2 ? null : (String) record.get(1);
                            String dataStr = TextUtil.changeCharset(data, StandardCharsets.UTF_8.name(), this.config.charset());
                            // 更新
                            if (exists) {
                                this.client.setData(path, dataStr);
                                this.message("update node[" + path + "] success");
                            } else {// 创建
                                this.client.create(path, dataStr, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, true);
                                this.message("create node[" + path + "] success");
                            }
                            this.processedIncr();
                        } catch (Exception ex) {
                            this.processedDecr();
                            this.message("create node[" + path + "] failed");
                        }
                    }
                }
            } finally {
                reader.close();
                this.message("Imported From -> " + this.config.filePath());
            }
        } else {
            JulLog.error("未找到可用的读取器，文件类型:{}", this.fileType);
        }
        this.message("Import Finished");
    }

    public void charset(String charset) {
        if (StringUtil.isBlank(charset)) {
            this.config.charset(StandardCharsets.UTF_8.name());
        } else {
            this.config.charset(charset);
        }
    }

    public void filePath(String filePath) {
        this.config.filePath(filePath);
    }

    public void txtIdentifier(Character txtIdentifier) {
        this.config.txtIdentifier(txtIdentifier);
    }
}

