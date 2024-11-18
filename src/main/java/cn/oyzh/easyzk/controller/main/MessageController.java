package cn.oyzh.easyzk.controller.main;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.dto.ZKDataHistoryVO;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKHistoryAddedEvent;
import cn.oyzh.easyzk.event.ZKHistoryShowEvent;
import cn.oyzh.easyzk.event.ZKNodeSelectedEvent;
import cn.oyzh.easyzk.fx.ZKMsgTextArea;
import cn.oyzh.easyzk.store.ZKDataHistoryStore2;
import cn.oyzh.easyzk.tabs.connect.ZKConnectTab;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tabs.TabClosedEvent;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


/**
 * zk数据历史业务
 *
 * @author oyzh
 * @since 2024/04/23
 */
public class MessageController extends SubStageController   {

    /**
     * 消息文本框
     */
    @FXML
    private ZKMsgTextArea msgArea;

    /**
     * 清空节点消息
     */
    @FXML
    private void clearMsg() {
        this.msgArea.clear();
    }

}
