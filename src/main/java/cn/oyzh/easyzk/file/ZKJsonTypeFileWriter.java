package cn.oyzh.easyzk.file;

import cn.oyzh.common.file.LineFileWriter;
import cn.oyzh.common.json.JSONUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class ZKJsonTypeFileWriter extends ZKTypeFileWriter {

    /**
     * 字段列表
     */
    private FileColumns columns;

    /**
     * 文件读取器
     */
    private LineFileWriter writer;

    /**
     * 是否首次写入
     */
    private boolean firstWrite = true;

    /**
     * 导出配置
     */
    private ZKDataExportConfig config;

    public ZKJsonTypeFileWriter(String filePath, ZKDataExportConfig config, FileColumns columns) throws FileNotFoundException {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.charset());
    }

    @Override
    public void writeHeader() throws Exception {
        this.writer.writeLine("[");
    }

    @Override
    public void writeTrial() throws Exception {
        this.writer.write("\n]");
    }

    @Override
    public void writeRecord(FileRecord record) throws Exception {
        if (!this.firstWrite) {
            this.writer.write(",\n");
        }
        int size = record.size();
        StringBuilder builder = new StringBuilder("  {\n");
        for (Map.Entry<Integer, Object> entry : record.entrySet()) {
            String columnName = this.columns.columnName(entry.getKey());
            // 名称
            builder.append("   \"").append(columnName).append("\" : ");
            // 值处理
            Object val = this.parameterized(entry.getValue());
            builder.append(val);
            if (--size != 0) {
                builder.append(",\n");
            } else {
                builder.append("\n");
            }
        }
        builder.append("  }");
        this.writer.write(builder.toString());
        this.firstWrite = false;
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.writer = null;
        this.config = null;
        this.columns = null;
    }

    @Override
    public Object parameterized(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number) {
            return value;
        }
        return "\"" + JSONUtil.escape(value.toString()) + "\"";
    }
}
