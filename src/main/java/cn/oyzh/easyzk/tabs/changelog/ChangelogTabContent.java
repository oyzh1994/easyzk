package cn.oyzh.easyzk.tabs.changelog;

import cn.oyzh.fx.plus.changelog.Changelog;
import cn.oyzh.fx.plus.changelog.ChangelogListView;
import cn.oyzh.fx.plus.changelog.ChangelogManager;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.util.ResourceUtil;
import javafx.fxml.FXML;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * zk更新日志tab内容组件
 *
 * @author oyzh
 * @since 2024/04/07
 */
@Lazy
@Component
public class ChangelogTabContent extends DynamicTabController {

    /**
     * 更新日志
     */
    @FXML
    private ChangelogListView changelog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        // 更新日志列表
        List<Changelog> changelogs = ChangelogManager.load(ResourceUtil.getResource("/changelog.json"));
        // 初始化更新日志
        this.changelog.init(changelogs);
    }
}
