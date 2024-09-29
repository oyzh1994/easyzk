package cn.oyzh.easyzk.tabs.node;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.controller.acl.ZKACLAddController;
import cn.oyzh.easyzk.controller.acl.ZKACLUpdateController;
import cn.oyzh.easyzk.controller.node.ZKNodeQRCodeController;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.fx.ZKACLVBox;
import cn.oyzh.easyzk.fx.ZKFormatComboBox;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.fx.common.dto.FriendlyInfo;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.combo.CharsetComboBox;
import cn.oyzh.fx.plus.controls.page.PageBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.fx.plus.thread.RenderService;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * zk节点tab内容组件
 *
 * @author oyzh
 * @since 2023/05/21
 */
// @Lazy
// @Component
// @Scope(ScopeType.PROTOTYPE)
public class ZKNodeTabContent extends DynamicTabController {

    /**
     * 根节点
     */
    @FXML
    private FlexTabPane root;

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
     * zk树节点
     */
    protected ZKNodeTreeItem treeItem;


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
    // private ZKDataTextArea nodeData;

    /**
     * 格式
     */
    @FXML
    protected ZKFormatComboBox format;

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
     * 文本监听器
     */
    private final ChangeListener<String> textListener = (observable, oldValue, newValue) -> {
        if (this.treeItem != null) {
            this.treeItem.data(newValue);
        }
    };

    /**
     * 数据监听器
     */
    private final ChangeListener<byte[]> dataListener = (observable, oldValue, newValue) -> {
        if (newValue == null) {
            this.saveNodeData.disable();
        } else {
            this.saveNodeData.enable();
        }
    };

    /**
     * 状态监听器
     */
    private final ChangeListener<Stat> statListener = (observable, oldValue, newValue) -> this.initStat();

    /**
     * 权限监听器
     */
    private final ChangeListener<List<ZKACL>> aclListener = (observable, oldValue, newValue) -> this.initAcl();

    /**
     * 配额监听器
     */
    private final ChangeListener<StatsTrack> quotaListener = (observable, oldValue, newValue) -> this.initQuota();

    /**
     * 格式监听器
     */
    private final ChangeListener<String> formatListener = (t1, t2, t3) -> {
        if (this.format.isStringFormat()) {
            this.showData(RichDataType.STRING);
            this.nodeData.setEditable(true);
        } else if (this.format.isJsonFormat()) {
            this.showData(RichDataType.JSON);
            this.nodeData.setEditable(true);
        } else if (this.format.isBinaryFormat()) {
            this.showData(RichDataType.BINARY);
            this.nodeData.setEditable(false);
        } else if (this.format.isHexFormat()) {
            this.showData(RichDataType.HEX);
            this.nodeData.setEditable(false);
        } else if (this.format.isRawFormat()) {
            this.showData(RichDataType.RAW);
        }
    };

    /**
     * 初始化
     *
     * @param item 树节点
     */
    public void init(ZKNodeTreeItem item) {
        // 移除旧数据监听器
        if (this.treeItem != null) {
            this.treeItem.clearListener();
        }
        // 初始化数据
        this.treeItem = item;
        // 初始化数据
        if (this.root.getSelectedIndex() == 0) {
            this.showData();
            // 首次设置数据要清除历史
            this.nodeData.forgetHistory();
        } else if (this.root.getSelectedIndex() == 1) {
            this.treeItem = item;
            this.initStat();
        } else if (this.root.getSelectedIndex() == 2) {
            this.treeItem = item;
            this.initAcl();
        } else if (this.root.getSelectedIndex() == 3) {
            this.treeItem = item;
            this.initQuota();
        }
        // 设置监听器
        this.treeItem.addAclListener(this.aclListener);
        this.treeItem.addDataListener(this.dataListener);
        this.treeItem.addStatListener(this.statListener);
        this.treeItem.addQuotaListener(this.quotaListener);

        // 按钮状态处理
        this.saveNodeData.disable();
        this.node2QRCode.setDisable(!this.treeItem.hasReadPerm());

        // 收藏处理
        this.collect.setVisible(!this.treeItem.isCollect());
        this.unCollect.setVisible(this.treeItem.isCollect());

        // 字符集处理
        this.charset.select(this.treeItem.getCharset());

        // 加载耗时处理
        FXUtil.runWait(() -> this.loadTime.setText(I18nHelper.cost() + ":" + this.treeItem.loadTime() + "ms"));
    }

