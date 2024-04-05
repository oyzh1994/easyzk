package cn.oyzh.easyzk.controller;


import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FXToggleGroup;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.digital.NumberTextField;
import cn.oyzh.fx.plus.controls.picker.FlexColorPicker;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.font.FontFamilyComboBox;
import cn.oyzh.fx.plus.font.FontWeightComboBox;
import cn.oyzh.fx.plus.font.FontSizeComboBox;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.tabs.DynamicTabStrategyComboBox;
import cn.oyzh.fx.plus.theme.ThemeComboBox;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.util.Objects;

/**
 * 应用设置业务
 *
 * @author oyzh
 * @since 2022/08/26
 */
@StageAttribute(
        title = "应用设置",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        value = ZKConst.FXML_BASE_PATH + "setting.fxml"
)
public class SettingController extends Controller {

    /**
     * 退出方式
     */
    @FXML
    private FXToggleGroup exitMode;

    /**
     * 退出方式0
     */
    @FXML
    private RadioButton exitMode0;

    /**
     * 退出方式1
     */
    @FXML
    private RadioButton exitMode1;

    /**
     * 退出方式2
     */
    @FXML
    private RadioButton exitMode2;

    /**
     * 记住页面大小
     */
    @FXML
    private FlexCheckBox pageSize;

    /**
     * 记住页面拉伸
     */
    @FXML
    private FlexCheckBox pageResize;

    /**
     * 记住页面位置
     */
    @FXML
    private FlexCheckBox pageLocation;

    /**
     * 节点加载
     */
    @FXML
    private FXToggleGroup loadMode;

    /**
     * 节点加载方式0
     */
    @FXML
    private RadioButton loadMode0;

    /**
     * 节点加载方式1
     */
    @FXML
    private RadioButton loadMode1;

    /**
     * ZK连接后加载方式2
     */
    @FXML
    private RadioButton loadMode2;

    /**
     * 节点自动认证
     */
    @FXML
    private FlexCheckBox authMode;

    /**
     * 标签数量限制
     */
    @FXML
    private NumberTextField tabLimit;

    /**
     * 标签策略
     */
    @FXML
    private DynamicTabStrategyComboBox tabStrategy;

    /**
     * 主题
     */
    @FXML
    private ThemeComboBox theme;

    /**
     * 背景色
     */
    @FXML
    private FlexColorPicker bgColor;

    /**
     * 前景色
     */
    @FXML
    private FlexColorPicker fgColor;

    /**
     * 强调色
     */
    @FXML
    private FlexColorPicker accentColor;

    /**
     * 背景色
     */
    @FXML
    private FlexHBox bgColorBox;

    /**
     * 前景色
     */
    @FXML
    private FlexHBox fgColorBox;

    /**
     * 强调色
     */
    @FXML
    private FlexHBox accentColorBox;

    /**
     * 字体大小
     */
    @FXML
    private FontSizeComboBox fontSize;

    /**
     * 字体粗细
     */
    @FXML
    private FontWeightComboBox fontWeight;

    /**
     * 字体名称
     */
    @FXML
    private FontFamilyComboBox fontFamily;


    /**
     * 配置对象
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 配置持久化对象
     */
    private final ZKSettingStore settingStore = ZKSettingStore.INSTANCE;

    @Override
    public void onStageShowing(WindowEvent event) {
        super.onStageShowing(event);
        // 应用退出处理
        if (this.setting.getExitMode() != null) {
            switch (this.setting.getExitMode()) {
                case 0 -> this.exitMode0.setSelected(true);
                case 1 -> this.exitMode1.setSelected(true);
                case 2 -> this.exitMode2.setSelected(true);
            }
        }
        // 节点加载处理
        if (this.setting.getLoadMode() != null) {
            switch (this.setting.getLoadMode()) {
                case 0 -> this.loadMode0.setSelected(true);
                case 1 -> this.loadMode1.setSelected(true);
                case 2 -> this.loadMode2.setSelected(true);
            }
        }
        // 节点认证处理
        if (this.setting.getAuthMode() != null) {
            this.authMode.setSelected(this.setting.isAutoAuth());
        }
        // 记住页面大小处理
        if (this.setting.getPageInfo() != null) {
            this.pageSize.setSelected(this.setting.isRememberPageSize());
        }
        // 记住页面拉伸处理
        if (this.setting.getRememberPageResize() != null) {
            this.pageResize.setSelected(this.setting.isRememberPageResize());
        }
        // 记住页面位置处理
        if (this.setting.getRememberPageLocation() != null) {
            this.pageLocation.setSelected(this.setting.isRememberPageLocation());
        }
        // 主题相关处理
        this.theme.select(this.setting.getTheme());
        this.fgColor.setColor(this.setting.getFgColor());
        this.bgColor.setColor(this.setting.getBgColor());
        this.accentColor.setColor(this.setting.getAccentColor());
        // 标签相关处理
        this.tabLimit.setValue(this.setting.getTabLimit());
        this.tabStrategy.select(this.setting.getTabStrategy());
        // 字体相关处理
        this.fontSize.select(this.setting.getFontSize());
        this.fontFamily.select(this.setting.getFontFamily());
        this.fontWeight.selectWeight(this.setting.getFontWeight());
    }

