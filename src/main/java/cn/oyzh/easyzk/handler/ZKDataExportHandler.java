package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.file.FileColumns;
import cn.oyzh.easyzk.file.FileRecord;
import cn.oyzh.easyzk.file.ZKCsvTypeFileWriter;
import cn.oyzh.easyzk.file.ZKDataExportConfig;
import cn.oyzh.easyzk.file.ZKExcelTypeFileWriter;
import cn.oyzh.easyzk.file.ZKHtmlTypeFileWriter;
import cn.oyzh.easyzk.file.ZKJsonTypeFileWriter;
import cn.oyzh.easyzk.file.ZKTxtTypeFileWriter;
import cn.oyzh.easyzk.file.ZKTypeFileWriter;
import cn.oyzh.easyzk.file.ZKXmlTypeFileWriter;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.i18n.I18nHelper;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.IOException;
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
    private String fileType;

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
     * 导出配置
     */
    private ZKDataExportConfig config = new ZKDataExportConfig();

    /**
     * 是否sql类型
     *
     * @return 结果
     */
    public boolean isSqlType() {
        return "sql".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否xml类型
     *
     * @return 结果
     */
    public boolean isXmlType() {
        return "xml".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否csv类型
     *
     * @return 结果
     */
    public boolean isCsvType() {
        return "csv".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否html类型
     *
     * @return 结果
     */
    public boolean isHtmlType() {
        return "html".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否xls类型
     *
     * @return 结果
     */
    public boolean isXlsType() {
        return "xls".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否xlsx类型
     *
     * @return 结果
     */
    public boolean isXlsxType() {
        return "xlsx".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否excel类型
     *
     * @return 结果
     */
    public boolean isExcelType() {
        return this.isXlsType() || this.isXlsxType();
    }

    /**
     * 是否json类型
     *
     * @return 结果
     */
    public boolean isJsonType() {
        return "json".equalsIgnoreCase(this.fileType);
    }

    /**
     * 是否txt类型
     *
     * @return 结果
     */
    public boolean isTxtType() {
        return "txt".equalsIgnoreCase(this.fileType);
    }

    /**
     * 执行导出
     *
     * @throws Exception 异常
     */
    public void doExport() throws Exception {
        this.message("Export Starting");
        FileColumns columns = new FileColumns();
        columns.addColumn(I18nHelper.nodePath());
        columns.addColumn(I18nHelper.nodeData());
        ZKTypeFileWriter writer = this.initWriter(this.exportFile.getPath(), columns);
        try {
            this.writeHeader(writer);
            ZKNodeUtil.loopNode(this.client, this.nodePath, zkNode -> {
                FileRecord record = new FileRecord();
                record.put(0, zkNode.nodePath());
                record.put(1, zkNode.nodeDataStr(Charset.defaultCharset()));
                writeRecord(writer, record);
            });
            this.writeTail(writer);
        } finally {
            // this.message("Exporting Table " + tableName + " To -> " + table.getFilePath());
        }
        this.message("Export Finished");
    }

    private ZKTypeFileWriter initWriter(String filePath, FileColumns columns) throws IOException {
        if (this.isExcelType()) {
            return new ZKExcelTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isHtmlType()) {
            return new ZKHtmlTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isJsonType()) {
            return new ZKJsonTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isXmlType()) {
            return new ZKXmlTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isCsvType()) {
            return new ZKCsvTypeFileWriter(filePath, this.config, columns);
        }
        if (this.isTxtType()) {
            return new ZKTxtTypeFileWriter(filePath, this.config, columns);
        }
        return null;
    }

    /**
     * 写入头
     *
     * @throws IOException 异常
     */
    private void writeHeader(ZKTypeFileWriter writer) throws Exception {
        writer.writeHeader();
    }

    /**
     * 写入记录
     *
     * @param record 记录列表
     * @throws IOException 异常
     */
    private void writeRecord(ZKTypeFileWriter writer, FileRecord record) {
        try {
            writer.writeRecord(record);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 写入尾
     *
     * @throws IOException 异常
     */
    private void writeTail(ZKTypeFileWriter writer) throws Exception {
        writer.writeTrial();
        writer.close();
    }

    public void recordSeparator(String recordSeparator) {
        this.config.recordSeparator(recordSeparator);
    }

    public void txtIdentifier(String txtIdentifier) {
        this.config.txtIdentifier(txtIdentifier);
    }
}

