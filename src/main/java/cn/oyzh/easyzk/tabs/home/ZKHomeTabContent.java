package cn.oyzh.easyzk.tabs.home;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import javafx.fxml.FXML;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    @Resource
    private Project project;

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        super.initialize(url, resource);
        this.softInfo.setText(this.i18nString("base.soft") + ": v" + this.project.getVersion() + " Powered by oyzh.");
        String jdkInfo = "";
        if (System.getProperty("java.vm.name") != null) {
            jdkInfo += System.getProperty("java.vm.name");
        }
        if (System.getProperty("java.vm.version") != null) {
            jdkInfo += System.getProperty("java.vm.version");
        }
        this.jdkInfo.setText(this.i18nString("base.env") + ": " + jdkInfo);
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

    @Override
    public String i18nId() {
        return "home";
    }
}
