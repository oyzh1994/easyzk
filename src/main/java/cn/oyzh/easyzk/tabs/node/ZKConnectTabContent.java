package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.easyzk.controller.acl.ZKACLAddController;
import cn.oyzh.easyzk.controller.acl.ZKACLUpdateController;
import cn.oyzh.easyzk.controller.node.ZKNodeQRCodeController;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.fx.ZKACLControl;
import cn.oyzh.easyzk.fx.ZKACLTableView;
import cn.oyzh.easyzk.fx.ZKDataFormatComboBox;
import cn.oyzh.easyzk.search.ZKNodeSearchTextField;
import cn.oyzh.easyzk.search.ZKNodeSearchTypeComboBox;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItemUtil;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeView;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.dto.FriendlyInfo;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.CollectionUtil;
import cn.oyzh.fx.common.util.TextUtil;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.combo.CharsetComboBox;
import cn.oyzh.fx.plus.controls.page.PageBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.fx.plus.node.ResizeHelper;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.thread.RenderService;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.util.TableViewUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.util.Callback;
import lombok.Getter;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.Stat;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * zk节点tab内容组件
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKConnectTabContent extends DynamicTabController {

    /**
     * 左侧节点
     */
    @FXML
    private FlexVBox leftBox;

    /**
     * tab节点
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * 拉伸辅助器
     */
    private ResizeHelper resizeHelper;

    /**
     * 节点数
     */
    @FXML
    private ZKNodeTreeView treeView;

    /**
     * 节点排序(正序)
     */
    @FXML
    private SVGGlyph sortAsc;

    /**
     * 节点排序(倒序)
     */
    @FXML
    private SVGGlyph sortDesc;

    /**
     * 搜索类型
     */
    @FXML
    private ZKNodeSearchTypeComboBox searchType;

    /**
     * 搜索组件
     */
    @FXML
    private ZKNodeSearchTextField searchKW;

    /**
     * 右侧zk属性组件
     */
    @FXML
    private FlexVBox statPane;

    /**
     * zk属性视图切换按钮
     */
    @FXML
    private FXToggleSwitch statViewSwitch;

    /**
     * 内容搜索组件
     */
    @FXML
    private ClearableTextField dataSearch;

    /**
     * 右侧acl组件
     */
    @FXML
    private FlexVBox aclBox;

    /**
     * 分页信息
     */
    private Paging<ZKACL> aclPaging;

    /**
     * zk连接节点
     */
    @Getter
    private ZKConnectTreeItem treeItem;

    /**
     * 当前激活的节点
     */
    @Getter
    private ZKNodeTreeItem activeItem;

    /**
     * 右侧acl分页组件
     */
    @FXML
    private PageBox<ZKACL> aclPage;

    /**
     * 右侧zk权限视图切换按钮
     */
    @FXML
    private CharsetComboBox charset;

    /**
     * 右侧zk权限视图切换按钮
     */
    @FXML
    private FXToggleSwitch aclViewSwitch;

    @FXML
    private ZKACLTableView aclTableView;

    @FXML
    private FlexTableColumn<String, String> aclId;

    @FXML
    private FlexTableColumn<String, String> aclPerms;

    @FXML
    private FlexTableColumn<String, String> aclSchema;

    @FXML
    private FlexTableColumn<String, String> aclStatus;

    /**
     * 收藏节点
     */
    @FXML
    private SVGGlyph collect;

    /**
     * 取消收藏节点
     */
    @FXML
    private SVGGlyph unCollect;

    /**
     * 加载耗时
     */
    @FXML
    private FXLabel loadTime;

    /**
     * 右侧zk数据二维码视图
     */
    @FXML
    private SVGGlyph node2QRCode;

    /**
     * zk数据保存按钮
     */
    @FXML
    private SVGGlyph saveNodeData;

    /**
     * zk数据撤销
     */
    @FXML
    private SVGGlyph dataUndo;

    /**
     * zk数据重做
     */
    @FXML
    private SVGGlyph dataRedo;

    /**
     * 右侧zk数据
     */
    @FXML
    private RichDataTextAreaPane nodeData;

    /**
     * 格式
     */
    @FXML
    protected ZKDataFormatComboBox dataFormat;

    /**
     * 配额组件tab
     */
    @FXML
    protected FXTab quotaTab;

    /**
     * 子节点数量配额
     */
    @FXML
    protected NumberTextField quotaNum;

    /**
     * 节点数据大小配额
     */
    @FXML
    protected NumberTextField quotaBytes;

    /**
     * 配置储存对象
     */
    private final ZKSetting setting = ZKSettingStore2.SETTING;

    private ZKClient client;


    /**
     * 初始化
     *
     * @param item 树节点
     */
    public void init(ZKConnectTreeItem item) {
        this.treeItem = item;
        this.treeView.disable();
        this.client = item.client();
        // 异步执行
        ThreadUtil.start(() -> {
            try {
                // 获取根节点
                ZKNode rootNode = ZKNodeUtil.getNode(item.client(), "/");
                if (rootNode != null) {
                    // 生成根节点
                    ZKNodeTreeItem rootItem = ZKNodeTreeItemUtil.of(rootNode, this.treeView, item.client());
                    // 设置根节点
                    this.treeView.setRoot(rootItem);
                    // 加载节点
                    if (this.setting.isLoadFirst()) {
                        rootItem.loadChild();
                    } else if (this.setting.isLoadAll()) {
                        rootItem.loadChildAll();
                    }
                    // 监听选中变化
                    this.treeView.selectItemChanged(this::initItem);
                } else {
                    MessageBox.warn(item.value().getName() + I18nHelper.loadFail());
                }
            } finally {
                this.treeView.enable();
            }
        }, 100);
    }

    /**
     * 初始化节点
     *
     * @param treeItem 当前节点
     */
    private void initItem(TreeItem<?> treeItem) {
        try {
            this.activeItem = (ZKNodeTreeItem) treeItem;
            this.flushTab();
            if (this.activeItem != null) {
                this.initData();
                this.initAcl();
                this.initStat();
                this.initQuota();
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 重新载入权限
     */
    @FXML
    public void reloadACL() {
        try {
            this.activeItem.refreshACL();
            this.initAcl();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 添加权限
     */
    @FXML
    private void toAddACL() {
        StageAdapter fxView = StageManager.parseStage(ZKACLAddController.class, this.window());
        fxView.setProp("zkItem", this.activeItem);
        fxView.setProp("zkClient", this.activeItem.client());
        fxView.display();
    }

    /**
     * 重新载入配额
     */
    @FXML
    private void reloadQuota() {
        try {
            this.activeItem.refreshQuota();
            this.initQuota();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 复制访问控制
     */
    @FXML
    private void copyACL() {
        ZKACL acl = this.aclTableView.getSelectedItem();
        if (acl == null) {
            return;
        }
        try {
            String builder = acl.idFriend().getName(this.aclViewSwitch.isSelected()) + " " + acl.idFriend().getValue(this.aclViewSwitch.isSelected()) + System.lineSeparator() +
                    acl.schemeFriend().getName(this.aclViewSwitch.isSelected()) + " " + acl.schemeFriend().getValue(this.aclViewSwitch.isSelected()) + System.lineSeparator() +
                    acl.permsFriend().getName(this.aclViewSwitch.isSelected()) + " " + acl.permsFriend().getValue(this.aclViewSwitch.isSelected());
            ClipboardUtil.setStringAndTip(builder);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nHelper.operationException());
        }
    }

    /**
     * 修改权限
     */
    @FXML
    private void updateACL() {
        ZKACL acl = this.aclTableView.getSelectedItem();
        if (acl == null) {
            return;
        }
        try {
            StageAdapter fxView = StageManager.parseStage(ZKACLUpdateController.class, this.window());
            fxView.setProp("acl", acl);
            fxView.setProp("zkItem", this.activeItem);
            fxView.setProp("zkClient", this.treeItem.client());
            fxView.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nHelper.operationException());
        }
    }

    /**
     * 删除权限
     */
    @FXML
    private void deleteACL(MouseEvent event) {
        ZKACL acl = this.aclTableView.getSelectedItem();
        if (acl == null) {
            return;
        }
        if (!MessageBox.confirm(I18nHelper.deleteACL())) {
            return;
        }
        if (this.activeItem.acl().size() == 1) {
            MessageBox.warn(this.i18nString("zk.aclTip1"));
            return;
        }
        try {
            Stat stat = this.activeItem.deleteACL(acl);
            if (stat != null) {
                this.reloadACL();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 权限列表，上一页
     */
    @FXML
    private void aclPrevPage() {
        if (this.aclPaging != null) {
            this.renderACLView(this.aclPaging.prevPage());
        }
    }

    /**
     * 权限列表，下一页
     */
    @FXML
    private void aclNextPage() {
        if (this.aclPaging != null) {
            this.renderACLView(this.aclPaging.nextPage());
        }
    }

    /**
     * 渲染权限控件
     *
     * @param pageNo 页码
     */
    private void renderACLView(long pageNo) {
        // // 获取子节点
        // List<Node> nodes = this.aclBox.getChildren();
        // // 隐藏子节点
        // nodes.forEach(n -> n.setVisible(false));
        // 获取zk权限分页数据
        List<ZKACL> aclList = this.aclPaging.page(pageNo);
        // 设置分页信息
        this.aclPage.setPaging(this.aclPaging);
        List<ZKACLControl> list = new ArrayList<>();
        for (ZKACL zkacl : aclList) {
            ZKACLControl control = new ZKACLControl();
            control.setId(zkacl.getId());
            control.setPerms(zkacl.getPerms());
            control.setFriendly(this.aclViewSwitch.isSelected());
            control.setAuthed(ZKAuthUtil.isDigestAuthed(this.client, zkacl.idVal()));
            list.add(control);
        }
        this.aclTableView.setItem(list);

        // // 处理zk权限分页数据
        // for (int i = 0; i < aclList.size(); i++) {
        //     ZKACL acl = aclList.get(i);
        //     ZKACLVBox vBox = (ZKACLVBox) nodes.get(i);
        //     // 获取控件，设置acl信息，并显示
        //     vBox.acl(acl);
        //     vBox.setVisible(true);
        //     // 查找组件
        //     HBox idBox = (HBox) vBox.lookup(".acl-id");
        //     HBox permsBox = (HBox) vBox.lookup(".acl-perms");
        //     HBox schemeBox = (HBox) vBox.lookup(".acl-scheme");
        //     Text statusText = (Text) vBox.lookup(".acl-status");
        //     // 执行渲染
        //     BackgroundService.submitFX(() -> {
        //         this.handleACLState(acl, statusText);
        //         this.handleACLInfo(acl.idFriend(), idBox);
        //         this.handleACLInfo(acl.permsFriend(), permsBox);
        //         this.handleACLInfo(acl.schemeFriend(), schemeBox);
        //     });
        // }
        // // 获取子节点
        // List<Node> nodes = this.aclBox.getChildren();
        // // 隐藏子节点
        // nodes.forEach(n -> n.setVisible(false));
        // // 获取zk权限分页数据
        // List<ZKACL> aclList = this.aclPaging.page(pageNo);
        // // 设置分页信息
        // this.aclPage.setPaging(this.aclPaging);
        // // 处理zk权限分页数据
        // for (int i = 0; i < aclList.size(); i++) {
        //     ZKACL acl = aclList.get(i);
        //     ZKACLVBox vBox = (ZKACLVBox) nodes.get(i);
        //     // 获取控件，设置acl信息，并显示
        //     vBox.acl(acl);
        //     vBox.setVisible(true);
        //     // 查找组件
        //     HBox idBox = (HBox) vBox.lookup(".acl-id");
        //     HBox permsBox = (HBox) vBox.lookup(".acl-perms");
        //     HBox schemeBox = (HBox) vBox.lookup(".acl-scheme");
        //     Text statusText = (Text) vBox.lookup(".acl-status");
        //     // 执行渲染
        //     BackgroundService.submitFX(() -> {
        //         this.handleACLState(acl, statusText);
        //         this.handleACLInfo(acl.idFriend(), idBox);
        //         this.handleACLInfo(acl.permsFriend(), permsBox);
        //         this.handleACLInfo(acl.schemeFriend(), schemeBox);
        //     });
        // }
    }

    // /**
    //  * 处理权限状态
    //  *
    //  * @param acl  权限
    //  * @param text 组件
    //  */
    // private void handleACLState(ZKACL acl, Text text) {
    //     Set<String> digests = ZKAuthUtil.getAuthedDigest(this.treeItem.client());
    //     if (CollectionUtil.isNotEmpty(digests) && digests.contains(acl.idVal())) {
    //         text.setText("(" + I18nHelper.authed() + ")");
    //     } else {
    //         text.setText("");
    //     }
    // }
    //
    // /**
    //  * 处理权限属性
    //  *
    //  * @param info 属性
    //  * @param box  组件
    //  */
    // private void handleACLInfo(FriendlyInfo<ACL> info, HBox box) {
    //     // 获取标题和文本框
    //     Label label = (Label) box.getChildren().get(0);
    //     Label data = (Label) box.getChildren().get(1);
    //     // 设置属性值及属性值
    //     label.setText(info.getName(this.aclViewSwitch.isSelected()));
    //     data.setText(info.getValue(this.aclViewSwitch.isSelected()).toString());
    // }

    /**
     * 复制zk状态
     */
    @FXML
    private void copyStat() {
        List<FriendlyInfo<Stat>> statInfos = this.activeItem.statInfos();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < statInfos.size(); i++) {
            FriendlyInfo<Stat> statInfo = statInfos.get(i);
            builder.append(statInfo.getName(this.statViewSwitch.isSelected()))
                    .append(" : ")
                    .append(statInfo.getValue(this.statViewSwitch.isSelected()));
            if (statInfo != CollectionUtil.getLast(statInfos)) {
                builder.append(System.lineSeparator());
            }
        }
        ClipboardUtil.setStringAndTip(builder.toString(), "节点状态");
    }

    /**
     * 刷新zk状态
     */
    @FXML
    private void reloadStat() {
        try {
            this.activeItem.refreshStat();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 复制节点路径及数据
     */
    @FXML
    private void copyNode() {
        String data = this.activeItem.decodeNodePath() + " " + this.nodeData.getTextTrim();
        ClipboardUtil.setStringAndTip(data, "节点");
    }

    /**
     * 复制节点路径
     */
    @FXML
    private void copyNodePath() {
        ClipboardUtil.setStringAndTip(this.activeItem.decodeNodePath(), "节点路径");
    }

    /**
     * 刷新zk节点数据
     */
    @FXML
    private void reloadData() {
        // 放弃保存
        if (this.activeItem.isDataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            return;
        }
        // 刷新数据
        try {
            this.activeItem.refreshData();
            // 数据变更
            this.showData();
            this.flushTabGraphicColor();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 收藏节点
     */
    @FXML
    private void collect() {
        this.activeItem.collect();
        this.collect.disappear();
        this.unCollect.display();
    }

    /**
     * 取消收藏节点
     */
    @FXML
    private void unCollect() {
        this.activeItem.unCollect();
        this.collect.display();
        this.unCollect.disappear();
    }

    /**
     * 保存节点数据
     */
    @FXML
    private void saveNodeData() {
        if (this.activeItem.isDataTooLong()) {
            MessageBox.warn(I18nHelper.dataTooLarge());
            return;
        }
        // 保存数据
        if (this.activeItem.isDataUnsaved()) {
            // 保存数据
            RenderService.submit(() -> {
                this.activeItem.saveData();
                this.flushTabGraphicColor();
            });
        }
    }

    /**
     * 数据撤销
     */
    @FXML
    private void dataUndo() {
        this.nodeData.undo();
        this.nodeData.requestFocus();
    }

    /**
     * 数据重做
     */
    @FXML
    private void dataRedo() {
        this.nodeData.redo();
        this.nodeData.requestFocus();
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteData() {
        this.nodeData.paste();
        this.nodeData.requestFocus();
    }

    /**
     * 清空数据
     */
    @FXML
    private void clearData() {
        this.nodeData.clear();
        this.nodeData.requestFocus();
    }

    /**
     * zk节点转二维码
     */
    @FXML
    private void node2QRCode() {
        try {
            StageAdapter fxView = StageManager.parseStage(ZKNodeQRCodeController.class, this.window());
            fxView.setProp("zkNode", this.activeItem.value());
            fxView.setProp("nodeData", this.nodeData.getTextTrim());
            fxView.display();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 认证节点
     */
    @FXML
    private void authNode() {
        this.activeItem.authNode();
    }

    /**
     * zk数据控件按键事件
     *
     * @param e 事件
     */
    @FXML
    private void onNodeDataKeyPressed(KeyEvent e) {
        // 保存节点数据
        if (KeyboardUtil.isCtrlS(e)) {
            this.saveNodeData();
            e.consume();
        }
    }

    /**
     * 显示历史
     */
    @FXML
    private void showHistory() {
        ZKEventUtil.historyShow(this.activeItem);
    }

    /**
     * 显示数据
     */
    protected void showData() {
        byte[] bytes;
        if (this.activeItem.isDataUnsaved()) {
            bytes = this.activeItem.unsavedData();
        } else {
            bytes = this.activeItem.nodeData();
        }
        bytes = TextUtil.changeCharset(bytes, Charset.defaultCharset(), this.charset.getCharset());
        this.nodeData.showData(bytes);
    }

    /**
     * 显示数据
     *
     * @param dataType 数据类型
     */
    protected void showData(RichDataType dataType) {
        if (this.activeItem.isDataUnsaved()) {
            this.nodeData.showData(dataType, this.activeItem.unsavedData());
        } else {
            this.nodeData.showData(dataType, this.activeItem.nodeData());
        }
    }

    public void initData() {
        // 显示数据
        this.showData();
        // 遗忘历史
        this.nodeData.forgetHistory();
        // 加载耗时处理
        FXUtil.runWait(() -> this.loadTime.setText(I18nHelper.cost() + ":" + this.activeItem.loadTime() + "ms"));
    }

    /**
     * 初始化状态
     */
    public void initStat() {
        if (this.treeItem == null) {
            return;
        }
        List<FriendlyInfo<Stat>> statInfos = this.activeItem.statInfos();
        // 遍历属性
        for (int i = 0; i < statInfos.size(); i++) {
            FriendlyInfo<Stat> statInfo = statInfos.get(i);
            FlexHBox box = (FlexHBox) this.statPane.getChildren().get(i);
            Label label = (Label) box.getChildren().get(0);
            Label data = (Label) box.getChildren().get(1);
            data.setFocusTraversable(true);
            // 设置属性值及属性值
            FXUtil.runLater(() -> {
                label.setText(statInfo.getName(this.statViewSwitch.isSelected()));
                data.setText(statInfo.getValue(this.statViewSwitch.isSelected()).toString());
            });
        }
    }

    /**
     * 初始化权限
     */
    public void initAcl() {
        if (this.treeItem == null) {
            return;
        }
        if (this.activeItem.aclEmpty()) {
            this.aclViewSwitch.disable();
            this.aclTableView.clearItems();
        } else {
            List<ZKACL> aclList = this.activeItem.acl();
            // 获取分页控件
            this.aclPaging = new Paging<>(aclList, 10);
            // 渲染首页数据
            this.renderACLView(0);
        }
    }

    /**
     * 初始化配额
     */
    public void initQuota() throws Exception {
        if (this.treeItem == null) {
            return;
        }
        if (this.activeItem.isRoot()) {
            this.quotaTab.getContent().setDisable(true);
        } else {
            try {
                this.quotaTab.getContent().setDisable(false);
                StatsTrack quota = this.activeItem.quota();
                this.quotaNum.setValue(quota.getCount());
                this.quotaBytes.setValue(quota.getBytes());
            } catch (KeeperException.NoNodeException ignore) {
                this.quotaNum.setValue(-1);
                this.quotaBytes.setValue(-1);
            }
        }
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // undo监听
        this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
        // redo监听
        this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
        // 字符集选择事件
        this.charset.selectedItemChanged((t3, t2, t1) -> this.showData());
        // 切换显示监听
        this.aclViewSwitch.selectedChanged((t3, t2, t1) -> this.initAcl());
        // 切换显示监听
        this.statViewSwitch.selectedChanged((t3, t2, t1) -> this.initStat());
        // 节点内容搜索
        this.dataSearch.addTextChangeListener((observable, oldValue, newValue) -> this.nodeData.setSearchText(newValue));
        // 格式监听
        this.dataFormat.selectedItemChanged((t1, t2, t3) -> {
            if (this.dataFormat.isStringFormat()) {
                this.showData(RichDataType.STRING);
                this.nodeData.setEditable(true);
            } else if (this.dataFormat.isJsonFormat()) {
                this.showData(RichDataType.JSON);
                this.nodeData.setEditable(true);
            } else if (this.dataFormat.isBinaryFormat()) {
                this.showData(RichDataType.BINARY);
                this.nodeData.setEditable(false);
            } else if (this.dataFormat.isHexFormat()) {
                this.showData(RichDataType.HEX);
                this.nodeData.setEditable(false);
            } else if (this.dataFormat.isRawFormat()) {
                this.showData(RichDataType.RAW);
            }
        });
        // 节点内容变更
        this.nodeData.addTextChangeListener((observable, oldValue, newValue) -> {
            if (this.activeItem != null) {
                byte[] bytes = newValue == null ? new byte[]{} : newValue.getBytes(this.charset.getCharset());
                this.activeItem.nodeData(bytes);
                this.flushTabGraphicColor();
            }
        });
        // 拖动改变redis树大小处理
        this.resizeHelper = new ResizeHelper(this.leftBox, Cursor.DEFAULT, this::resizeLeft);
        this.resizeHelper.widthLimit(240, 750);
        this.resizeHelper.initResizeEvent();
    }

    /**
     * 左侧组件重新布局
     *
     * @param newWidth 新宽度
     */
    private void resizeLeft(Double newWidth) {
        if (newWidth != null && !Double.isNaN(newWidth)) {
            // 设置组件宽
            this.leftBox.setRealWidth(newWidth);
            this.tabPane.setLayoutX(newWidth);
            this.tabPane.setFlexWidth("100% - " + newWidth);
            this.leftBox.parentAutosize();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        // 收藏处理
        this.collect.managedProperty().bind(this.collect.visibleProperty());
        this.unCollect.managedProperty().bind(this.unCollect.visibleProperty());
        // acl处理
        this.aclId.setCellValueFactory(new PropertyValueFactory<>("idControl"));
        this.aclPerms.setCellValueFactory(new PropertyValueFactory<>("permsControl"));
        this.aclSchema.setCellValueFactory(new PropertyValueFactory<>("schemaControl"));
        this.aclStatus.setCellValueFactory(new PropertyValueFactory<>("statusControl"));
        // 设置cell工厂
        Callback<TableColumn<String, String>, TableCell<String, String>> cellFactory = param -> TableViewUtil.lineHeightCell(16);
        this.aclId.setCellFactory(cellFactory);
        this.aclPerms.setCellFactory(cellFactory);
        this.aclSchema.setCellFactory(cellFactory);
        this.aclStatus.setCellFactory(cellFactory);
    }

    /**
     * 当前窗口
     *
     * @return 窗口
     */
    protected Window window() {
        return this.treeItem == null ? null : this.treeItem.window();
    }

    /**
     * 保存配额
     */
    @FXML
    private void saveQuota() {
        try {
            this.activeItem.saveQuota(this.quotaBytes.getValue(), this.quotaNum.getValue().intValue());
            MessageBox.info(I18nHelper.operationSuccess());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 清除子节点数量配额
     */
    @FXML
    private void clearQuotaNum() {
        try {
            this.activeItem.clearQuotaNum();
            this.quotaNum.setValue(-1);
            MessageBox.info(I18nHelper.operationSuccess());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 清除数据大小配额
     */
    @FXML
    private void clearQuotaBytes() {
        try {
            this.activeItem.clearQuotaBytes();
            this.quotaBytes.setValue(-1);
            MessageBox.info(I18nHelper.operationSuccess());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        // 销毁节点
        this.treeView.destroy();
        super.onTabClose(tab, event);
    }

    /**
     * 恢复数据
     *
     * @param data 数据
     */
    public void restoreData(byte[] data) {
        // 保存数据历史
        this.activeItem.nodeData(data);
        this.showData();
    }

    @FXML
    private void doSearch() {
        String kw = this.searchKW.getTextTrim();
        int mode = this.searchKW.getSelectedIndex();
        int type = this.searchType.getSelectedIndex();
        this.treeView.itemFilter().setKw(kw);
        this.treeView.itemFilter().setType(type);
        this.treeView.itemFilter().setMatchMode(mode);
        this.treeView.filter();
    }

    /**
     * 对子节点排序，正序
     */
    @FXML
    private void sortAsc() {
        this.sortAsc.disappear();
        this.sortDesc.display();
        this.treeView.sortAsc();
    }

    /**
     * 对子节点排序，倒序
     */
    @FXML
    private void sortDesc() {
        this.sortDesc.disappear();
        this.sortAsc.display();
        this.treeView.sortDesc();
    }

    /**
     * 定位节点
     */
    @FXML
    private void positionNode() {
        this.treeView.scrollTo(this.treeView.getSelectedItem());
    }
}
