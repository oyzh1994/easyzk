package cn.oyzh.easyzk.file;


import java.io.Closeable;
import java.util.List;

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
     * @param value 值
     * @return 参数化后的值
     */
    public Object parameterized(Object value) {
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

    protected String formatLine(Object[] objects, String prefix, String fieldSeparator, String txtIdentifier, String recordSeparator) {
        return this.formatLine(List.of(objects), prefix, fieldSeparator, txtIdentifier, recordSeparator);
    }

    protected String formatLine(List<?> list, String prefix, String fieldSeparator, String txtIdentifier, String recordSeparator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object val : list) {
            if (prefix != null) {
                sb.append(prefix);
            }
            if (txtIdentifier != null) {
                sb.append(txtIdentifier);
            }
            sb.append(val);
            if (txtIdentifier != null) {
                sb.append(txtIdentifier);
            }
            if (first) {
                first = false;
            } else {
                sb.append(fieldSeparator);
            }
        }
        sb.append(recordSeparator);
        return sb.toString();
    }

}
