package cn.oyzh.easyzk.search;

import cn.oyzh.fx.gui.skin.ClearableTextFieldSkin;
import cn.oyzh.fx.gui.svg.glyph.SettingSVGGlyph;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Getter;

/**
 * 搜索文本输入框皮肤
 *
 * @author oyzh
 * @since 2023/10/9
 */
public class ZKNodeSearchTextFieldSkin extends ClearableTextFieldSkin {

    /**
     * 搜索历史按钮
     */
    protected final SVGGlyph button;

    /**
     * 搜索历史弹窗
     */
    @Getter
    protected ZKNodeSearchPopup popup;

    /**
     * 初始化弹窗
     */
    protected void initPopup() {
        this.popup = new ZKNodeSearchPopup();
        this.popup.show(this.getSkinnable());
        this.popup.setOnIndexSelected(this::onIndexSelected);
    }

    /**
     * 显示弹窗
     */
    protected void showPopup() {
        if (this.popup != null) {
            if (this.popup.isShowing()) {
                this.closePopup();
            } else {
                this.popup.show(this.getSkinnable());
            }
        }
    }

    public void onIndexSelected(int index) {
        this.closePopup();
        this.onSearch(this.getSkinnable().getText());
        this.getSkinnable().setPromptText(this.popup.getItems().get(index));
    }

    /**
     * 关闭历史弹窗组件
     */
    protected void closePopup() {
        if (this.popup != null && this.popup.isShowing()) {
            this.popup.hide();
        }
    }

    public ZKNodeSearchTextFieldSkin(TextField textField) {
        super(textField);
        // 初始化弹窗
        this.initPopup();
        // 初始化按钮
        this.button = new SettingSVGGlyph();
        this.button.setEnableWaiting(false);
        this.button.setFocusTraversable(false);
        this.button.setPadding(new Insets(0));
        this.button.setOnMousePrimaryClicked(e -> this.showPopup());
        this.button.setOnMouseMoved(mouseEvent -> this.button.setColor("#E36413"));
        this.button.setOnMouseExited(mouseEvent -> this.button.setColor(this.getButtonColor()));
        this.getChildren().add(this.button);
        // 鼠标监听
        this.getSkinnable().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> this.closePopup());
        // 按键监听
        this.getSkinnable().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.onSearch(this.getText());
            }
        });
        // 文本变化监听
        this.getSkinnable().textProperty().addListener((observable, oldValue, newValue) -> this.onSearch(this.getText()));

    }

    public void onSearch(String text) {
        this.closePopup();
    }

    @Override
    protected Color getButtonColor() {
        if (!ThemeManager.isDarkMode()) {
            return Color.valueOf("#696969");
        }
        return super.getButtonColor();
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        // 组件大小
        double size = h * .8;
        // 计算组件大小
        double btnSize = this.snapSizeX(size);
        // 设置组件大小
        this.button.setSize(size);
        // 获取边距
        Insets padding = this.getSkinnable().getPadding();
        // 计算左边距
        double paddingLeft = btnSize + 8;
        // 设置左边距
        if (padding.getLeft() != paddingLeft) {
            padding = new Insets(padding.getTop(), padding.getRight(), padding.getBottom(), paddingLeft);
            this.getSkinnable().setPadding(padding);
        }
        // 设置组件位置
        super.positionInArea(this.button, 3, y * 0.9, w, h, btnSize, HPos.LEFT, VPos.CENTER);
    }

    public int getSelectedIndex() {
        if (this.popup == null) {
            return 0;
        }
        return this.popup.getSelectedIndex();
    }

    public void setSelectedIndex(int selectedIndex) {
        if (this.popup != null) {
            FXListView<String> listView = this.popup.listView();
            if (listView != null) {
                listView.selectIndex(selectedIndex);
            }
        }
    }
}
