package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.dto.ZKQueryParam;
import cn.oyzh.easyzk.dto.ZKQueryResult;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.property.KeyValueProperty;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk更新日志tab内容组件
 *
 * @author oyzh
 * @since 2024/04/07
 */
public class ZKQueryStatTabController extends DynamicTabController {


    @FXML
    private FlexTableView<KeyValueProperty<String,Object>> statTable;

    public void init(Stat stat) {
        List<KeyValueProperty<String, Object>> data = new ArrayList<>();
        data.add(KeyValueProperty.of("pzxid", stat.getPzxid()));

        data.add(KeyValueProperty.of("czxid", stat.getCzxid()));
        data.add(KeyValueProperty.of("ctime", stat.getCtime()));

        data.add(KeyValueProperty.of("mzxid", stat.getMzxid()));
        data.add(KeyValueProperty.of("mtime", stat.getMtime()));

        data.add(KeyValueProperty.of("version", stat.getVersion()));
        data.add(KeyValueProperty.of("cversion", stat.getCversion()));
        data.add(KeyValueProperty.of("aversion", stat.getAversion()));
        data.add(KeyValueProperty.of("aversion", stat.getEphemeralOwner()));

        data.add(KeyValueProperty.of("dataLength", stat.getDataLength()));
        data.add(KeyValueProperty.of("numChildren", stat.getNumChildren()));
        data.add(KeyValueProperty.of("ephemeralOwner", stat.getEphemeralOwner()));

        this.statTable.setItem(data);
    }

}