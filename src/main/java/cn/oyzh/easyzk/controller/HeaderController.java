package cn.oyzh.easyzk.controller;

import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.FXController;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.FXAlertUtil;
import cn.oyzh.fx.plus.node.NodeGroupManage;
import cn.oyzh.fx.plus.svg.SVGLabel;
import cn.oyzh.fx.plus.view.FXView;
import cn.oyzh.fx.plus.view.FXViewUtil;
import cn.oyzh.easyzk.controller.auth.ZKAuthMainController;
import cn.oyzh.easyzk.controller.filter.ZKFilterMainController;
import cn.oyzh.easyzk.controller.info.ZKInfoTransportController;
import cn.oyzh.easyzk.event.ZKEventTypes;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class HeaderController extends FXController {

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
        FXView fxView = FXViewUtil.getView(ZKAuthMainController.class);
        if (fxView != null) {
            fxView.toFront();
        } else {
            FXViewUtil.showView(ZKAuthMainController.class);
        }
    }

    /**
     * 设置
     */
    @FXML
    private void setting() {
        FXView fxView = FXViewUtil.getView(SettingController.class);
        if (fxView != null) {
            fxView.toFront();
        } else {
            FXViewUtil.showView(SettingController.class, this.view);
        }
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        FXViewUtil.showView(AboutController.class, this.view);
    }

    /**
     * 过滤
     */
    @FXML
    private void filter() {
        FXView fxView = FXViewUtil.getView(ZKFilterMainController.class);
        if (fxView != null) {
            fxView.toFront();
        } else {
            FXViewUtil.showView(ZKFilterMainController.class);
        }
    }

    /**
     * 退出
     */
    @FXML
    private void quit() {
        if (FXAlertUtil.confirm("确定退出" + this.project.getName() + "？")) {
            EventUtil.fire(ZKEventTypes.APP_EXIT);
        }
    }

    /**
     * 传输数据
     */
    @FXML
    private void transport() {
        FXView fxView = FXViewUtil.getView(ZKInfoTransportController.class);
        if (fxView != null) {
            fxView.toFront();
        } else {
            FXViewUtil.showView(ZKInfoTransportController.class);
        }
    }

    /**
     * 收缩左侧zk树
     */
    @FXML
    private void collapseTree() {
        this.treeMutexes.visible( this.expandTree);
        EventUtil.fire(ZKEventTypes.LEFT_COLLAPSE);
    }

    /**
     * 展开左侧zk树
     */
    @FXML
    private void expandTree() {
        this.treeMutexes.visible( this.collapseTree);
        EventUtil.fire(ZKEventTypes.LEFT_EXTEND);
    }

    @Override
    public void onViewShown(WindowEvent event) {
        super.onViewShown(event);
        this.treeMutexes.addNodes(this.collapseTree, this.expandTree);
        this.treeMutexes.manageBindVisible();
    }
}
