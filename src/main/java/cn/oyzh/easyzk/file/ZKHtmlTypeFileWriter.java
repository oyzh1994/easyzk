package cn.oyzh.easyzk.file;

import cn.oyzh.common.file.LineFileWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class ZKHtmlTypeFileWriter extends ZKTypeFileWriter {

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

    public ZKHtmlTypeFileWriter(String filePath, ZKDataExportConfig config, FileColumns columns) throws FileNotFoundException {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.charset());
    }

    @Override
    public void writeHeader() throws Exception {
        String head = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <style>
                table{
                border-collapse: collapse;
                width: 100%;
                }
                th, td{
                text-align: left;
                padding: 8px;
                }
                tr:nth-child(even){
                background-color: #fafafa;
                }
                th{
                background-color: #7799AA;
                color: white;
                }
                </style>
                </head>
                <body>
                <table>
                """;
        List<FileColumn> columnList = columns.sortOfPosition();
        StringBuilder builder = new StringBuilder(head);
        builder.append("\n<tr>");
        for (FileColumn dbColumn : columnList) {
            builder.append("<th>").append(dbColumn.getName()).append("</th>");
        }
        builder.append("</tr>");
        this.writer.writeLine(builder.toString());
    }

    @Override
    public void writeTrial() throws Exception {
        String tail = """
                </table>
                </body>
                </html>
                """;
        this.writer.writeLine(tail);
    }

    @Override
    public void writeRecord(FileRecord object) throws Exception {
        StringBuilder builder = new StringBuilder("<tr>");
        Object[] values = new Object[object.size()];
        for (Map.Entry<Integer, Object> entry : object.entrySet()) {
            int index = entry.getKey();
            Object val = entry.getValue();
            values[index] = val;
        }
        for (Object val : values) {
            builder.append("<td>").append(val).append("</td>");
        }
        builder.append("</tr>");
        this.writer.writeLine(builder.toString());
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
        this.writer = null;
        this.config = null;
        this.columns = null;
    }
}
