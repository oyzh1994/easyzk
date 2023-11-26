package cn.oyzh.easyzk.util;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.dto.ZKNodeExport;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.common.util.OSUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * zk导出工具类
 *
 * @author oyzh
 * @since 2023/3/18
 */
//@Slf4j
@UtilityClass
public class ZKExportUtil {

    /**
     * 换行符替换文本，已过时，仅v1.5.5版本支持，后续版本将淘汰此配置
     */
    @Deprecated
    private static final String LINE_REPLACE = "__@line@__";

    /**
     * 文本分隔符
     */
    private static final String TEXT_LINE_SEPARATOR = "<<<" + "-".repeat(50) + ">>>";

    /**
     * 替换数据
     *
     * @param data 数据
     * @return 替换的数据
     */
    @Deprecated
    public static String replaceData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        return data.lines().collect(Collectors.joining(LINE_REPLACE));
    }

    /**
     * 复原数据
     *
     * @param data 数据
     * @return 复原的数据
     */
    @Deprecated
    public static String restoreData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        return data.replaceAll(LINE_REPLACE, System.lineSeparator());
    }

//    /**
//     * 从zk节点数据生成
//     *
//     * @param zkNodes zk节点数据
//     * @return 数据字符串
//     */
//    public static String fromNodes(@NonNull List<ZKNode> zkNodes, boolean isJSON, String charset) {
//        String exportData;
//        Project project = SpringUtil.getBean(Project.class);
//        //ZKNode fistNode = CollectionUtil.first(zkNodes);
//        String version = project.getVersion();
//        String platform = OSUtil.getOSType();
//        //String charset = fistNode != null ? fistNode.charset() : null;
//        if (isJSON) {
//            ZKNodeExport export = new ZKNodeExport();
//            // 元信息
//            export.setNodes(new ArrayList<>());
//            export.setVersion(version);
//            export.setCharset(charset);
//            export.setPlatform(platform);
//            // 拼接数据
//            if (CollectionUtil.isNotEmpty(zkNodes)) {
//                for (ZKNode n : zkNodes) {
//                    Map<String, String> node = new HashMap<>();
//                    node.put("path", n.nodePath());
//                    String data = n.nodeDataStr(charset);
//                    if (data != null) {
//                        node.put("data", data);
//                    }
//                    export.getNodes().add(node);
//                }
//            }
//            exportData = export.toJSONString();
//        } else {
//            StringBuilder builder = new StringBuilder();
//            // 元信息
//            builder.append("**")
//                    .append("version=").append(version).append(" ")
//                    .append("charset=").append(charset).append(" ")
//                    .append("platform=").append(platform).append(" ")
//                    .append("**")
//                    .append(System.lineSeparator());
//            // 拼接数据
//            for (ZKNode n : zkNodes) {
//                String nodeData = n.nodeDataStr();
//                // 格式化数据的换行符
//                nodeData = replaceData(nodeData);
//                builder.append(n.nodePath()).append(" ").append(nodeData).append(System.lineSeparator());
//            }
//            exportData = builder.toString();
//        }
//        return exportData;
//    }

    /**
     * zk节点数据生成文本
     *
     * @param zkNodes zk节点数据
     * @param charset 字符集
     * @param prefix  前缀
     * @return 数据文本字符串
     */
    public static String nodesToTxt(@NonNull List<ZKNode> zkNodes, String charset, String prefix) {
        Project project = SpringUtil.getBean(Project.class);
        String version = project.getVersion();
        String platform = OSUtil.getOSType();
        StringBuilder builder = new StringBuilder();
        // 元信息
        builder.append("**")
                .append("prefix=").append(prefix).append(" ")
                .append("version=").append(version).append(" ")
                .append("charset=").append(charset).append(" ")
                .append("platform=").append(platform)
                .append("**");
        // 拼接数据
        for (ZKNode n : zkNodes) {
            builder.append(System.lineSeparator()).append(TEXT_LINE_SEPARATOR).append(System.lineSeparator());
            // 拼接前缀
            if (!StrUtil.isBlank(prefix)) {
                builder.append(prefix).append(" ");
            }
            // 拼接数据
            builder.append(n.nodePath()).append(" ").append(n.nodeData());
            // builder.append(n.nodePath()).append(" ").append(n.nodeDataStr(charset));
        }
        return builder.toString();
    }

    /**
     * zk节点数据生成json字符串
     *
     * @param zkNodes      zk节点数据
     * @param charset      字符集
     * @param prettyFormat 美化
     * @return 数据json字符串
     */
    public static String nodesToJSON(@NonNull List<ZKNode> zkNodes, String charset, boolean prettyFormat) {
        Project project = SpringUtil.getBean(Project.class);
        String version = project.getVersion();
        String platform = OSUtil.getOSType();
        ZKNodeExport export = new ZKNodeExport();
        // 元信息
        export.setNodes(new ArrayList<>());
        export.setVersion(version);
        export.setCharset(charset);
        export.setPlatform(platform);
        // 拼接数据
        for (ZKNode n : zkNodes) {
            Map<String, String> node = new HashMap<>();
            node.put("path", n.nodePath());
            // String data = n.nodeData();
            String data = n.nodeDataStr(charset);
            // String data = n.nodeDataStr(charset);
            if (data != null) {
                node.put("data", data);
            }
            export.getNodes().add(node);
        }
        return export.toJSONString(prettyFormat);
    }

    /**
     * 从文件生成
     *
     * @param file 文件
     * @return ZKNodeExport
     */
    public static ZKNodeExport fromFile(@NonNull File file) {
        String text = FileUtil.readUtf8String(file);
        if (StrUtil.equalsIgnoreCase(FileNameUtil.extName(file), "json")) {
            return fromJSON(text);
        }
        return fromTxt(text);
    }

    /**
     * 从json对象数据生成
     *
     * @param json json字符串
     * @return ZKNodeExport
     */
    public static ZKNodeExport fromJSON(@NonNull String json) {
        StaticLog.info("json: {}", json);
        JSONObject object = JSONObject.parseObject(json);
        ZKNodeExport export = new ZKNodeExport();
        export.setNodes(new ArrayList<>());
        export.setVersion(object.getString("version"));
        export.setCharset(object.getString("charset"));
        export.setPlatform(object.getString("platform"));
        JSONArray nodes = object.getJSONArray("nodes");
        for (Object n : nodes) {
            JSONObject o = (JSONObject) n;
            Map<String, String> node = new HashMap<>();
            node.put("path", o.getString("path"));
            if (o.containsKey("data")) {
                node.put("data", o.getString("data"));
            } else {
                node.put("data", "");
            }
            export.getNodes().add(node);
        }
        return export;
    }

    /**
     * 从文本数据生成
     *
     * @param txt 文本
     * @return ZKNodeExport
     */
    public static ZKNodeExport fromTxt(@NonNull String txt) {
        StaticLog.info("txt: {}", txt);
        ZKNodeExport export = new ZKNodeExport();
        export.setNodes(new ArrayList<>());
        // 分割数据
        List<String> txtList = txt.lines().collect(Collectors.toList());
        // 元数据行
        int metaLine = -1;
        // 处理元数据
        for (int i = 0; i < txtList.size(); i++) {
            String t = txtList.get(i);
            if (t.startsWith("**") && t.endsWith("**")) {
                metaLine = i;
                t = t.replace("**", "");
                String[] strs = t.split(" ");
                for (String str : strs) {
                    if (str.startsWith("charset=")) {
                        export.setCharset(str.replace("charset=", ""));
                    }
                    if (str.startsWith("version=")) {
                        export.setVersion(str.replace("version=", ""));
                    }
                    if (str.startsWith("platform=")) {
                        export.setPlatform(str.replace("platform=", ""));
                    }
                    if (str.startsWith("prefix=")) {
                        export.setPrefix(str.replace("prefix=", ""));
                    }
                }
                break;
            }
        }
        // 移除元数据
        if (metaLine != -1) {
            txtList.remove(metaLine);
        }
        // 是否v1.5.5版本
        boolean isV155 = txt.contains(LINE_REPLACE) || export.version().contains("1.5.5");
        //boolean isV155 = txt.contains(LINE_REPLACE) || export.version().contains("1.5.5") || !txt.contains(TEXT_LINE_SEPARATOR);
        if (isV155) {
            StaticLog.warn("当前导入数据版本为1.5.5版本");
            v1_5_5_txtHandle(export, txtList);
        } else {
            txtHandle(export, txtList);
        }
        return export;
    }

    /**
     * 处理数据，新版本
     *
     * @param export  导出对象
     * @param txtList 数据列表
     */
    private static void txtHandle(ZKNodeExport export, List<String> txtList) {
        // 当前行数据
        StringBuilder lineData = new StringBuilder();
        // 数据行处理方式
        Runnable func = () -> {
            try {
                if (lineData.length() > 0) {
                    String t = lineData.toString();
                    // 处理前缀
                    if (export.hasPrefix()) {
                        t = t.substring(export.getPrefix().length() + 1);
                    }
                    // 处理路径、数据
                    int index = t.indexOf(" ");
                    String path = index == -1 ? t : t.substring(0, index);
                    String data = index == -1 ? "" : t.substring(index + 1);
                    Map<String, String> node = new HashMap<>();
                    node.put("path", path);
                    node.put("data", data);
                    export.getNodes().add(node);
                    // 清空缓冲
                    lineData.setLength(0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
        // 处理数据行
        for (String t : txtList) {
            // 当前为分割行
            if (t.equals(TEXT_LINE_SEPARATOR)) {
                func.run();
//                if (log.isDebugEnabled()) {
                    StaticLog.debug("寻找到分割行.");
//                }
            } else {// 当前是数据行
                lineData.append(t);
//                if (log.isDebugEnabled()) {
                    StaticLog.debug("寻找到数据行.");
//                }
            }
        }
        func.run();
    }

    /**
     * v1.5.5版本处理数据
     * 注意，v1.5.5版本的导出数据处理有缺陷，已废弃
     *
     * @param export  导出对象
     * @param txtList 数据列表
     */
    private static void v1_5_5_txtHandle(ZKNodeExport export, List<String> txtList) {
        // 处理数据行
        for (String t : txtList) {
            if (t.startsWith("/")) {// 节点路径、数据
                int index = t.indexOf(" ");
                String path = index == -1 ? t : t.substring(0, index);
                String data = index == -1 ? "" : t.substring(index + 1);
                Map<String, String> node = new HashMap<>();
                node.put("path", path);
                node.put("data", restoreData(data));
                export.getNodes().add(node);
            } else {
                StaticLog.warn("数据:{} 不合法", t);
            }
        }
    }
}
