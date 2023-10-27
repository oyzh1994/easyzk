package cn.oyzh.easyzk.fx;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.store.ZKSearchHistoryStore;
import cn.oyzh.fx.plus.ext.SearchHistoryPopup;

import java.util.List;

/**
 * zk搜索历史弹窗
 *
 * @author oyzh
 * @since 2023/4/24
 */
public class ZKSearchHistoryPopup extends SearchHistoryPopup {

    /**
     * 类型
     */
    private final int type;

    /**
     * 搜索历史储存
     */
    private final ZKSearchHistoryStore historyStore = ZKSearchHistoryStore.INSTANCE;

    public ZKSearchHistoryPopup(int type) {
        this.type = type;
    }

    @Override
    public List<String> getHistories() {
        List<String> list = this.historyStore.getKw(this.type);
        if (CollUtil.isNotEmpty(list)) {
            return list.reversed();
        }
        return list;
    }
}
