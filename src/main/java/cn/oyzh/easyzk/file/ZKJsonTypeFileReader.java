package cn.oyzh.easyzk.file;

import cn.oyzh.common.util.FileUtil;
import com.alibaba.fastjson.JSONReader;
import lombok.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-03
 */
public class ZKJsonTypeFileReader extends ZKTypeFileReader {

    /**
     * json读取器
     */
    private JSONReader reader;

    /**
     * 导入配置
     */
    private ZKDataImportConfig config;

    public ZKJsonTypeFileReader(@NonNull File file, ZKDataImportConfig config) throws FileNotFoundException {
        this.config = config;
        this.reader = new JSONReader(FileUtil.getReader(file, Charset.forName(config.charset())));
        this.init();
    }

    @Override
    protected void init() {
        // 初始化
        if (this.reader.hasNext()) {
            if (this.config.recordLabel() == null) {
                this.reader.startArray();
            } else {
                this.reader.startObject();
                String key = this.reader.readString();
                if (key.equalsIgnoreCase(this.config.recordLabel())) {
                    this.reader.startArray();
                }
            }
        }
    }

    @Override
    public Map<String, Object> readObject() {
        if (this.reader.hasNext()) {
            return this.reader.readObject(HashMap.class);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        if (this.config.recordLabel() == null) {
            this.reader.endArray();
            this.reader.endArray();
        } else {
            this.reader.endArray();
            this.reader.endObject();
        }
        this.reader.close();
        this.reader = null;
        this.config = null;
    }
}
