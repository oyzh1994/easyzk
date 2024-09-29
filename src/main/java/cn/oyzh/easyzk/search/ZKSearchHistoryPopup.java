package cn.oyzh.easyzk.search;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.store.ZKSearchHistoryStore2;
import cn.oyzh.fx.plus.controls.popup.SearchHistoryPopup;

import java.util.List;

/**
 * zk搜索历史弹窗
 *
 * @author oyzh
 * @since 2023/4/24
 */
public class ZKSearchHistoryPopup extends SearchHistoryPopup {

    private final byte type;

    /**
     * 搜索历史储存
     */
    private final ZKSearchHistoryStore2 historyStore = ZKSearchHistoryStore2.INSTANCE;

    public ZKSearchHistoryPopup(byte type) {
        this.type = type;
    }

    @Override
    public List<String> getHistories() {
        List<String> list = this.historyStore.listKw(this.type);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.reversed();
        }
        return list;
    }
}
