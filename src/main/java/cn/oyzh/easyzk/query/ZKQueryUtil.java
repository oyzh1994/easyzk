package cn.oyzh.easyzk.query;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.terminal.command.TerminalCommandHandler;
import cn.oyzh.fx.terminal.util.TerminalManager;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author oyzh
 * @since 2024/2/21
 */
@UtilityClass
public class ZKQueryUtil {

    /**
     * 关键字
     */
    private static final Set<String> KEYWORDS = new HashSet<>();

    static {
        // 设置内容提示符
        Collection<TerminalCommandHandler<?, ?>> handlers = TerminalManager.listHandler();
        for (TerminalCommandHandler<?, ?> handler : handlers) {
            if (StringUtil.isNotBlank(handler.commandName())) {
                KEYWORDS.add(handler.commandName());
            }
            if (StringUtil.isNotBlank(handler.commandSubName())) {
                KEYWORDS.add(handler.commandSubName());
            }
            if (StringUtil.isNotBlank(handler.commandFullName())) {
                KEYWORDS.add(handler.commandFullName());
            }
        }
    }

    public static Set<String> getKeywords() {
        return KEYWORDS;
    }

    public static double clacCorr(String str, String text) {
        str = str.toUpperCase();
        text = text.toUpperCase();
        if (!str.contains(text) && !text.contains(str)) {
            return 0.d;
        }
        double corr = StringUtil.similarity(str, text);
        if (str.startsWith(text)) {
            corr += 0.3;
        } else if (str.contains(text)) {
            corr += 0.2;
        } else if (str.endsWith(text)) {
            corr += 0.1;
        }
        return corr;
    }

    /**
     * 初始化提示词
     *
     * @param token   提示词
     * @param minCorr 最低相关度
     * @return 结果
     */
    public static List<ZKQueryPromptItem> initPrompts(ZKQueryToken token, float minCorr) {
        if (token == null || token.isEmpty()) {
            return Collections.emptyList();
        }
        // 当前提示词
        String text = token.getContent().toUpperCase();
        // 提示词列表
        final List<ZKQueryPromptItem> items = new CopyOnWriteArrayList<>();
        // 任务列表
        List<Runnable> tasks = new ArrayList<>();
        // 关键字
        if (token.isPossibilityKeyword()) {
            tasks.add(() -> ZKQueryUtil.getKeywords().parallelStream().forEach(keyword -> {
                // 计算相关度
                double corr = ZKQueryUtil.clacCorr(keyword, text);
                if (corr > minCorr) {
                    ZKQueryPromptItem item = new ZKQueryPromptItem();
                    item.setType((byte) 4);
                    item.setContent(keyword);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 执行任务
        ThreadUtil.submit(tasks);
        // 根据相关度排序
        List<ZKQueryPromptItem> itemList = items.parallelStream().sorted(Comparator.comparingDouble(ZKQueryPromptItem::getCorrelation)).collect(Collectors.toList());
        // 反转列表
        return itemList.reversed();
    }
}
