package cn.oyzh.easyzk.tabs.node;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.controller.acl.ZKACLAddController;
import cn.oyzh.easyzk.controller.acl.ZKACLUpdateController;
import cn.oyzh.easyzk.controller.node.ZKNodeQRCodeController;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.fx.ZKACLVBox;
import cn.oyzh.easyzk.fx.ZKFormatComboBox;
import cn.oyzh.easyzk.fx.ZKNodeTreeItem;
import cn.oyzh.easyzk.fx.ZKRichDataTextArea;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.fx.common.dto.FriendlyInfo;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.CharsetComboBox;
import cn.oyzh.fx.plus.controls.FXLabel;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.FlexTabPane;
import cn.oyzh.fx.plus.controls.FlexTextArea;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.controls.PagePane;
import cn.oyzh.fx.plus.controls.ToggleSwitch;
import cn.oyzh.fx.plus.ext.NumberTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.rich.FlexRichTextArea;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
@Lazy
@Slf4j
@Component
public class ZKNodeTabContentController implements Initializable {

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
    private ToggleSwitch statViewSwitch;

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
    private PagePane<ZKACL> aclPage;

    /**
     * 右侧zk权限视图切换按钮
     */
    @FXML
    private CharsetComboBox charset;

    /**
     * 右侧zk权限视图切换按钮
     */
    @FXML
    private ToggleSwitch aclViewSwitch;

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
     * 添加节点
     */
    @FXML
    private SVGGlyph addNode;

    /**
     * 右侧zk节点路径
     */
    @FXML
    private FXLabel nodePath;

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
    private ZKRichDataTextArea nodeData;
    // private ZKDataTextArea nodeData;

    /**
     * 格式
     */
    @FXML
    protected ZKFormatComboBox format;

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
     * 忽略变化
     */
    private boolean ignoreChanged;

    /**
     * 文本监听器
     */
    private final ChangeListener<String> textListener = (observable, oldValue, newValue) -> {
        if (!this.ignoreChanged) {
            this.treeItem.data(newValue);
        }
    };

    /**
     * 数据监听器
     */
    private final ChangeListener<byte[]> dataListener = (observable, oldValue, newValue) -> {
        if (!this.ignoreChanged) {
            if (newValue == null) {
                this.saveNodeData.disable();
            } else {
                this.saveNodeData.enable();
            }
        }
    };

    /**
     * 状态监听器
     */
    private final ChangeListener<Stat> statListener = (observable, oldValue, newValue) -> {
        if (!this.ignoreChanged) {
            this.initStat();
        }
    };

    /**
     * 权限监听器
     */
    private final ChangeListener<List<ZKACL>> aclListener = (observable, oldValue, newValue) -> {
        if (!this.ignoreChanged) {
            this.initAcl();
        }
    };


    /**
     * 配额监听器
     */
    private final ChangeListener<StatsTrack> quotaListener = (observable, oldValue, newValue) -> {
        if (!this.ignoreChanged) {
            this.initQuota();
        }
    };

    /**
     * 格式监听器
     */
    private final ChangeListener<String> formatListener = (observable, oldValue, newValue) -> {
        this.ignoreChanged = true;
        if (this.format.isStringFormat()) {
            this.showData((byte) 0);
            this.nodeData.setEditable(true);
            this.nodeData.setEditable(false);
        } else if (this.format.isJsonFormat()) {
            this.showData((byte) 1);
            this.nodeData.setEditable(true);
        } else if (this.format.isBinaryFormat()) {
            this.showData((byte) 2);
            this.nodeData.setEditable(false);
        } else if (this.format.isHexFormat()) {
            this.showData((byte) 3);
            this.nodeData.setEditable(false);
        } else if (this.format.isRawFormat()) {
            this.showData((byte) 4);
        }
    };

