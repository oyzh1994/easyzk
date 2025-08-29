package cn.oyzh.easyzk.controller;

import cn.oyzh.common.SysConst;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.util.ZKViewFactory;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.gui.svg.pane.LayoutSVGPane2;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2022/1/26
 */
public class HeaderController5 extends StageController {

    /**
     * 布局组件
     */
    @FXML
    private LayoutSVGPane2 layoutPane;

    /**
     * 设置
     */
    @FXML
    private void setting() {
        ZKViewFactory.setting();
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        ZKViewFactory.about();
    }

    /**
     * 退出
     */
    @FXML
    private void quit() {
        if (MessageBox.confirm(I18nHelper.quit() + " " + SysConst.projectName())) {
            StageManager.exit();
        }
    }

    /**
     * 传输数据
     */
    @FXML
    private void transport() {
        ZKViewFactory.transportData(null);
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
        ZKViewFactory.tool();
    }

    /**
     * 布局1事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void layout1(Layout1Event event) {
        this.layoutPane.layout2();
    }

    /**
     * 布局2事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void layout2(Layout2Event event) {
        this.layoutPane.layout1();
    }

    /**
     * 布局
     */
    @FXML
    private void layout() {
        if (!this.layoutPane.isLayout1()) {
            ZKEventUtil.layout2();
        } else {
            ZKEventUtil.layout1();
        }
    }

    /**
     * 迁移
     */
    @FXML
    private void migration() {
        ZKViewFactory.migrationData();
    }
}
