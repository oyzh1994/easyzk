package cn.oyzh.easyzk.controller.filter;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKFilterStore;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.BaseResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * zk过滤配置添加业务
 *
 * @author oyzh
 * @since 2022/12/20
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        value = ZKConst.FXML_BASE_PATH + "filter/zkFilterAdd.fxml"
)
public class ZKFilterAddController extends Controller {

    /**
     * 关键字
     */
    @FXML
    private ClearableTextField kw;

    /**
     * 是否启用
     */
    @FXML
    private FXToggleSwitch enable;

    /**
     * 匹配方式
     */
    @FXML
    private FXToggleSwitch matchMode;

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
            MessageBox.tipMsg(BaseResourceBundle.getBaseString("base.contentNotEmpty"), this.kw);
            return;
        }
        if (this.filterStore.exist(kw)) {
            MessageBox.tipMsg(BaseResourceBundle.getBaseString("base.contentAlreadyExists"), this.kw);
            return;
        }
        try {
            ZKFilter filter = new ZKFilter();
            filter.setKw(kw);
            filter.setEnable(this.enable.isSelected());
            filter.setPartMatch(this.matchMode.isSelected());
            if (this.filterStore.add(filter)) {
                ZKEventUtil.filterAdded();
                ZKEventUtil.treeChildFilter();
                MessageBox.okToast(BaseResourceBundle.getBaseString("base.actionSuccess"));
                this.closeStage();
            } else {
                MessageBox.warn(BaseResourceBundle.getBaseString("base.actionFail"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String i18nId() {
        return "filter.add";
    }
}