    /**
     * 初始化
     *
     * @param treeItem 树节点
     */
    public void init(ZKNodeTreeItem treeItem) {

        // 移除旧数据监听器
        if (this.treeItem != null) {
            this.treeItem.aclProperty().removeListener(this.aclListener);
            this.treeItem.dataProperty().removeListener(this.dataListener);
            this.treeItem.statProperty().removeListener(this.statListener);
            this.treeItem.quotaProperty().removeListener(this.quotaListener);
        }

        // 初始化文本、格式监听器
        this.format.selectedItemChanged(this.formatListener);
        this.nodeData.addTextChangeListener(this.textListener);

        // 初始化数据
        this.treeItem = treeItem;

        // 设置监听器
        this.treeItem.aclProperty().addListener(this.aclListener);
        this.treeItem.dataProperty().addListener(this.dataListener);
        this.treeItem.statProperty().addListener(this.statListener);
        this.treeItem.quotaProperty().addListener(this.quotaListener);

        // 按钮状态处理
        this.addNode.setDisable(this.treeItem.ephemeral());
        this.node2QRCode.setDisable(!this.treeItem.hasReadPerm());
        this.saveNodeData.setDisable(!this.treeItem.dataUnsaved());

        // 收藏处理
        this.collect.setVisible(!this.treeItem.isCollect());
        this.unCollect.setVisible(this.treeItem.isCollect());

        // 字符集处理
        this.charset.select(this.treeItem.getCharset());

        // 节点路径处理
        FXUtil.runWait(() -> this.nodePath.setText(this.treeItem.decodeNodePath()));

        // 初始化数据
        if (this.root.getSelectedIndex() == 0) {
            this.initData();
        } else if (this.root.getSelectedIndex() == 1) {
            this.initStat();
        } else if (this.root.getSelectedIndex() == 2) {
            this.initAcl();
        } else if (this.root.getSelectedIndex() == 3) {
            this.initQuota();
        }
    }

    /**
     * 获取节点数据组件
     *
     * @return 节点数据组件
     */
    public FlexRichTextArea getDataNode() {
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
        StageWrapper fxView = StageUtil.parseStage(ZKACLAddController.class, this.window());
        fxView.setProp("zkItem", this.treeItem);
        fxView.setProp("zkClient", this.treeItem.zkClient());
        fxView.display();
    }

    /**
     * 重新载入配额
     */
    @FXML
    public void reloadQuota() {
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
            StringBuilder builder = new StringBuilder();
            builder.append(acl.idFriend().getName(this.aclViewSwitch.isSelected()))
                    .append(" : ")
                    .append(acl.idFriend().getValue(this.aclViewSwitch.isSelected()))
                    .append(System.lineSeparator())
                    .append(acl.schemeFriend().getName(this.aclViewSwitch.isSelected()))
                    .append(" : ")
                    .append(acl.schemeFriend().getValue(this.aclViewSwitch.isSelected()))
                    .append(System.lineSeparator())
                    .append(acl.permsFriend().getName(this.aclViewSwitch.isSelected()))
                    .append(" : ")
                    .append(acl.permsFriend().getValue(this.aclViewSwitch.isSelected()));
            if (FXUtil.clipboardCopy(builder.toString())) {
                MessageBox.okToast("已复制权限信息到粘贴板");
            } else {
                MessageBox.warn("复制权限信息失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, "复制权限信息异常");
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
            StageWrapper fxView = StageUtil.parseStage(ZKACLUpdateController.class, this.window());
            fxView.setProp("acl", acl);
            fxView.setProp("zkItem", this.treeItem);
            fxView.setProp("zkClient", this.treeItem.zkClient());
            fxView.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, "操作出现异常");
        }
    }

