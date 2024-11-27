package cn.oyzh.easyzk.file;

import cn.oyzh.common.util.FileNameUtil;
import lombok.experimental.UtilityClass;

import java.io.IOException;

/**
 * @author oyzh
 * @since 2024-11-27
 */
@UtilityClass
public class ZKFileHelper {

    /**
     * 初始化写入器
     *
     * @param fileType 文件类型
     * @param config   配置
     * @param filePath 文件路径
     * @param columns  字段列表
     * @return 文件写入器
     * @throws IOException 异常
     */
    public static ZKTypeFileWriter initWriter(String fileType, ZKDataExportConfig config, String filePath, FileColumns columns) throws IOException {
        if (FileNameUtil.isExcelType(fileType)) {
            return new ZKExcelTypeFileWriter(filePath, config, columns);
        }
        if (FileNameUtil.isHtmlType(fileType)) {
            return new ZKHtmlTypeFileWriter(filePath, config, columns);
        }
        if (FileNameUtil.isJsonType(fileType)) {
            return new ZKJsonTypeFileWriter(filePath, config, columns);
        }
        if (FileNameUtil.isXmlType(fileType)) {
            return new ZKXmlTypeFileWriter(filePath, config, columns);
        }
        if (FileNameUtil.isCsvType(fileType)) {
            return new ZKCsvTypeFileWriter(filePath, config, columns);
        }
        if (FileNameUtil.isTxtType(fileType)) {
            return new ZKTxtTypeFileWriter(filePath, config, columns);
        }
        return null;
    }
}
