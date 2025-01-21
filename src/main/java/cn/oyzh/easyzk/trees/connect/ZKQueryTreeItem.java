package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKQueryStore;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKQueryTreeItem extends RichTreeItem<ZKQueryTreeItemValue> {

    private final ZKQuery value;

    private final ZKQueryStore queryStore = ZKQueryStore.INSTANCE;

    public ZKQueryTreeItem(ZKQuery query, RichTreeView treeView) {
        super(treeView);
        this.value = query;
        this.setValue(new ZKQueryTreeItemValue(this));
    }

    @Override
    public ZKQueryTypeTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (ZKQueryTypeTreeItem) parent;
    }

    public ZKClient client() {
        return this.parent().client();
    }

    public ZKConnect zkConnect() {
        return this.parent().zkConnect();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openQuery = MenuItemHelper.openQuery("12", this::loadChild);
        FXMenuItem renameQuery = MenuItemHelper.renameQuery("12", this::rename);
        FXMenuItem deleteQuery = MenuItemHelper.deleteQuery("12", this::delete);
        items.add(openQuery);
        items.add(renameQuery);
        items.add(deleteQuery);
        return items;
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.deleteQuery() + "[" + this.value.getName() + "]?")) {
            this.queryStore.delete(this.value);
            super.delete();
        }
    }

    @Override
    public void rename() {
        String queryName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (queryName == null || Objects.equals(queryName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(queryName)) {
            MessageBox.warn(I18nHelper.nameCanNotEmpty());
            return;
        }
        this.value.setName(queryName);
        // 修改名称
        if (this.queryStore.update(this.value)) {
            this.setValue(new ZKQueryTreeItemValue(this));
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void loadChild() {
        ZKEventUtil.openQuery(this.client(), this.value);
    }

    @Override
    public void onPrimaryDoubleClick() {
        this.loadChild();
    }

}
