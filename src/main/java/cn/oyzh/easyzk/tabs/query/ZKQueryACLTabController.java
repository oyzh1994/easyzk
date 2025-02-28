package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import cn.oyzh.fx.plus.property.Param3Property;
import javafx.fxml.FXML;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.List;

/**
 * zk更新日志tab内容组件
 *
 * @author oyzh
 * @since 2024/04/07
 */
public class ZKQueryACLTabController extends RichTabController {

    @FXML
    private FXTableView<Param3Property<String, String, String>> aclTable;

    public void init(List<ACL> aclList) {
        List<Param3Property<String, String, String>> data = new ArrayList<>();
        for (ACL acl : aclList) {
            data.add(Param3Property.of(acl.getId().getId(), acl.getId().getScheme(), ZKACLUtil.toPermStr(acl.getPerms(), ",")));
        }
        this.aclTable.setItem(data);
    }

}