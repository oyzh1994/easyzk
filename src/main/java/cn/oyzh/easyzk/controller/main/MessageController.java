package cn.oyzh.easyzk.controller.main;

import cn.oyzh.easyzk.fx.ZKMsgTextArea;
import cn.oyzh.fx.plus.controller.SubStageController;
import javafx.fxml.FXML;


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
