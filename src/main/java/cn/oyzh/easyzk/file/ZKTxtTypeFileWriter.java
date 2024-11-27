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
public class ZKTxtTypeFileWriter extends ZKTypeFileWriter {

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

    public ZKTxtTypeFileWriter(ZKDataExportConfig config, FileColumns columns) throws FileNotFoundException {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(config.filePath(), config.charset());
    }

    @Override
    public void writeRecord(FileRecord record) throws Exception {
        Object[] values = new Object[record.size()];
        for (Map.Entry<Integer, Object> entry : record.entrySet()) {
            int index = entry.getKey();
            Object val = this.parameterized(entry.getValue());
            values[index] = val;
        }
        this.writer.write(this.formatLine(values, " ", null, "\n"));
    }

    @Override
    public Object parameterized(Object value) {
        if (value == null) {
            return "";
        }
        return JSONUtil.escape(value.toString());
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.writer = null;
        this.config = null;
        this.columns = null;
    }
}
