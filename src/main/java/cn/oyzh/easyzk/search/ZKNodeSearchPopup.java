package cn.oyzh.easyzk.search;

import cn.oyzh.fx.plus.controls.popup.ListViewPopup;
import cn.oyzh.i18n.I18nHelper;

import java.util.List;

/**
 * zk搜索历史弹窗
 *
 * @author oyzh
 * @since 2023/4/24
 */
public class ZKNodeSearchPopup extends ListViewPopup<String> {

    @Override
    public List<String> getItems() {
        return List.of(I18nHelper.contains(), I18nHelper.containsCaseSensitive(), I18nHelper.matchWholeWord(), I18nHelper.matchWholeWordCaseSensitive());
    }
}
