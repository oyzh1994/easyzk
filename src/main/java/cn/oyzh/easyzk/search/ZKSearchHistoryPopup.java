package cn.oyzh.easyzk.search;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.store.ZKSearchHistoryStore;
import cn.oyzh.fx.plus.controls.popup.SearchHistoryPopup;

import java.util.List;

/**
 * zk搜索历史弹窗
 *
 * @author oyzh
 * @since 2023/4/24
 */
public class ZKSearchHistoryPopup extends SearchHistoryPopup {

    /**
     * 搜索历史储存
     */
    private final ZKSearchHistoryStore historyStore = ZKSearchHistoryStore.INSTANCE;

    @Override
    public List<String> getHistories() {
        List<String> list = this.historyStore.getSearchKw();
        if (CollUtil.isNotEmpty(list)) {
            return list.reversed();
        }
        return list;
    }
}
