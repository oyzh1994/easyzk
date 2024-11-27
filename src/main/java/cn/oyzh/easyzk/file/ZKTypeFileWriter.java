package cn.oyzh.easyzk.file;


import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public abstract class ZKTypeFileWriter implements Closeable {

    protected void init() throws Exception {

    }

    /**
     * 参数化
     *
     * @param column 字段
     * @param value  值
     * @param config 导出配置
     * @return 参数化后的值
     */
    public Object parameterized(FileColumn column, Object value, ZKDataExportConfig config) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    /**
     * 写入头
     *
     * @throws Exception 异常
     */
    public void writeHeader() throws Exception {

    }

    /**
     * 写入尾
     *
     * @throws Exception 异常
     */
    public void writeTrial() throws Exception {

    }

    /**
     * 写入对象
     *
     * @param record 对象
     * @throws Exception 异常
     */
    public abstract void writeRecord(FileRecord record) throws Exception;

    /**
     * 写入多个对象
     *
     * @param records 对象
     * @throws Exception 异常
     */
    public void writeRecords(List<FileRecord> records) throws Exception {
        for (FileRecord object : records) {
            this.writeRecord(object);
        }
    }

    protected String formatLine(Object[] objects, String fieldSeparator, String txtIdentifier, String recordSeparator) {
        return this.formatLine(List.of(objects), fieldSeparator, txtIdentifier, recordSeparator);
    }

    protected String formatLine(List<?> list, String fieldSeparator, String txtIdentifier, String recordSeparator) {
        StringBuilder sb = new StringBuilder();
        for (Object val : list) {
            sb.append(fieldSeparator)
                    .append(txtIdentifier)
                    .append(val)
                    .append(txtIdentifier);
        }
        sb.append(recordSeparator);
        return sb.substring(1);
    }

}
