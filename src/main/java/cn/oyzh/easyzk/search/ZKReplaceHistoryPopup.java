package cn.oyzh.easyzk.search;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.store.ZKSearchHistoryStore;
import cn.oyzh.fx.plus.controls.popup.SearchHistoryPopup;

import java.util.List;

/**
 * zk替换历史弹窗
 *
 * @author oyzh
 * @since 2024/04/02
 */
public class ZKReplaceHistoryPopup extends SearchHistoryPopup {

    /**
     * 搜索历史储存
     */
    private final ZKSearchHistoryStore historyStore = ZKSearchHistoryStore.INSTANCE;

    @Override
    public List<String> getHistories() {
        List<String> list = this.historyStore.getReplaceKw();
        if (CollUtil.isNotEmpty(list)) {
            return list.reversed();
        }
        return list;
    }
}