    /**
     * 保存设置
     */
    @FXML
    private void saveSetting() {
        String tips = "";
        byte authMode = (byte) (this.authMode.isSelected() ? 0 : 1);
        byte loadMode = Byte.parseByte(this.loadMode.selectedUserData());
        // 设置参数
        if (!Objects.equals(this.setting.getLoadMode(), loadMode) || !Objects.equals(this.setting.getAuthMode(), authMode)) {
            tips = "（重新打开连接生效）";
        }
        this.setting.setLoadMode(loadMode);
        this.setting.setAuthMode(authMode);
        // 字体相关
        this.setting.setFontSize(this.fontSize.getValue());
        this.setting.setFontWeight(this.fontWeight.getWeight());
        this.setting.setFontFamily(this.fontFamily.getValue());
        // 主题相关
        this.setting.setTheme(this.theme.name());
        this.setting.setBgColor(this.bgColor.getColor());
        this.setting.setFgColor(this.fgColor.getColor());
        this.setting.setAccentColor(this.accentColor.getColor());
        // 其他设置
        this.setting.setPageInfo(this.pageSize.isSelected() ? 1 : 0);
        this.setting.setTabStrategy(this.tabStrategy.getStrategy());
        this.setting.setTabLimit(this.tabLimit.getValue().intValue());
        this.setting.setRememberPageResize(this.pageResize.isSelected() ? 1 : 0);
        this.setting.setRememberPageLocation(this.pageLocation.isSelected() ? 1 : 0);
        this.setting.setExitMode(Integer.parseInt(this.exitMode.selectedUserData()));
        if (this.settingStore.update(this.setting)) {
            // 清除认证列表
            if (!this.setting.isAutoAuth()) {
                ZKAuthUtil.clearAuthed();
            }
            MessageBox.okToast("保存配置成功" + tips);
            this.closeStage();
            // 应用字体配置
            FontManager.apply(this.setting.fontConfig());
            // 应用主题配置
            ThemeManager.apply(this.setting.themeConfig());
        } else {
            MessageBox.warnToast("保存配置失败！");
        }
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.fgColorBox.disableProperty().bind(this.accentColorBox.disabledProperty());
        this.bgColorBox.disableProperty().bind(this.accentColorBox.disabledProperty());
        this.theme.selectedItemChanged((observableValue, number, t1) -> {
            this.accentColorBox.setDisable(this.theme.isSystem());
            this.fgColor.setValue(t1.getForegroundColor());
            this.bgColor.setValue(t1.getBackgroundColor());
            this.accentColor.setValue(t1.getAccentColor());
        });
        if (!this.theme.isSystem()) {
            this.accentColorBox.enable();
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.stage.hideOnEscape();
    }

    /**
     * 重置前景色
     */
    @FXML
    private void resetFgColor() {
        this.fgColor.setValue(this.theme.getValue().getForegroundColor());
    }

    /**
     * 重置背景色
     */
    @FXML
    private void resetBgColor() {
        this.bgColor.setValue(this.theme.getValue().getBackgroundColor());
    }

    /**
     * 重置强调色
     */
    @FXML
    private void resetAccentColor() {
        this.accentColor.setValue(this.theme.getValue().getAccentColor());
    }

    /**
     * 重置字体名称
     */
    @FXML
    private void resetFontFamily() {
        this.fontFamily.select(null);
    }

    /**
     * 重置字体大小
     */
    @FXML
    private void resetFontSize() {
        this.fontSize.select(null);
    }

    /**
     * 重置字体粗细
     */
    @FXML
    private void resetFontWeight() {
        this.fontWeight.select(null);
    }
}
