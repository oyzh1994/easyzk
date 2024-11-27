package cn.oyzh.easyzk.file;

import cn.oyzh.common.util.FileNameUtil;
import lombok.experimental.UtilityClass;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

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
     * @param columns  字段列表
     * @return 文件写入器
     * @throws IOException 异常
     */
    public static ZKTypeFileWriter initWriter(String fileType, ZKDataExportConfig config, FileColumns columns) throws IOException, InvalidFormatException {
        if (FileNameUtil.isExcelType(fileType)) {
            return new ZKExcelTypeFileWriter(config, columns);
        }
        if (FileNameUtil.isHtmlType(fileType)) {
            return new ZKHtmlTypeFileWriter(config, columns);
        }
        if (FileNameUtil.isJsonType(fileType)) {
            return new ZKJsonTypeFileWriter(config, columns);
        }
        if (FileNameUtil.isXmlType(fileType)) {
            return new ZKXmlTypeFileWriter(config, columns);
        }
        if (FileNameUtil.isCsvType(fileType)) {
            return new ZKCsvTypeFileWriter(config, columns);
        }
        if (FileNameUtil.isTxtType(fileType)) {
            return new ZKTxtTypeFileWriter(config, columns);
        }
        return null;
    }
}
