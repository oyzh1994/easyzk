package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.FXListView;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.util.FontUtil;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.store.ZKSearchHistoryStore;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Popup;

import java.util.Collections;
import java.util.List;

/**
 * zk搜索历史弹窗
 *
 * @author oyzh
 * @since 2023/4/24
 */
public class ZKSearchHistoryPopup extends Popup {

    /**
     * 类型
     */
    private final int type;

    /**
     * 列表视图组件
     */
    private FXListView<String> view;

    /**
     * 搜索历史储存
     */
    private final ZKSearchHistoryStore historyStore = ZKSearchHistoryStore.INSTANCE;

    public ZKSearchHistoryPopup(int type) {
        this.type = type;
    }

    @Override
    public void show(Node node, double anchorX, double anchorY) {
        // 初始化内容
        this.initContent();
        // 展示弹窗，修正x、y值
        super.show(node, anchorX - 8, anchorY + 5);
    }

    /**
     * 初始化内容
     */
    private void initContent() {
        // 初始化视图列表，弹窗
        if (this.view == null) {
            this.view = new FXListView<>();
            this.view.setFontSize(12);
            this.getContent().setAll(view);
            this.setAutoHide(true);
            this.view.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (this.type == 1) {
                        EventUtil.fire(ZKEventTypes.ZK_SEARCH_HISTORY_SELECTED, newValue);
                    } else {
                        EventUtil.fire(ZKEventTypes.ZK_REPLACE_HISTORY_SELECTED, newValue);
                    }
                    this.hide();
                }
            });
            this.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    this.hide();
                }
            });
        }
        // 清除旧数据
        this.view.getItems().clear();
        List<String> list = this.type == 1 ? this.historyStore.getSearchKw() : this.historyStore.getReplaceKw();
        // 无数据设置默认宽高
        if (list.isEmpty()) {
            this.view.setWidthAll(50);
            this.view.setHeightAll(120);
        } else {
            // 计算列表视图宽
            double width = 0;
            for (String s : list) {
                double w = FontUtil.stringWidth(s);
                if (w > width) {
                    width = w;
                }
            }
            if (width > 300) {
                width = 300;
            } else {
                width += 40;
            }
            // 计算列表视图高
            double fontHeight = FontUtil.calcFontHeight(12) + 8;
            double height = fontHeight * list.size();
            if (list.size() == 1) {
                height += 5;
            }
            if (height > 300) {
                height = 300;
            }
            // 反转数组，最新的数据排在前面
            Collections.reverse(list);
            // 初始化列表
            this.view.getItems().setAll(list);
            this.view.setWidthAll(width);
            this.view.setHeightAll(height);
        }
        this.view.setCursor(Cursor.HAND);
    }
}