    /**
     * 获取节点数据组件
     *
     * @return 节点数据组件
     */
    public RichDataTextAreaPane getDataNode() {
        return this.nodeData;
    }

    /**
     * 选中数据tab
     */
    public void selectDataTab() {
        this.root.select(0);
    }

    /**
     * 重新载入
     */
    public void reload() {
        if (this.root.getSelectedIndex() == 0) {
            this.reloadData();
        } else if (this.root.getSelectedIndex() == 1) {
            this.reloadStat();
        } else if (this.root.getSelectedIndex() == 2) {
            this.reloadACL();
        } else if (this.root.getSelectedIndex() == 3) {
            this.reloadQuota();
        }
    }

    /**
     * 重新载入权限
     */
    @FXML
    public void reloadACL() {
        try {
            this.treeItem.refreshACL();
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
        fxView.setProp("zkItem", this.treeItem);
        fxView.setProp("zkClient", this.treeItem.client());
        fxView.display();
    }

    /**
     * 重新载入配额
     */
    @FXML
    private void reloadQuota() {
        try {
            this.treeItem.reloadQuota();
        } catch (KeeperException.NoNodeException ignore) {
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 复制权限
     */
    @FXML
    private void copyACL(MouseEvent event) {
        try {
            SVGGlyph glyph = (SVGGlyph) event.getTarget();
            ZKACLVBox aclVBox = (ZKACLVBox) glyph.getParent().getParent();
            ZKACL acl = aclVBox.acl();
            String builder = acl.idFriend().getName(this.aclViewSwitch.isSelected()) + " " + acl.idFriend().getValue(this.aclViewSwitch.isSelected()) + System.lineSeparator() +
                    acl.schemeFriend().getName(this.aclViewSwitch.isSelected()) + " " + acl.schemeFriend().getValue(this.aclViewSwitch.isSelected()) + System.lineSeparator() +
                    acl.permsFriend().getName(this.aclViewSwitch.isSelected()) + " " + acl.permsFriend().getValue(this.aclViewSwitch.isSelected());
            ClipboardUtil.setStringAndTip(builder, "权限信息");
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nHelper.operationException());
        }
    }

    /**
     * 修改权限
     */
    @FXML
    private void updateACL(MouseEvent event) {
        try {
            SVGGlyph glyph = (SVGGlyph) event.getTarget();
            ZKACLVBox aclVBox = (ZKACLVBox) glyph.getParent().getParent();
            ZKACL acl = aclVBox.acl();
            StageAdapter fxView = StageManager.parseStage(ZKACLUpdateController.class, this.window());
            fxView.setProp("acl", acl);
            fxView.setProp("zkItem", this.treeItem);
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
        if (!MessageBox.confirm(I18nHelper.deleteData())) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) event.getTarget();
        ZKACLVBox aclVBox = (ZKACLVBox) glyph.getParent().getParent();
        ZKACL acl = aclVBox.acl();
        if (this.treeItem.acl().size() == 1) {
            MessageBox.warn(this.i18nString("zk.aclTip1"));
            return;
        }
        try {
            Stat stat = this.treeItem.deleteACL(acl);
            if (stat != null) {
                this.reloadACL();
                MessageBox.okToast(I18nHelper.operationSuccess());
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
    private void prevPage() {
        if (this.aclPaging != null) {
            this.renderNode(this.aclPaging.prevPage());
        }
    }

    /**
     * 权限列表，下一页
     */
    @FXML
    private void nextPage() {
        if (this.aclPaging != null) {
            this.renderNode(this.aclPaging.nextPage());
        }
    }

    /**
     * 渲染权限控件
     *
     * @param pageNo 页码
     */
    private void renderNode(long pageNo) {
        // 获取子节点
        List<Node> nodes = this.aclBox.getChildren();
        // 隐藏子节点
        nodes.forEach(n -> n.setVisible(false));
        // 获取zk权限分页数据
        List<ZKACL> aclList = this.aclPaging.page(pageNo);
        // 设置分页信息
        this.aclPage.setPaging(this.aclPaging);
        // 处理zk权限分页数据
        for (int i = 0; i < aclList.size(); i++) {
            ZKACL acl = aclList.get(i);
            ZKACLVBox vBox = (ZKACLVBox) nodes.get(i);
            // 获取控件，设置acl信息，并显示
            vBox.acl(acl);
            vBox.setVisible(true);
            // 查找组件
            HBox idBox = (HBox) vBox.lookup(".acl-id");
            HBox permsBox = (HBox) vBox.lookup(".acl-perms");
            HBox schemeBox = (HBox) vBox.lookup(".acl-scheme");
            Text statusText = (Text) vBox.lookup(".acl-status");
            // 执行渲染
            BackgroundService.submitFX(() -> {
                this.handleACLState(acl, statusText);
                this.handleACLInfo(acl.idFriend(), idBox);
                this.handleACLInfo(acl.permsFriend(), permsBox);
                this.handleACLInfo(acl.schemeFriend(), schemeBox);
            });
        }
    }

    /**
     * 处理权限状态
     *
     * @param acl  权限
     * @param text 组件
     */
    private void handleACLState(ZKACL acl, Text text) {
        Set<String> digests = ZKAuthUtil.getAuthedDigest(this.treeItem.client());
        if (CollUtil.isNotEmpty(digests) && digests.contains(acl.idVal())) {
            text.setText("(" + I18nHelper.authed() + ")");
        } else {
            text.setText("");
        }
    }

    /**
     * 处理权限属性
     *
     * @param info 属性
     * @param box  组件
     */
    private void handleACLInfo(FriendlyInfo<ACL> info, HBox box) {
        // 获取标题和文本框
        Label label = (Label) box.getChildren().get(0);
        Label data = (Label) box.getChildren().get(1);
        // 设置属性值及属性值
        label.setText(info.getName(this.aclViewSwitch.isSelected()));
        data.setText(info.getValue(this.aclViewSwitch.isSelected()).toString());
    }

    /**
     * 复制zk状态
     */
    @FXML
    private void copyStat() {
        List<FriendlyInfo<Stat>> statInfos = this.treeItem.statInfos();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < statInfos.size(); i++) {
            FriendlyInfo<Stat> statInfo = statInfos.get(i);
            builder.append(statInfo.getName(this.statViewSwitch.isSelected()))
                    .append(" : ")
                    .append(statInfo.getValue(this.statViewSwitch.isSelected()));
            if (statInfo != CollUtil.getLast(statInfos)) {
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
            this.treeItem.refreshStat();
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
        String data = this.treeItem.decodeNodePath() + " " + this.nodeData.getTextTrim();
        ClipboardUtil.setStringAndTip(data, "节点");
    }

    /**
     * 复制节点路径
     */
    @FXML
    private void copyNodePath() {
        ClipboardUtil.setStringAndTip(this.treeItem.decodeNodePath(), "节点路径");
    }

    /**
     * 刷新zk节点数据
     */
    @FXML
    private void reloadData() {
        // 放弃保存
        if (this.treeItem.dataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            return;
        }
        // 刷新数据
        try {
            this.treeItem.refreshData();
            // 数据变更
            this.showData();
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
        this.treeItem.collect();
        this.collect.disappear();
        this.unCollect.display();
    }

    /**
     * 取消收藏节点
     */
    @FXML
    private void unCollect() {
        this.treeItem.unCollect();
        this.collect.display();
        this.unCollect.disappear();
    }

    /**
     * 保存节点数据
     */
    @FXML
    private void saveNodeData() {
        if (this.treeItem.isDataTooLong()) {
            MessageBox.warn(I18nHelper.dataTooLarge());
            return;
        }
        // 保存数据
        if (this.treeItem.dataUnsaved()) {
            // 保存数据历史
            this.treeItem.saveDataHistory();
            RenderService.submit(this.treeItem::saveData);
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
            fxView.setProp("zkNode", this.treeItem.value());
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
        this.treeItem.authNode();
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
        ZKEventUtil.historyShow(this.treeItem);
    }

    /**
     * 显示数据
     */
    protected void showData() {
        this.nodeData.showData(this.treeItem.data());
    }

    /**
     * 显示数据
     *
     * @param dataType 数据类型
     */
    protected void showData(RichDataType dataType) {
        this.nodeData.showData(dataType, this.treeItem.data());
    }

    /**
     * 初始化状态
     */
    public void initStat() {
        if (this.treeItem == null) {
            return;
        }
        List<FriendlyInfo<Stat>> statInfos = this.treeItem.statInfos();
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
        if (this.treeItem.aclEmpty()) {
            this.aclViewSwitch.disable();
        } else {
            this.aclViewSwitch.disable();
            List<ZKACL> aclList = this.treeItem.acl();
            // 获取分页控件
            this.aclPaging = new Paging<>(aclList, 5);
            // 渲染首页收据
            this.renderNode(0);
            this.aclViewSwitch.enable();
        }
    }

    /**
     * 初始化配额
     */
    public void initQuota() {
        if (this.treeItem == null) {
            return;
        }
        this.quotaTab.getContent().setDisable(this.treeItem.value().rootNode());
        StatsTrack quota = this.treeItem.quota();
        if (quota != null) {
            this.quotaNum.setValue(quota.getCount());
            this.quotaBytes.setValue(quota.getBytes());
        } else {
            this.quotaNum.setValue(-1);
            this.quotaBytes.setValue(-1);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        // 收藏处理
        this.collect.managedProperty().bind(this.collect.visibleProperty());
        this.unCollect.managedProperty().bind(this.unCollect.visibleProperty());
        // 切换tab
        this.root.selectedIndexChanged((t3, t2, newValue) -> {
            if (newValue.intValue() == 0) {
                this.showData();
            } else if (newValue.intValue() == 1) {
                this.initStat();
            } else if (newValue.intValue() == 2) {
                this.initAcl();
            } else if (newValue.intValue() == 3) {
                this.initQuota();
            }
        });
        // 格式监听
        this.format.selectedItemChanged(this.formatListener);
        // 数据监听
        this.nodeData.addTextChangeListener(this.textListener);
        // undo监听
        this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
        // redo监听
        this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
        // 切换显示监听
        this.aclViewSwitch.selectedChanged((t3, t2, t1) -> this.initAcl());
        // 切换显示监听
        this.statViewSwitch.selectedChanged((t3, t2, t1) -> this.initStat());
        // 字符集选择事件
        this.charset.selectedItemChanged((t3, t2, t1) -> {
            this.treeItem.setCharset(this.charset.getCharset());
            this.showData();
        });
        // 节点内容搜索
        this.dataSearch.addTextChangeListener((observable, oldValue, newValue) -> this.nodeData.setSearchText(newValue));
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
            this.treeItem.saveQuota(this.quotaBytes.getValue(), this.quotaNum.getValue().intValue());
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
            this.treeItem.clearQuotaNum();
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
            this.treeItem.clearQuotaBytes();
            this.quotaBytes.setValue(-1);
            MessageBox.info(I18nHelper.operationSuccess());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        // 取消当前节点的选中
        if (this.treeItem != null && this.treeItem.getTreeView().getSelectedItem() == this.treeItem) {
            this.treeItem.getTreeView().select(this.treeItem.root());
        }
        super.onTabClose(tab, event);
    }

    /**
     * 恢复数据
     *
     * @param data 数据
     */
    public void restoreData(byte[] data) {
        // 保存数据历史
        this.treeItem.data(data);
        this.showData();
    }
}
