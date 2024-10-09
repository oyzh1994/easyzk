package cn.oyzh.easyzk.search;

import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.common.util.BooleanUtil;
import cn.oyzh.fx.common.util.CollectionUtil;
import cn.oyzh.fx.plus.controls.popup.FXPopup;
import cn.oyzh.fx.plus.controls.view.FXListView;
import cn.oyzh.fx.plus.font.FontUtil;
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

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * zk搜索历史弹窗
 *
 * @author oyzh
 * @since 2023/4/24
 */
public class ZKNodeSearchPopup extends FXPopup {

    {
        this.showingProperty().addListener((observableValue, windowEventEventHandler, t1) -> {
            if (BooleanUtil.isTrue(t1)) {
                this.initContent();
                this.calcListViewSize();
            } else {
                ExecutorUtil.start(this::clearItems, 100);
            }
        });
        NodeManager.init(this);
    }

    /**
     * 选中事件
     */
    @Getter
    @Setter
    protected Consumer<String> onItemSelected;

    /**
     * 选中事件
     */
    @Getter
    @Setter
    protected Consumer<Integer> onIndexSelected;

    /**
     * 单列数据高
     */
    @Getter
    @Setter
    protected double cellDataHeight = 20;

    /**
     * 初始化内容
     */
    protected void initContent() {
        FXListView<String> listView = this.listView();
        if (listView == null) {
            listView = new FXListView<>();
            this.content(listView);
            listView.setFontSize(11);
            listView.setCursor(Cursor.HAND);
            listView.selectedItemChanged((observableValue, s, t1) -> {
                if (this.onItemSelected != null && this.isShowing()) {
                    this.onItemSelected.accept(t1);
                }
            });
            listView.selectedIndexChanged((observableValue, s, t1) -> {
                if (this.onIndexSelected != null && this.isShowing()) {
                    this.onIndexSelected.accept(t1.intValue());
                }
            });

            listView.setCellFactory(new Callback<>() {
                @Override
                public ListCell<String> call(ListView<String> param) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                this.setText(null);
                            } else {
                                this.setText(item);
                                this.setPrefHeight(cellDataHeight);
                                ListViewUtil.highlightCell(this);
                            }
                        }
                    };
                }
            });

        }
        this.setItems(this.getItems());
    }

    /**
     * 获取列表组件
     *
     * @return 列表组件
     */
    public FXListView<String> listView() {
        return (FXListView<String>) CollectionUtil.getFirst(this.getContent());
    }

    /**
     * 获取搜索历史
     *
     * @return 搜索历史
     */
    public List<String> getItems() {
        return Collections.emptyList();
    }

    /**
     * 设置搜索历史
     *
     * @param items 搜索历史
     */
    public void setItems(List<String> items) {
        if (items != null && this.listView() != null) {
            FXUtil.runWait(() -> this.listView().setItem(items));
        }
    }

    /**
     * 清除搜索历史
     */
    public void clearItems() {
        if (this.listView() != null) {
            FXUtil.runWait(() -> this.listView().clearItems());
        }
    }

    /**
     * 计算列表组件大小
     */
    public void calcListViewSize() {
        FXListView<String> listView = this.listView();
        if (listView == null) {
            return;
        }
        List<String> list = this.getItems();
        // 无数据设置默认宽高
        if (CollectionUtil.isEmpty(list)) {
            listView.setRealWidth(50);
            listView.setRealHeight(120);
        } else {
            // 计算列表视图宽
            double width = 0;
            Font font = this.listView().getFont();
            for (String s : list) {
                double w = FontUtil.stringWidth(s, font);
                if (w > width) {
                    width = w;
                }
            }
            // 限制宽度
            if (width > 300) {
                width = 300;
            } else {
                width += 60;
            }
            // 显示个数限制为10个
            int size = Math.min(list.size(), 10);
            // 修正高
            double height = this.cellDataHeight * size + 3;
            listView.setRealWidth(width);
            listView.setRealHeight(height);
        }
    }

    /**
     * 显示组件
     *
     * @param ownerNode 父节点
     * @param event     鼠标事件
     */
    public void show(@NonNull Node ownerNode, @NonNull MouseEvent event) {
        this.show(ownerNode, event.getScreenX(), event.getScreenY());
    }

}
