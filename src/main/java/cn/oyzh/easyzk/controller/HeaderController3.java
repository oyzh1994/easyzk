package cn.oyzh.easyzk.controller;

import cn.oyzh.common.SysConst;
import cn.oyzh.easyzk.controller.data.ZKDataMigrationController;
import cn.oyzh.easyzk.controller.tool.ZKToolController;
import cn.oyzh.easyzk.event.ZKEventUtil;
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
public class HeaderController3 extends StageController {

    /**
     * 设置
     */
    @FXML
    private void setting() {
//        StageAdapter wrapper = StageManager.getStage(SettingController2.class);
//        if (wrapper != null) {
//            wrapper.toFront();
//        } else {
//            StageManager.showStage(SettingController2.class, this.stage);
//        }
        ZKEventUtil.showSetting();
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        StageManager.showStage(AboutController.class, this.stage);
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
//        StageAdapter wrapper = StageManager.getStage(ZKDataTransportController.class);
//        if (wrapper != null) {
//            wrapper.toFront();
//        } else {
//            StageManager.showStage(ZKDataTransportController.class, this.stage);
//        }
        ZKEventUtil.showTransportData();
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
        StageManager.showStage(ZKToolController.class, StageManager.getPrimaryStage());
    }

    /**
     * 布局1
     */
    @FXML
    private void layout1() {
        ZKEventUtil.layout1();
    }

    /**
     * 布局2
     */
    @FXML
    private void layout2() {
        ZKEventUtil.layout2();
    }

    /**
     * 迁移
     */
    @FXML
    private void migration() {
        StageManager.showStage(ZKDataMigrationController.class, this.stage);
    }
}
