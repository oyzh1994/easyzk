package cn.oyzh.easyzk.file;

import cn.oyzh.common.file.SkipAbleFileReader;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class ZKTxtTypeFileReader extends ZKTypeFileReader {

    /**
     * 字段列表
     */
    private List<String> columns;

    /**
     * 导入配置
     */
    private ZKDataImportConfig config;

    /**
     * 文件读取器
     */
    private SkipAbleFileReader reader;

    public ZKTxtTypeFileReader(@NonNull File file, ZKDataImportConfig config) throws IOException {
        this.config = config;
        this.reader = new SkipAbleFileReader(file, Charset.forName(config.charset()));
        // 设置换行符
        if (!Objects.equals(config.recordSeparator(), System.lineSeparator())) {
            this.reader.lineBreak(config.recordSeparator());
        }
        this.init();
    }

    @Override
    protected void init() throws IOException {
        this.reader.jumpLine(this.config.columnIndex());
        this.columns = new ArrayList<>();
        String line = this.reader.readLine();
        this.columns.addAll(this.parseLine(line, this.config.txtIdentifierChar(), this.config.fieldSeparatorChar()));
        this.reader.jumpLine(this.config.dataStartIndex());
    }

    @Override
    public Map<String, Object> readObject() throws IOException {
        String line = this.reader.readLine();
        if (line != null) {
            List<String> arr = this.parseLine(line, this.config.txtIdentifierChar(), this.config.fieldSeparatorChar());
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < arr.size(); i++) {
                map.put(this.columns.get(i), arr.get(i));
            }
            return map;
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
        this.reader = null;
        this.config = null;
        this.columns.clear();
        this.columns = null;
    }
}
