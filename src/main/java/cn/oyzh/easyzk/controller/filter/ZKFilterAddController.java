package cn.oyzh.easyzk.controller.filter;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.plus.controller.FXController;
import cn.oyzh.fx.plus.controls.ToggleSwitch;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.ext.ClearableTextField;
import cn.oyzh.fx.plus.handler.TabSwitchHandler;
import cn.oyzh.fx.plus.information.FXAlertUtil;
import cn.oyzh.fx.plus.information.FXTipUtil;
import cn.oyzh.fx.plus.information.FXToastUtil;
import cn.oyzh.fx.plus.view.FXWindow;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.parser.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKFilterStore;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * zk过滤配置添加业务
 *
 * @author oyzh
 * @since 2022/12/20
 */
@Slf4j
@FXWindow(
        title = "zk过滤配置新增",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "filter/zkFilterAdd.fxml"
)
public class ZKFilterAddController extends FXController {

    /**
     * 关键字
     */
    @FXML
    private ClearableTextField kw;

    /**
     * 是否启用
     */
    @FXML
    private ToggleSwitch enable;

    /**
     * 模糊匹配
     */
    @FXML
    private ToggleSwitch partMatch;

    /**
     * zk过滤配置储存
     */
    private final ZKFilterStore filterStore = ZKFilterStore.INSTANCE;

    /**
     * 添加zk过滤配置
     */
    @FXML
    private void addZKFilter() {
        // 获取节点值
        String kw = this.kw.getText().trim();
        if (StrUtil.isBlank(kw)) {
            FXTipUtil.tip("请输入过滤关键字！", this.kw);
            return;
        }
        if (this.filterStore.exist(kw)) {
            FXTipUtil.tip("此关键字已存在！", this.kw);
            return;
        }
        try {
            ZKFilter filter = new ZKFilter();
            filter.setKw(kw);
            filter.setEnable(this.enable.isSelected());
            filter.setPartMatch(this.partMatch.isSelected());
            if (this.filterStore.add(filter)) {
                EventUtil.fire(ZKEventTypes.ZK_FILTER_ADDED);
                ZKEventUtil.treeChildFilter();
                FXToastUtil.ok("新增ZK过滤配置成功!");
                this.closeView();
            } else {
                FXAlertUtil.warn("新增ZK过滤配置失败！");
            }
        } catch (Exception e) {
            FXAlertUtil.warn(e, ZKExceptionParser.INSTANCE);
        }
    }

    @Override
    public void onViewShown(WindowEvent event) {
        TabSwitchHandler.init(this.view);
        this.view.hideOnEscape();
    }

    @Override
    public void onViewHidden(WindowEvent event) {
        TabSwitchHandler.destroy(this.view);
        super.onViewHidden(event);
    }
}
