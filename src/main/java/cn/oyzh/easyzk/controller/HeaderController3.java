// package cn.oyzh.easyzk.controller;
//
// import cn.oyzh.common.SysConst;
// import cn.oyzh.easyzk.event.ZKEventUtil;
// import cn.oyzh.easyzk.util.ZKViewFactory;
// import cn.oyzh.event.EventSubscribe;
// import cn.oyzh.fx.gui.event.Layout1Event;
// import cn.oyzh.fx.gui.event.Layout2Event;
// import cn.oyzh.fx.gui.svg.pane.LayoutSVGPane;
// import cn.oyzh.fx.plus.controller.StageController;
// import cn.oyzh.fx.plus.information.MessageBox;
// import cn.oyzh.fx.plus.window.StageManager;
// import cn.oyzh.i18n.I18nHelper;
// import javafx.fxml.FXML;
//
// /**
//  * 主页头部业务
//  *
//  * @author oyzh
//  * @since 2022/1/26
//  */
// public class HeaderController3 extends StageController {
//
//     /**
//      * 布局组件
//      */
//     @FXML
//     private LayoutSVGPane layoutPane;
//
//     /**
//      * 设置
//      */
//     @FXML
//     private void setting() {
// //        StageAdapter wrapper = StageManager.getStage(SettingController2.class);
// //        if (wrapper != null) {
// //            wrapper.toFront();
// //        } else {
// //            StageManager.showStage(SettingController2.class, this.stage);
// //        }
//         ZKViewFactory.setting();
//     }
//
//     /**
//      * 关于
//      */
//     @FXML
//     private void about() {
// //        StageManager.showStage(AboutController.class, this.stage);
//         ZKViewFactory.about();
//     }
//
//     /**
//      * 退出
//      */
//     @FXML
//     private void quit() {
//         if (MessageBox.confirm(I18nHelper.quit() + " " + SysConst.projectName())) {
//             StageManager.exit();
//         }
//     }
//
//     /**
//      * 传输数据
//      */
//     @FXML
//     private void transport() {
// //        StageAdapter wrapper = StageManager.getStage(ZKDataTransportController.class);
// //        if (wrapper != null) {
// //            wrapper.toFront();
// //        } else {
// //            StageManager.showStage(ZKDataTransportController.class, this.stage);
// //        }
//         ZKViewFactory.transportData(null);
//     }
//
//     /**
//      * 工具箱
//      */
//     @FXML
//     private void tool() {
// //        StageManager.showStage(ZKToolController.class, StageManager.getPrimaryStage());
//         ZKViewFactory.tool();
//     }
//
// //    /**
// //     * 布局1
// //     */
// //    @FXML
// //    private void layout1() {
// //        ZKEventUtil.layout1();
// //    }
// //
// //    /**
// //     * 布局2
// //     */
// //    @FXML
// //    private void layout2() {
// //        ZKEventUtil.layout2();
// //    }
//
//     /**
//      * 布局1事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void layout1(Layout1Event event) {
//         this.layoutPane.layout2();
//     }
//
//     /**
//      * 布局2事件
//      *
//      * @param event 事件
//      */
//     @EventSubscribe
//     private void layout2(Layout2Event event) {
//         this.layoutPane.layout1();
//     }
//
//     /**
//      * 布局
//      */
//     @FXML
//     private void layout() {
//         if (!this.layoutPane.isLayout1()) {
//             ZKEventUtil.layout2();
//         } else {
//             ZKEventUtil.layout1();
//         }
//     }
//
//     /**
//      * 迁移
//      */
//     @FXML
//     private void migration() {
// //        StageManager.showStage(ZKMigrationDataController.class, this.stage);
//         ZKViewFactory.migrationData();
//     }
//
// //    /**
// //     * 分割面板
// //     */
// //    @FXML
// //    private FXPane splitPane;
// //
// //    @Override
// //    public void onWindowShowing(WindowEvent event) {
// //        super.onWindowShowing(event);
// //        if (OSUtil.isWindows() || OSUtil.isLinux()) {
// //            this.splitPane.setFlexHeight("100% - 280");
// //        }
// //    }
// }
