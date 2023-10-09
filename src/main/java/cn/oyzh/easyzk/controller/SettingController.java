package cn.oyzh.easyzk.controller;


import cn.oyzh.fx.plus.controller.FXController;
import cn.oyzh.fx.plus.controls.FXToggleGroup;
import cn.oyzh.fx.plus.controls.FlexCheckBox;
import cn.oyzh.fx.plus.information.FXToastUtil;
import cn.oyzh.fx.plus.view.FXWindow;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.util.ZKAuthUtil;
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
@FXWindow(
        title = "应用设置",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "setting.fxml"
)
public class SettingController extends FXController {

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
    //
    // /**
    //  * 页面布局
    //  */
    // @FXML
    // private FXToggleGroup layout;
    //
    // /**
    //  * 页面布局1
    //  */
    // @FXML
    // private RadioButton layout1;
    //
    // /**
    //  * 页面布局2
    //  */
    // @FXML
    // private RadioButton layout2;

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

//    /**
//     * 分组自动展开
//     */
//    @FXML
//    private FlexCheckBox groupExpand;

    /**
     * 配置对象
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 配置持久化对象
     */
    private final ZKSettingStore settingStore = ZKSettingStore.INSTANCE;

    @Override
    public void onViewShowing(WindowEvent event) {
        super.onViewShowing(event);
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

//        // 分组展开处理
//        if (this.setting.getGroupExpand() != null) {
//            this.groupExpand.setSelected(this.setting.isGroupExpand());
//        }

        // // 布局模式处理
        // if (this.setting.getLayout() != null) {
        //     switch (this.setting.getLayout()) {
        //         case 0 -> this.layout1.setSelected(true);
        //         case 1 -> this.layout2.setSelected(true);
        //     }
        // }
    }

    /**
     * 保存设置
     */
    @FXML
    private void saveSetting() {
        String tips = "";
        int authMode = this.authMode.isSelected() ? 0 : 1;
//        int groupExpand = this.groupExpand.isSelected() ? 1 : 0;
//         int layout = Integer.parseInt(this.layout.selectedUserData());
        int loadMode = Integer.parseInt(this.loadMode.selectedUserData());
        // 设置参数
        if (!Objects.equals(this.setting.getLoadMode(), loadMode) || !Objects.equals(this.setting.getAuthMode(), authMode)) {
            tips = "（重新打开连接生效）";
        }
//         if ( !Objects.equals(this.setting.getLayout(), layout)) {
// //        if (!Objects.equals(this.setting.getGroupExpand(), groupExpand) || !Objects.equals(this.setting.getLayout(), layout)) {
//             tips = "（重启软件生效）";
//         }
        // this.setting.setLayout(layout);
        this.setting.setLoadMode(loadMode);
        this.setting.setAuthMode(authMode);
//        this.setting.setGroupExpand(groupExpand);
        this.setting.setPageInfo(this.pageSize.isSelected() ? 1 : 0);
        this.setting.setRememberPageResize(this.pageResize.isSelected() ? 1 : 0);
        this.setting.setRememberPageLocation(this.pageLocation.isSelected() ? 1 : 0);
        this.setting.setExitMode(Integer.parseInt(this.exitMode.selectedUserData()));
        if (this.settingStore.update(this.setting)) {
            // 清除认证列表
            if (!this.setting.isAutoAuth()) {
                ZKAuthUtil.clearAuthed();
            }
            FXToastUtil.ok("保存配置成功" + tips);
            this.closeView();
        } else {
            FXToastUtil.warn("保存配置失败！");
        }
    }

    @Override
    public void onViewShown(WindowEvent event) {
        this.view.hideOnEscape();
    }
}
