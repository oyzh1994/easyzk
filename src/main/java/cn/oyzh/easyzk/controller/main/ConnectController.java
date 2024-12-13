package cn.oyzh.easyzk.controller.main;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeView;
import cn.oyzh.fx.gui.svg.pane.SortSVGPane;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;


/**
 * zk数据历史业务
 *
 * @author oyzh
 * @since 2024/04/23
 */
public class ConnectController extends SubStageController {

    /**
     * 左侧zk树
     */
    @FXML
    private ZKConnectTreeView tree;

    /**
     * 节点排序组件
     */
    @FXML
    private SortSVGPane sortPane;

    // /**
    //  * 节点排序(正序)
    //  */
    // @FXML
    // private SVGGlyph sortAsc;
    //
    // /**
    //  * 节点排序(倒序)
    //  */
    // @FXML
    // private SVGGlyph sortDesc;

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {

    }

    // /**
    //  * 对子节点排序，正序
    //  */
    // @FXML
    // private void sortAsc() {
    //     this.sortAsc.disappear();
    //     this.sortDesc.display();
    //     this.tree.sortAsc();
    // }
    //
    // /**
    //  * 对子节点排序，倒序
    //  */
    // @FXML
    // private void sortDesc() {
    //     this.sortDesc.disappear();
    //     this.sortAsc.display();
    //     this.tree.sortDesc();
    // }

    /**
     * 定位节点
     */
    @FXML
    private void positionNode() {
        this.tree.scrollTo(this.tree.getSelectedItem());
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        // 关闭连接
        this.tree.closeConnects();
        // 取消F5按键监听
        KeyListener.unListenReleased(this.tree, KeyCode.F5);
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // this.sortAsc.managedBindVisible();
        // this.sortDesc.managedBindVisible();
        // zk树变化事件
        this.tree.selectItemChanged(ZKEventUtil::treeItemChanged);
        // 文件拖拽初始化
        this.stage.initDragFile(this.tree.getDragContent(), this.tree.getRoot()::dragFile);
        // 刷新触发事件
        KeyListener.listenReleased(this.tree, KeyCode.F5, keyEvent -> this.tree.reload());
    }

    @FXML
    private void addConnect() {
        ZKEventUtil.addConnect();
    }

    @FXML
    private void sortTree() {
        if (this.sortPane.isAsc()) {
            this.tree.sortAsc();
            this.sortPane.desc();
        } else {
            this.tree.sortDesc();
            this.sortPane.asc();
        }
    }
}
