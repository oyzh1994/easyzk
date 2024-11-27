package cn.oyzh.easyzk.file;

import cn.oyzh.common.file.LineFileWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class ZKCsvTypeFileWriter extends ZKTypeFileWriter {

    /**
     * 字段列表
     */
    private FileColumns columns;

    /**
     * 导出配置
     */
    private ZKDataExportConfig config;

    /**
     * 文件读取器
     */
    private final LineFileWriter writer;

    public ZKCsvTypeFileWriter(String filePath, ZKDataExportConfig config, FileColumns columns) throws FileNotFoundException {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.charset());
    }

    @Override
    public void writeHeader() throws Exception {
        this.writer.write(this.formatLine(this.columns.columnNames(), ",", this.config.txtIdentifier(), this.config.recordSeparator()));
    }

    @Override
    public void writeRecord(FileRecord record) throws Exception {
        Object[] values = new Object[record.size()];
        for (Map.Entry<Integer, Object> entry : record.entrySet()) {
            int index = entry.getKey();
            Object val = entry.getValue();
            values[index] = val;
        }
        this.writer.write(this.formatLine(values, ",", this.config.txtIdentifier(), this.config.recordSeparator()));
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.config = null;
        this.columns = null;
    }
}
