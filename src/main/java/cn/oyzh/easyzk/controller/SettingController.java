package cn.oyzh.easyzk.controller;


import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.util.ZKProcessUtil;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.picker.FlexColorPicker;
import cn.oyzh.fx.plus.controls.text.FlexSlider;
import cn.oyzh.fx.plus.controls.toggle.FXToggleGroup;
import cn.oyzh.fx.plus.font.FontFamilyComboBox;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.font.FontSizeComboBox;
import cn.oyzh.fx.plus.font.FontWeightComboBox;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.i18n.LocaleComboBox;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.opacity.OpacityManager;
import cn.oyzh.fx.plus.theme.ThemeComboBox;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.i18n.I18nManager;
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
        // iconUrl = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "setting.fxml"
)
public class SettingController extends StageController {

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
    private FXCheckBox pageSize;

    /**
     * 记住页面拉伸
     */
    @FXML
    private FXCheckBox pageResize;

    /**
     * 记住页面位置
     */
    @FXML
    private FXCheckBox pageLocation;

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
    private FXCheckBox authMode;

    /**
     * 节点加载限制
     */
    @FXML
    private NumberTextField nodeLoadLimit;

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
     * 区域
     */
    @FXML
    private LocaleComboBox locale;

    /**
     * 透明度
     */
    @FXML
    private FlexSlider opacity;

    /**
     * 配置对象
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 配置持久化对象
     */
    private final ZKSettingStore settingStore = ZKSettingStore.INSTANCE;

    @Override
    public void onWindowShowing(WindowEvent event) {
        super.onWindowShowing(event);
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
        if (this.setting.getRememberPageSize() != null) {
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
        // 节点加载限制
        this.nodeLoadLimit.setValue(this.setting.nodeLoadLimit());
        // 主题相关处理
        this.theme.select(this.setting.getTheme());
        this.fgColor.setColor(StringUtil.emptyToDefault(this.setting.getFgColor(), this.theme.getFgColorHex()));
        this.bgColor.setColor(StringUtil.emptyToDefault(this.setting.getBgColor(), this.theme.getBgColorHex()));
        this.accentColor.setColor(StringUtil.emptyToDefault(this.setting.getAccentColor(), this.theme.getAccentColorHex()));
        // 字体相关处理
        this.fontSize.selectSize(this.setting.getFontSize());
        this.fontFamily.select(this.setting.getFontFamily());
        this.fontWeight.selectWeight(this.setting.getFontWeight());
        // 区域相关处理
        this.locale.select(this.setting.getLocale());
        // 透明度相关处理
        if (this.setting.getOpacity() != null) {
            this.opacity.setValue(this.setting.getOpacity());
        }
    }

    /**
     * 保存设置
     */
    @FXML
    private void saveSetting() {
        try {
            byte authMode = (byte) (this.authMode.isSelected() ? 0 : 1);
            byte loadMode = Byte.parseByte(this.loadMode.selectedUserData());
            String locale = this.locale.name();
            Byte fontSize = this.fontSize.byteValue();
            short fontWeight = this.fontWeight.getWeight();
            String fontFamily = this.fontFamily.getValue();

            // 提示文字
            String tips = this.checkConfigForRestart(fontSize, fontWeight, fontFamily, locale);
//            String tips = this.checkConfigForRestart(loadMode, authMode, fontSize, fontWeight, fontFamily, locale);

            this.setting.setLoadMode(loadMode);
            this.setting.setAuthMode(authMode);
            // 字体相关
            this.setting.setFontSize(fontSize);
            this.setting.setFontFamily(fontFamily);
            this.setting.setFontWeight(fontWeight);
            // 主题相关
            this.setting.setTheme(this.theme.name());
            this.setting.setBgColor(this.bgColor.getColor());
            this.setting.setFgColor(this.fgColor.getColor());
            this.setting.setAccentColor(this.accentColor.getColor());
            // 区域相关处理
            this.setting.setLocale(locale);
            // 透明度相关处理
            this.setting.setOpacity((float) this.opacity.getValue());
            // 节点加载限制
            this.setting.setNodeLoadLimit(this.nodeLoadLimit.getIntValue());
            // 页面设置
            this.setting.setRememberPageSize((byte) (this.pageSize.isSelected() ? 1 : 0));
            this.setting.setRememberPageResize((byte) (this.pageResize.isSelected() ? 1 : 0));
            this.setting.setRememberPageLocation((byte) (this.pageLocation.isSelected() ? 1 : 0));
            this.setting.setExitMode((byte) Integer.parseInt(this.exitMode.selectedUserData()));
            this.settingStore.replace(this.setting);
            // // 清除认证列表
            // if (!this.setting.isAutoAuth()) {
            //     ZKAuthUtil.clearAuthed();
            // }
            // // 执行提示
            // MessageBox.okToast(I18nHelper.operationSuccess());
            // 关闭窗口
            this.closeWindow();
            // 应用区域配置
            I18nManager.apply(this.setting.getLocale());
            // 应用字体配置
            FontManager.apply(this.setting.fontConfig());
            // 应用主题配置
            ThemeManager.apply(this.setting.themeConfig());
            // 应用透明度配置
            OpacityManager.apply((float) this.opacity.getValue());
            // 提示不为空，说明需要重启，则执行重启
            if (StringUtil.isNotBlank(tips) && MessageBox.confirm(tips)) {
                ZKProcessUtil.restartApplication();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 检查重启软件配置
     *
     * @param fontSize   字体大小
     * @param fontWeight 字体宽度
     * @param fontFamily 字体名称
     * @param locale     区域
     * @return 结果
     */
    private String checkConfigForRestart(Byte fontSize, Short fontWeight, String fontFamily, String locale) {
        if (!Objects.equals(this.setting.getFontSize(), fontSize) || !Objects.equals(this.setting.getLocale(), locale)
                || !Objects.equals(this.setting.getFontFamily(), fontFamily) || !Objects.equals(this.setting.getFontWeight(), fontWeight)) {
            return I18nResourceBundle.i18nString("base.restartTip1");
        }
        return "";
    }

//    /**
//     * 检查重启软件配置
//     *
//     * @param loadMode   加载模式
//     * @param authMode   认证模式
//     * @param fontSize   字体大小
//     * @param fontWeight 字体宽度
//     * @param fontFamily 字体名称
//     * @param locale     区域
//     * @return 结果
//     */
//    private String checkConfigForRestart(byte loadMode, byte authMode, Byte fontSize, Short fontWeight, String fontFamily, String locale) {
//        if (!Objects.equals(this.setting.getLoadMode(), loadMode) || !Objects.equals(this.setting.getAuthMode(), authMode)
//                || !Objects.equals(this.setting.getFontSize(), fontSize) || !Objects.equals(this.setting.getLocale(), locale)
//                || !Objects.equals(this.setting.getFontFamily(), fontFamily) || !Objects.equals(this.setting.getFontWeight(), fontWeight)) {
//            return I18nResourceBundle.i18nString("base.restartTip1");
//        }
//        return "";
//    }

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

    /**
     * 重置区域
     */
    @FXML
    private void resetLocale() {
        this.locale.select((String) null);
    }

    /**
     * 重置透明度
     */
    @FXML
    private void resetOpacity() {
        this.opacity.setValue(OpacityManager.defaultOpacity * 100);
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.settingTitle();
    }
}
