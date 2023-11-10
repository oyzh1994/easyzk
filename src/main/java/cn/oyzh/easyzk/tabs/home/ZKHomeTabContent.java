package cn.oyzh.easyzk.tabs.home;

import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controls.tab.DynamicTabController;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.event.EventUtil;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * zk主页tab内容组件
 *
 * @author oyzh
 * @since 2023/5/24
 */
@Lazy
@Component
public class ZKHomeTabContent extends DynamicTabController {

    /**
     * 软件信息
     */
    @FXML
    private FXLabel softInfo;

    /**
     * 环境信息
     */
    @FXML
    private FXLabel jdkInfo;

    /**
     * 项目对象
     */
    @Autowired
    private Project project;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.softInfo.setText("程序信息 v" + this.project.getVersion() + " Powered by oyzh.");
        String jdkInfo = "";
        if (System.getProperty("java.vm.name") != null) {
            jdkInfo += System.getProperty("java.vm.name");
        }
        if (System.getProperty("java.vm.version") != null) {
            jdkInfo += System.getProperty("java.vm.version");
        }
        this.jdkInfo.setText("环境信息 " + jdkInfo);
    }

    /**
     * 新增连接
     */
    @FXML
    private void addConnect() {
        EventUtil.fire(ZKEventTypes.ZK_ADD_CONNECT);
    }

    /**
     * 添加分组
     */
    @FXML
    private void addGroup() {
        EventUtil.fire(ZKEventTypes.ZK_ADD_GROUP);
    }

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        ZKEventUtil.terminalOpen();
    }
}
