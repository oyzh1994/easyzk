package cn.oyzh.easyzk.trees;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.controller.info.ZKInfoAddController;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKInfoExport;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.msg.ZKInfoAddedMsg;
import cn.oyzh.easyzk.event.msg.ZKInfoUpdatedMsg;
import cn.oyzh.easyzk.store.ZKGroupStore;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.util.FXFileChooser;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * zk树根节点
 *
 * @author oyzh
 * @since 2023/1/29
 */
//@Slf4j
public class ZKRootTreeItem extends ZKTreeItem<ZKRootTreeItemValue> implements ZKConnectManager {

    /**
     * zk信息储存
     */
    private final ZKInfoStore infoStore = ZKInfoStore.INSTANCE;

    /**
     * zk分组储存
     */
    private final ZKGroupStore groupStore = ZKGroupStore.INSTANCE;

    public ZKRootTreeItem(@NonNull ZKTreeView treeView) {
        super(treeView);
        this.setValue(new ZKRootTreeItemValue());
        // 注册事件处理
        EventUtil.register(this);
        // 初始化子节点
        this.initChildes();
        // 监听节点变化
//        this.getChildren().addListener((ListChangeListener<? super ZKTreeItem>) c -> {
//            ZKEventUtil.treeChildChanged();
//            this.getTreeView().flushLocal();
//        });
        // 监听节点变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            ZKEventUtil.treeChildChanged();
//            this.flushLocal();
        });
    }

    /**
     * 初始化子节点
     */
    private void initChildes() {
        // 初始化分组
        List<ZKGroup> groups = this.groupStore.load();
        if (CollUtil.isNotEmpty(groups)) {
            List<TreeItem<?>> list = new ArrayList<>();
            for (ZKGroup group : groups) {
                list.add(new ZKGroupTreeItem(group, this.getTreeView()));
            }
            this.addChild(list);
        }
        // 初始化连接
        List<ZKInfo> zkInfos = this.infoStore.load();
        if (CollUtil.isNotEmpty(groups)) {
            this.addConnects(zkInfos);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem addConnect = MenuItemExt.newItem("添加连接", new SVGGlyph("/font/add.svg", "12"), "添加zk连接", this::addConnect);
        MenuItem exportConnect = MenuItemExt.newItem("导出连接", new SVGGlyph("/font/export.svg", "12"), "导出zk连接", this::exportConnect);
        MenuItem importConnect = MenuItemExt.newItem("导入连接", new SVGGlyph("/font/Import.svg", "12"), "选择文件，导入zk连接，也可拖拽文件到窗口进行导入", this::importConnect);
        MenuItem addGroup = MenuItemExt.newItem("添加分组", new SVGGlyph("/font/addGroup.svg", "12"), "添加分组", this::addGroup);

        exportConnect.setDisable(this.isChildEmpty());

        items.add(addConnect);
        items.add(exportConnect);
        items.add(importConnect);
        items.add(addGroup);
        return items;
    }

    /**
     * 导出连接
     */
    private void exportConnect() {
        List<ZKInfo> zkInfos = this.infoStore.load();
        if (zkInfos.isEmpty()) {
            MessageBox.warn("zk连接列表为空！");
            return;
        }
        ZKInfoExport export = ZKInfoExport.fromConnects(zkInfos);
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JSON files", "*.json");
        File file = FXFileChooser.save("保存zk连接列表", "zk连接列表.json", new FileChooser.ExtensionFilter[]{extensionFilter});
        try {
            FileUtil.writeUtf8String(export.toJSONString(), file);
            MessageBox.okToast("保存zk连接列表成功！");
        } catch (Exception ex) {
            MessageBox.warn("保存zk连接列表失败！");
        }
    }

    /**
     * 拖拽文件
     *
     * @param files 文件
     */
    public void dragFile(List<File> files) {
        if (CollUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn("仅支持单个文件！");
            return;
        }
        File file = files.get(0);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 导入连接
     */
    private void importConnect() {
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("JSON files", "*.json");
        FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("All", "*.*");
        File file = FXFileChooser.choose("选择zk连接列表", new FileChooser.ExtensionFilter[]{filter1, filter2});
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 解析连接文件
     *
     * @param file 文件
     */
    private void parseConnect(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            MessageBox.warn("文件不存在！");
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn("不支持文件夹！");
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "json")) {
            MessageBox.warn("仅支持json文件！");
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn("文件内容为空！");
            return;
        }
        try {
            String text = FileUtil.readUtf8String(file);
            ZKInfoExport export = ZKInfoExport.fromJSON(text);
            List<ZKInfo> zkInfos = export.getConnects();
            if (CollUtil.isNotEmpty(zkInfos)) {
                for (ZKInfo zkInfo : zkInfos) {
                    // if (this.infoStore.exist(zkInfo)) {
                    //     MessageBox.warn("连接[" + zkInfo.getName() + "]已存在");
                    // } else
                    if (this.infoStore.add(zkInfo)) {
                        this.addConnect(zkInfo);
                    } else {
                        MessageBox.warn("连接[" + zkInfo.getName() + "]导入失败");
                    }
                }
                MessageBox.okToast("导入zk连接列表成功！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, "解析zk连接列表失败！");
        }
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageUtil.showStage(ZKInfoAddController.class, this.window());
    }

    /**
     * 添加分组
     */
    @EventReceiver(ZKEventTypes.ZK_ADD_GROUP)
    private void addGroup() {
        String groupName = MessageBox.prompt("请输入分组名称");

        // 名称为null，则忽略
        if (groupName == null) {
            return;
        }

        // 不能为空
        if (StrUtil.isBlank(groupName)) {
            MessageBox.warn("名称不能为空！");
            return;
        }

        ZKGroup group = new ZKGroup();
        group.setName(groupName);
        if (this.groupStore.exist(group)) {
            MessageBox.warn("此分组已存在！");
            return;
        }
        group = this.groupStore.add(groupName);
        if (group != null) {
            this.addChild(new ZKGroupTreeItem(group, this.getTreeView()));
        } else {
            MessageBox.warn("添加分组失败！");
        }
    }

    /**
     * 获取分组节点
     *
     * @param groupId 分组id
     */
    private ZKGroupTreeItem getGroupItem(String groupId) {
        if (StrUtil.isNotBlank(groupId)) {
            List<ZKGroupTreeItem> items = this.getGroupItems();
            Optional<ZKGroupTreeItem> groupTreeItem = items.parallelStream().filter(g -> Objects.equals(g.value().getGid(), groupId)).findAny();
            return groupTreeItem.orElse(null);
        }
        return null;
    }

    /**
     * 获取分组节点
     *
     * @return 分组节点
     */
    private List<ZKGroupTreeItem> getGroupItems() {
        List<ZKGroupTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.getRealChildren()) {
            if (item instanceof ZKGroupTreeItem groupTreeItem) {
                items.add(groupTreeItem);
            }
        }
        return items;
    }

    /**
     * 连接新增事件
     *
     * @param msg 消息
     */
    @EventReceiver(ZKEventTypes.ZK_INFO_ADDED)
    private void onInfoAdded(ZKInfoAddedMsg msg) {
        this.addConnect(msg.info());
    }

    /**
     * 连接变更事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = ZKEventTypes.ZK_INFO_UPDATED, async = true, verbose = true)
    private void onInfoUpdated(ZKInfoUpdatedMsg msg) {
        ZKInfo info = msg.info();
        f1:
        for (TreeItem<?> item : this.getRealChildren()) {
            if (item instanceof ZKConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == info) {
                    connectTreeItem.value(info);
                    break;
                }
            } else if (item instanceof ZKGroupTreeItem groupTreeItem) {
                for (ZKConnectTreeItem connectTreeItem : groupTreeItem.getConnectItems()) {
                    if (connectTreeItem.value() == info) {
                        connectTreeItem.value(info);
                        break f1;
                    }
                }
            }
        }
    }

    @Override
    public void addConnect(@NonNull ZKInfo zkInfo) {
        ZKGroupTreeItem groupItem = this.getGroupItem(zkInfo.getGroupId());
        if (groupItem == null) {
            super.addChild(new ZKConnectTreeItem(zkInfo, this.getTreeView()));
            this.extend();
        } else {
            groupItem.addConnect(zkInfo);
            groupItem.extend();
        }
    }

    @Override
    public void addConnectItem(@NonNull ZKConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (item.value().getGroupId() != null) {
                item.value().setGroupId(null);
                this.infoStore.update(item.value());
            }
            super.addChild(item);
            this.extend();
        }
    }

    @Override
    public void addConnectItems(@NonNull List<ZKConnectTreeItem> items) {
        if (CollUtil.isNotEmpty(items)) {
            this.addChild((List) items);
            this.extend();
        }
    }

    @Override
    public boolean delConnectItem(@NonNull ZKConnectTreeItem item) {
        // 删除连接
        if (this.infoStore.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<ZKConnectTreeItem> getConnectItems() {
        List<ZKConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> child : this.getRealChildren()) {
            if (child instanceof ZKConnectTreeItem connectTreeItem) {
                items.add(connectTreeItem);
            } else if (child instanceof ZKGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectItems());
            }
        }
        return items;
    }

    @Override
    public List<ZKConnectTreeItem> getConnectedItems() {
        List<ZKConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.getRealChildren()) {
            if (item instanceof ZKConnectTreeItem connectTreeItem) {
                if (connectTreeItem.isConnected()) {
                    items.add(connectTreeItem);
                }
            } else if (item instanceof ZKGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectedItems());
            }
        }
        return items;
    }

    /**
     * 添加连接
     */
    @EventReceiver(ZKEventTypes.ZK_ADD_CONNECT)
    private void onAddConnect() {
        StageUtil.showStage(ZKInfoAddController.class, this.window());
    }

    @Override
    public boolean allowDrop() {
        return true;
    }

    @Override
    public boolean allowDropNode(DragNodeItem item) {
        return item instanceof ZKConnectTreeItem;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof ZKConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }
}
