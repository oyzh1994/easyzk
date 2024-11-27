package cn.oyzh.easyzk.file;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.common.xls.WorkbookHelper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public class ZKExcelTypeFileWriter extends ZKTypeFileWriter {

    /**
     * 字段列表
     */
    private FileColumns columns;

    /**
     * 导出配置
     */
    private ZKDataExportConfig config;

    /**
     * xls工作薄
     */
    private Workbook workbook;

    /**
     * xls行记录
     */
    private int xlsRowIndex = 1;

    public ZKExcelTypeFileWriter(ZKDataExportConfig config, FileColumns columns) throws IOException, InvalidFormatException {
        this.columns = columns;
        this.config = config;
        boolean isXlsx = StringUtil.endWithIgnoreCase(config.filePath(), ".xlsx");
        this.workbook = WorkbookHelper.create(isXlsx);
    }

    @Override
    public void writeHeader() throws Exception {
        // 重置行索引
        this.xlsRowIndex = 1;
        // 创建一个新的工作表sheet
        Sheet sheet = this.workbook.createSheet("Nodes");
        // 创建列名行
        Row headerRow = sheet.createRow(0);
        // 写入列名
        List<FileColumn> columnList = columns.sortOfPosition();
        for (int i = 0; i < columnList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnList.get(i).getName());
        }
        // 写入数据
        WorkbookHelper.write(this.workbook, this.config.filePath());
    }

    /**
     * 写入记录
     *
     * @param record 记录
     * @param flush  是否刷新
     * @throws Exception 异常
     */
    private void writeRecord(FileRecord record, boolean flush) throws Exception {
        // 处理数据
        Object[] values = new Object[record.size()];
        for (Map.Entry<Integer, Object> entry : record.entrySet()) {
            int index = entry.getKey();
            Object val = entry.getValue();
            values[index] = val;
        }
        // 获取当前页
        Sheet sheet = WorkbookHelper.getActiveSheet(this.workbook);
        // 创建数据行
        Row row = sheet.createRow(this.xlsRowIndex++);
        // 填充数据列
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            Object val = values[i];
            switch (val) {
                case null -> {
                }
                case Date v -> cell.setCellValue(v);
                case Double v -> cell.setCellValue(v);
                case String v -> cell.setCellValue(v);
                case Boolean v -> cell.setCellValue(v);
                case Calendar v -> cell.setCellValue(v);
                case LocalDate v -> cell.setCellValue(v);
                case LocalDateTime v -> cell.setCellValue(v);
                case Number v -> cell.setCellValue(v.doubleValue());
                default -> cell.setCellValue(val.toString());
            }
        }
        // 写入数据
        if (flush) {
            WorkbookHelper.write(this.workbook, this.config.filePath());
        }
    }

    @Override
    public void writeRecord(FileRecord record) throws Exception {
        this.writeRecord(record, true);
    }

    @Override
    public void writeRecords(List<FileRecord> records) throws Exception {
        for (FileRecord record : records) {
            this.writeRecord(record, false);
        }
        // 写入数据
        WorkbookHelper.write(this.workbook, this.config.filePath());
    }

    @Override
    public void close() throws IOException {
        this.workbook.close();
        this.workbook = null;
        this.config = null;
        this.columns = null;
    }
}
