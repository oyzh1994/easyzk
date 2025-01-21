package cn.oyzh.easyzk.tabs.home;

import cn.oyzh.common.dto.Project;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * zk主页tab内容组件
 *
 * @author oyzh
 * @since 2023/5/24
 */
public class ZKHomeTabController extends DynamicTabController {

    /**
     * 软件信息
     */
    @FXML
    private FXText softInfo;

    /**
     * 环境信息
     */
    @FXML
    private FXText jdkInfo;

    /**
     * 项目对象
     */
    private final Project project = Project.load();

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        super.initialize(url, resource);
        this.softInfo.setText(I18nHelper.soft() + ": v" + this.project.getVersion() + " Powered by oyzh.");
        String jdkInfo = "";
        if (System.getProperty("java.vm.name") != null) {
            jdkInfo += System.getProperty("java.vm.name");
        }
        if (System.getProperty("java.vm.version") != null) {
            jdkInfo += System.getProperty("java.vm.version");
        }
        this.jdkInfo.setText(I18nHelper.env() + " : " + jdkInfo);
    }

    /**
     * 新增连接
     */
    @FXML
    private void addConnect() {
        ZKEventUtil.addConnect();
    }

    /**
     * 添加分组
     */
    @FXML
    private void addGroup() {
        ZKEventUtil.addGroup();
    }

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        ZKEventUtil.terminalOpen();
    }

    /**
     * 更新日志
     */
    @FXML
    private void changelog() {
        ZKEventUtil.changelog();
    }
}
