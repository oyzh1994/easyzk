package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.controller.info.ZKInfoTransportController;
import cn.oyzh.easyzk.controller.tool.ZKToolController;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.svg.SVGLabel;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeMutexes;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.plus.window.StageAdapter;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2022/1/26
 */
@Lazy
@Component
public class HeaderController extends SubStageController {

    /**
     * 项目信息
     */
    @Resource
    private Project project;

    /**
     * 展开zk树
     */
    @FXML
    private SVGLabel expandTree;

    /**
     * 收缩zk树
     */
    @FXML
    private SVGLabel collapseTree;

    /**
     * zk树互斥器
     */
    private final NodeMutexes treeMutexes = new NodeMutexes();

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
        if (MessageBox.confirm(I18nHelper.quit() + " " + this.project.getName())) {
            StageManager.exit();
        }
    }

    /**
     * 传输数据
     */
    @FXML
    private void transport() {
        StageAdapter wrapper = StageManager.getStage(ZKInfoTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(ZKInfoTransportController.class);
        }
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
        StageManager.showStage(ZKToolController.class, StageManager.getPrimaryStage());
    }

    /**
     * 收缩左侧zk树
     */
    @FXML
    private void collapseTree() {
        this.treeMutexes.visible(this.expandTree);
        ZKEventUtil.leftCollapse();
    }

    /**
     * 展开左侧zk树
     */
    @FXML
    private void expandTree() {
        this.treeMutexes.visible(this.collapseTree);
        ZKEventUtil.leftExtend();
    }

    /**
     * 搜索
     */
    @FXML
    private void search() {
        ZKEventUtil.searchFire();
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.treeMutexes.addNodes(this.collapseTree, this.expandTree);
        this.treeMutexes.manageBindVisible();
    }
}
