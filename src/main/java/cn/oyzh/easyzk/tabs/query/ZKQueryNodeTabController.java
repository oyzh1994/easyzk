package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import cn.oyzh.fx.plus.property.KeyValueProperty;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryNodeTabController extends DynamicTabController {

    @FXML
    private FXTableView<KeyValueProperty<String, String>> nodeTable;

    public void init(String path, List<String> nodes) {
        List<KeyValueProperty<String, String>> data = new ArrayList<>();
        int index = 1;
        for (String node : nodes) {
            data.add(KeyValueProperty.of(index + "", ZKNodeUtil.concatPath(path, node)));
            index++;
        }
        this.nodeTable.setItem(data);
    }

}