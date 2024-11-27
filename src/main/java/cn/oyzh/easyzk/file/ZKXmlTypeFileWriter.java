package cn.oyzh.easyzk.file;

import cn.oyzh.common.file.LineFileWriter;
import cn.oyzh.common.util.IOUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class ZKXmlTypeFileWriter extends ZKTypeFileWriter {

    /**
     * 字段列表
     */
    private FileColumns columns;

    /**
     * 导出配置
     */
    private ZKDataExportConfig config;

    /**
     * 文件写入器
     */
    private LineFileWriter writer;

    public ZKXmlTypeFileWriter(String filePath, ZKDataExportConfig config, FileColumns columns) throws FileNotFoundException {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.charset());
    }

    @Override
    public void writeHeader() throws Exception {
        this.writer.writeLine("<?xml version=\"1.0\" standalone=\"yes\"?>");
        this.writer.writeLine("<RECORDS>");
    }

    @Override
    public void writeTrial() throws Exception {
        this.writer.writeLine("</RECORDS>");
    }

    @Override
    public void writeRecord(FileRecord record) throws Exception {
        StringBuilder builder;
        builder = new StringBuilder("  <RECORD>\n");
        for (Map.Entry<Integer, Object> entry : record.entrySet()) {
            String columnName = this.columns.columnName(entry.getKey());
            // 名称
            builder.append("   <").append(columnName);
            Object val = entry.getValue();
            if (val != null) {
                builder.append(">");
                builder.append(val);
                builder.append("</").append(columnName).append(">");
            } else {
                builder.append("/>");
            }
            builder.append("\n");
        }
        builder.append("  </RECORD>");
        this.writer.writeLine(builder.toString());
    }

    @Override
    public void close() throws IOException {
        IOUtil.close(this.writer);
        this.writer = null;
        this.config = null;
        this.columns = null;
    }

    @Override
    public Object parameterized(FileColumn column, Object value, ZKDataExportConfig config) {
        if (value == null) {
            return null;
        }
        return super.parameterized(column, value, config);
    }
}
