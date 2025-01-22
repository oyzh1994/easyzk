package cn.oyzh.easyzk.filter;

import cn.oyzh.easyzk.popups.ZKFilterSettingPopupController;
import cn.oyzh.fx.gui.skin.ClearableTextFieldSkin;
import cn.oyzh.fx.gui.svg.glyph.SettingSVGGlyph;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
 * 过滤文本输入框皮肤
 *
 * @author oyzh
 * @since 2023/10/9
 */
public class ZKNodeFilterTextFieldSkin extends ClearableTextFieldSkin {

    /**
     * 过滤历史按钮
     */
    protected final SVGGlyph button;

    /**
     * 过滤历史弹窗
     */
    @Getter
    protected PopupAdapter popup;

    private ObjectProperty<ZKNodeFilterParam> filterParamProperty;

    public ZKNodeFilterParam filterParam() {
        if (this.filterParamProperty == null) {
            return new ZKNodeFilterParam();
        }
        return this.filterParamProperty.get();
    }

    public ObjectProperty<ZKNodeFilterParam> filterParamProperty() {
        if (this.filterParamProperty == null) {
            this.filterParamProperty = new SimpleObjectProperty<>();
        }
        return this.filterParamProperty;
    }

    /**
     * 显示弹窗
     */
    protected void showPopup() {
        if (this.popup != null) {
            this.closePopup();
        }
        this.popup = PopupManager.parsePopup(ZKFilterSettingPopupController.class);
        this.popup.setProp("filterParam", this.filterParam());
        this.popup.setSubmitHandler(o -> {
            if (o instanceof ZKNodeFilterParam param) {
                this.filterParamProperty().set(param);
                this.onSearch(this.getText());
            }
        });
        this.popup.show(this.button);
    }

    /**
     * 关闭历史弹窗组件
     */
    protected void closePopup() {
        if (this.popup != null && this.popup.isShowing()) {
            this.popup.hide();
        }
    }

    public ZKNodeFilterTextFieldSkin(TextField textField) {
        super(textField);
        // 初始化按钮
        this.button = new SettingSVGGlyph();
        this.button.setEnableWaiting(false);
        this.button.setFocusTraversable(false);
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
}
