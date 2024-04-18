package cn.oyzh.easyzk.trees.root;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.controller.info.ZKInfoAddController;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKInfoExport;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKGroupStore;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.easyzk.trees.ZKConnectManager;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.group.ZKGroupTreeItem;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.AddConnectMenuItem;
import cn.oyzh.fx.plus.menu.AddGroupMenuItem;
import cn.oyzh.fx.plus.menu.ExportConnectMenuItem;
import cn.oyzh.fx.plus.menu.ImportConnectMenuItem;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.util.FileChooserUtil;
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
        // 初始化子节点
        this.initChildes();
        // 监听节点变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            ZKEventUtil.treeChildChanged();
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
        if (CollUtil.isNotEmpty(zkInfos)) {
            this.addConnects(zkInfos);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        AddConnectMenuItem addConnect = new AddConnectMenuItem("12", this::addConnect);
        ExportConnectMenuItem exportConnect = new ExportConnectMenuItem("12", this::exportConnect);
        ImportConnectMenuItem importConnect = new ImportConnectMenuItem("12", this::importConnect);
        AddGroupMenuItem addGroup = new AddGroupMenuItem("12", this::addGroup);

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
        File file = FileChooserUtil.save("保存zk连接列表", "zk连接列表.json", new FileChooser.ExtensionFilter[]{extensionFilter});
        try {
            FileUtil.writeUtf8String(export.toJSONString(), file);
            MessageBox.okToast(I18nResourceBundle.i18nString("base.actionSuccess"));
        } catch (Exception ex) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.actionFail"));
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
        File file = CollUtil.getFirst(files);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 导入连接
     */
    private void importConnect() {
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("JSON files", "*.json");
        FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("All", "*.*");
        File file = FileChooserUtil.choose("选择zk连接列表", new FileChooser.ExtensionFilter[]{filter1, filter2});
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
            MessageBox.warn(I18nResourceBundle.i18nString("base.file", "base.notExists"));
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.notSupport", "base.folder"));
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "json")) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.invalid", "base.format"));
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.contentNotEmpty"));
            return;
        }
        try {
            String text = FileUtil.readUtf8String(file);
            ZKInfoExport export = ZKInfoExport.fromJSON(text);
            List<ZKInfo> zkInfos = export.getConnects();
            if (CollUtil.isNotEmpty(zkInfos)) {
                for (ZKInfo zkInfo : zkInfos) {
                    if (this.infoStore.add(zkInfo)) {
                        this.addConnect(zkInfo);
                    } else {
                        MessageBox.warn(I18nResourceBundle.i18nString("base.connect") + "[" + zkInfo.getName() + "]" + I18nResourceBundle.i18nString("base.importFail"));
                    }
                }
                MessageBox.okToast(I18nResourceBundle.i18nString("base.actionSuccess"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nResourceBundle.i18nString("base.actionException"));
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
    public void addGroup() {
        String groupName = MessageBox.prompt(I18nResourceBundle.i18nString("base.contentTip1"));

        // 名称为null，则忽略
        if (groupName == null) {
            return;
        }

        // 不能为空
        if (StrUtil.isBlank(groupName)) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.nameNotEmpty"));
            return;
        }

        ZKGroup group = new ZKGroup();
        group.setName(groupName);
        if (this.groupStore.exist(group)) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.contentAlreadyExists"));
            return;
        }
        group = this.groupStore.add(groupName);
        if (group != null) {
            this.addChild(new ZKGroupTreeItem(group, this.getTreeView()));
        } else {
            MessageBox.warn(I18nResourceBundle.i18nString("base.actionFail"));
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
     */
    public void infoAdded(ZKInfo info) {
        this.addConnect(info);
    }

    /**
     * 连接变更事件
     */
    public void infoUpdated(ZKInfo info) {
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