    /**
     * 删除权限
     */
    @FXML
    private void deleteACL(MouseEvent event) {
        if (!MessageBox.confirm("确定删除此权限控制？")) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) event.getTarget();
        ZKACLVBox aclVBox = (ZKACLVBox) glyph.getParent().getParent();
        ZKACL acl = aclVBox.acl();
        if (this.treeItem.acl().size() == 1) {
            MessageBox.warn("请最少保留一个权限控制！");
            return;
        }
        try {
            Stat stat = this.treeItem.deleteACL(acl);
            if (stat != null) {
                this.reloadACL();
                MessageBox.okToast("删除权限控制成功！");
            } else {
                MessageBox.warn("删除权限控制失败！");
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
    private void renderNode(int pageNo) {
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
            FXUtil.runLater(() -> {
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
        Set<String> digests = ZKAuthUtil.getAuthedDigest(this.treeItem.zkClient());
        if (CollUtil.isNotEmpty(digests) && digests.contains(acl.idVal())) {
            text.setText("(已认证)");
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
        if (FXUtil.clipboardCopy(builder.toString())) {
            MessageBox.okToast("已复制节点状态到剪贴板");
        } else {
            MessageBox.warn("复制节点状态到剪贴板失败！");
        }
    }

    /**
     * 刷新zk状态
     */
    @FXML
    public void reloadStat() {
        try {
            this.treeItem.refreshStat();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 添加子节点
     */
    @FXML
    private void addNode() {
        this.treeItem.addNode();
    }

    /**
     * 删除zk节点
     */
    @FXML
    private void deleteNode() {
        this.treeItem.delete();
    }

    /**
     * 复制节点路径及数据
     */
    @FXML
    private void copyNode() {
        String data = this.treeItem.decodeNodePath() + " " + this.nodeData.getTextTrim();
        if (FXUtil.clipboardCopy(data)) {
            MessageBox.okToast("已复制节点到粘贴板");
        } else {
            MessageBox.warn("复制节点到粘贴板失败");
        }
    }

    /**
     * 复制节点路径
     */
    @FXML
    private void copyNodePath() {
        if (FXUtil.clipboardCopy(this.treeItem.decodeNodePath())) {
            MessageBox.okToast("已复制节点路径到粘贴板");
        } else {
            MessageBox.warn("复制节点路径到粘贴板失败");
        }
    }

    /**
     * 刷新zk节点数据
     */
    @FXML
    public void reloadData() {
        // 放弃保存
        if (this.treeItem.dataUnsaved() && !MessageBox.confirm("放弃未保存的数据？")) {
            return;
        }
        // 刷新数据
        try {
            this.treeItem.refreshData();
            // 数据变更
            this.initData();
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
            MessageBox.warn("数据太大，无法保存！");
            return;
        }
        // 保存数据
        if (this.treeItem.dataUnsaved()) {
            ThreadUtil.startVirtual(() -> this.treeItem.saveData());
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
            StageWrapper fxView = StageUtil.parseStage(ZKNodeQRCodeController.class, this.window());
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
        KeyCode code = e.getCode();
        // 保存节点数据
        if (code == KeyCode.S && e.isControlDown()) {
            this.saveNodeData();
        }
    }

    /**
     * 初始化数据
     */
    public void initData() {
        this.showData(this.nodeData.getShowType());
    }

    /**
     * 显示数据
     *
     * @param showType 类型
     */
    protected void showData(byte showType) {
        // 标记为忽略变化
        this.ignoreChanged = true;
        this.nodeData.disable();
        this.nodeData.clear();
        this.nodeData.setPromptText("数据加载中...");
        this.nodeData.setShowType(showType);
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> this.nodeData.showData(this.treeItem.dataStr()))
                .onFinish(() -> {
                    this.nodeData.setPromptText("");
                    this.nodeData.enable();
                    this.ignoreChanged = false;
                })
                .onError(MessageBox::exception)
                .build();
        ExecutorUtil.start(task, 20);
    }

    /**
     * 初始化状态
     */
    public void initStat() {
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
    public void initialize(URL location, ResourceBundle resources) {
        // 收藏处理
        this.collect.managedProperty().bind(this.collect.visibleProperty());
        this.unCollect.managedProperty().bind(this.unCollect.visibleProperty());

        // 节点数据处理
        this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
        this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));

        // 切换tab
        this.root.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                this.initData();
            } else if (newValue.intValue() == 1) {
                this.initStat();
            } else if (newValue.intValue() == 2) {
                this.initAcl();
            } else if (newValue.intValue() == 3) {
                this.initQuota();
            }
        });

        // 切换显示监听
        this.aclViewSwitch.selectedChanged((abs, o, n) -> this.initAcl());
        // 切换显示监听
        this.statViewSwitch.selectedChanged((abs, o, n) -> this.initStat());
        // 字符集选择事件
        this.charset.selectedItemChanged((observable, oldValue, newValue) -> {
            this.treeItem.setCharset(this.charset.getCharset());
            this.initData();
        });
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
            this.treeItem.saveQuota(this.quotaBytes.getValue(), (int) this.quotaNum.getValue());
            MessageBox.info("配额已保存");
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
            MessageBox.info("节点数量配额已清除");
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
            MessageBox.info("数据大小配额已清除");
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }
}
