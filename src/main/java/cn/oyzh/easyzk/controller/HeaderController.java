package cn.oyzh.easyzk.controller;

import cn.oyzh.common.SysConst;
import cn.oyzh.easyzk.controller.data.ZKDataTransportController;
import cn.oyzh.easyzk.controller.data.ZKDataMigrationController;
import cn.oyzh.easyzk.controller.tool.ZKToolController;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2022/1/26
 */
public class HeaderController extends SubStageController {

    /**
     * 项目信息
     */
//    @Resource
//    private final Project project = Project.load();

//    /**
//     * 展开zk树
//     */
//    @FXML
//    private SVGLabel expandTree;
//
//    /**
//     * 收缩zk树
//     */
//    @FXML
//    private SVGLabel collapseTree;

//    /**
//     * zk树互斥器
//     */
//    private final NodeMutexes treeMutexes = new NodeMutexes();

    /**
     * 认证管理
     */
    @FXML
    private void auth() {
        ZKEventUtil.authMain();
    }

    /**
     * 设置
     */
    @FXML
    private void setting() {
        StageAdapter wrapper = StageManager.getStage(SettingController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(SettingController.class, this.stage);
        }
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        StageManager.showStage(AboutController.class, this.stage);
    }

    /**
     * 过滤
     */
    @FXML
    private void filter() {
        ZKEventUtil.filterMain();
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
        StageAdapter wrapper = StageManager.getStage(ZKDataTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(ZKDataTransportController.class);
        }
        // StageAdapter wrapper = StageManager.getStage(ZKInfoTransportController.class);
        // if (wrapper != null) {
        //     wrapper.toFront();
        // } else {
        //     StageManager.showStage(ZKInfoTransportController.class);
        // }
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
        StageManager.showStage(ZKToolController.class, StageManager.getPrimaryStage());
    }

//    /**
//     * 收缩左侧zk树
//     */
//    @FXML
//    private void collapseTree() {
//        this.treeMutexes.visible(this.expandTree);
//        ZKEventUtil.leftCollapse();
//    }
//
//    /**
//     * 展开左侧zk树
//     */
//    @FXML
//    private void expandTree() {
//        this.treeMutexes.visible(this.collapseTree);
//        ZKEventUtil.leftExtend();
//    }

//    @Override
//    public void onStageShown(WindowEvent event) {
//        super.onStageShown(event);
//        this.treeMutexes.addNodes(this.collapseTree, this.expandTree);
//        this.treeMutexes.manageBindVisible();
//    }

    @FXML
    private void layout1() {
        ZKEventUtil.layout1();
    }

    @FXML
    private void layout2() {
        ZKEventUtil.layout2();
    }

    @FXML
    private void migration() {
        StageManager.showStage(ZKDataMigrationController.class);
    }
}
