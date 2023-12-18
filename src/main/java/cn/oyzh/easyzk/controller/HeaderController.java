package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.controller.info.ZKInfoTransportController;
import cn.oyzh.easyzk.controller.tool.ZKToolController;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.svg.SVGLabel;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupManage;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2022/1/26
 */
@Lazy
@Component
public class HeaderController extends SubController {

    /**
     * 项目信息
     */
    @Autowired
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
    private final NodeGroupManage treeMutexes = new NodeGroupManage();

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
        StageWrapper wrapper = StageUtil.getStage(SettingController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageUtil.showStage(SettingController.class, this.stage);
        }
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        StageUtil.showStage(AboutController.class, this.stage);
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
        if (MessageBox.confirm("确定退出" + this.project.getName() + "？")) {
            EventUtil.fire(ZKEventTypes.APP_EXIT);
        }
    }

    /**
     * 传输数据
     */
    @FXML
    private void transport() {
        StageWrapper wrapper = StageUtil.getStage(ZKInfoTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageUtil.showStage(ZKInfoTransportController.class);
        }
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
        StageUtil.showStage(ZKToolController.class, StageUtil.getPrimaryStage());
    }

    /**
     * 收缩左侧zk树
     */
    @FXML
    private void collapseTree() {
        this.treeMutexes.visible(this.expandTree);
        EventUtil.fire(ZKEventTypes.LEFT_COLLAPSE);
    }

    /**
     * 展开左侧zk树
     */
    @FXML
    private void expandTree() {
        this.treeMutexes.visible(this.collapseTree);
        EventUtil.fire(ZKEventTypes.LEFT_EXTEND);
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.treeMutexes.addNodes(this.collapseTree, this.expandTree);
        this.treeMutexes.manageBindVisible();
    }
}
