package cn.oyzh.easyzk.search;

import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.common.util.BooleanUtil;
import cn.oyzh.fx.common.util.CollectionUtil;
import cn.oyzh.fx.plus.controls.popup.FXPopup;
import cn.oyzh.fx.plus.controls.popup.ListViewPopup;
import cn.oyzh.fx.plus.controls.view.FXListView;
import cn.oyzh.fx.plus.font.FontUtil;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.node.NodeManager;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.util.ListViewUtil;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.util.Callback;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
