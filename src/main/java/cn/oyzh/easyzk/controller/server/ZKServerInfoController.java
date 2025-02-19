//package cn.oyzh.easyzk.controller.server;
//
//import cn.oyzh.common.log.JulLog;
//import cn.oyzh.common.thread.ExecutorUtil;
//import cn.oyzh.easyzk.dto.ZKClusterNode;
//import cn.oyzh.easyzk.dto.ZKEnvNode;
//import cn.oyzh.easyzk.vo.ZKServerInfo;
//import cn.oyzh.easyzk.zk.ZKClient;
//import cn.oyzh.fx.plus.FXConst;
//import cn.oyzh.fx.plus.controller.StageController;
//import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
//import cn.oyzh.fx.plus.controls.table.FXTableView;
//import cn.oyzh.fx.plus.window.StageAttribute;
//import cn.oyzh.i18n.I18nHelper;
//import javafx.fxml.FXML;
//import javafx.stage.Modality;
//import javafx.stage.WindowEvent;
//
//import java.util.List;
//import java.util.concurrent.Future;
//
//
///**
// * zk配置信息业务
// *
// * @author oyzh
// * @since 2022/08/25
// */
//@StageAttribute(
//        modality = Modality.WINDOW_MODAL,
//        value = FXConst.FXML_PATH + "server/zkServerInfo.fxml"
//)
//public class ZKServerInfoController extends StageController {
//
//    /**
//     * 服务信息
//     */
//    @FXML
//    private FXTableView<ZKServerInfo> serverTable;
//
//    /**
//     * 延迟信息
//     */
//    @FXML
//    private FlexTableColumn<ZKServerInfo, String> latency;
//
//    /**
//     * 命令信息
//     */
//    @FXML
//    private FlexTableColumn<ZKServerInfo, String> command;
//
//    /**
//     * 状态信息
//     */
//    @FXML
//    private FXTableView<ZKEnvNode> statTable;
//
//    /**
//     * 配置信息
//     */
//    @FXML
//    private FXTableView<ZKEnvNode> confTable;
//
//    /**
//     * 服务信息
//     */
//    @FXML
//    private FXTableView<ZKEnvNode> srvrTable;
//
//    /**
//     * 客户端环境
//     */
//    @FXML
//    private FXTableView<ZKEnvNode> localEnvTable;
//
//    /**
//     * 服务端环境
//     */
//    @FXML
//    private FXTableView<ZKEnvNode> serverEnvTable;
//
//    /**
//     * 集群列表
//     */
//    @FXML
//    private FXTableView<ZKClusterNode> clusterTable;
//
//    /**
//     * 汇总信息
//     */
//    @FXML
//    private ZKAggregationController aggregationController;
//
//    /**
//     * zk客户端
//     */
//    private ZKClient zkClient;
//
//    /**
//     * 刷新任务
//     */
//    private Future<?> refreshTask;
//
//    @Override
//    protected void bindListeners() {
//        super.bindListeners();
//        this.stage.hideOnEscape();
//    }
//
//    @Override
//    public void onWindowShown(WindowEvent event) {
//        this.zkClient = this.getWindowProp("zkClient");
//        // 初始化基本信息
//        this.refreshLocal();
//        this.refreshEnvi();
//        this.refreshSrvr();
//        this.refreshStat();
//        this.refreshConf();
//        this.refreshCluster();
//        // 设置信息
//        String command = this.command.getText() + "(" + I18nHelper.received() + "/" + I18nHelper.sent() + "/" + I18nHelper.outstanding() + ")";
//        this.command.setText(command);
//        String latency = this.latency.getText() + "(" + I18nHelper.min() + "/" + I18nHelper.avg() + "/" + I18nHelper.max() + ")" + I18nHelper.millisecond();
//        this.latency.setText(latency);
//        // 初始化刷新任务
//        this.initRefreshTask();
//    }
//
//    /**
//     * 初始化自动刷新任务
//     */
//    private void initRefreshTask() {
//        this.refreshTask = ExecutorUtil.start(this::renderPane, 0, 3_000);
//        JulLog.debug("RefreshTask started.");
//    }
//
//    /**
//     * 关闭自动刷新任务
//     */
//    public void closeRefreshTask() {
//        try {
//            ExecutorUtil.cancel(this.refreshTask);
//            JulLog.debug("RefreshTask closed.");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    /**
//     * 渲染主面板
//     */
//    private void renderPane() {
//        try {
//            // 服务信息
//            ZKServerInfo serverInfo;
//            // 初始化
//            if (this.serverTable.isItemEmpty()) {
//                serverInfo = new ZKServerInfo();
//                this.serverTable.setItem(serverInfo);
//            } else {// 获取
//                serverInfo = (ZKServerInfo) this.serverTable.getItem(0);
//            }
//            // 获取信息
//            List<ZKEnvNode> envNodes = this.zkClient.srvrNodes();
//            // 更新信息
//            serverInfo.update(envNodes);
//            // 初始化图表
//            this.aggregationController.init(serverInfo);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    public String getViewTitle() {
//        return I18nHelper.serverInfo();
//    }
//
//    @FXML
//    private void refreshLocal() {
//        // 客户端环境信息
//        List<ZKEnvNode> localEnviNodes = this.zkClient.localNodes();
//        this.localEnvTable.setItem(localEnviNodes);
//    }
//
//    @FXML
//    private void refreshEnvi() {
//        // 服务端环境信息
//        List<ZKEnvNode> serverEnviNodes = this.zkClient.enviNodes();
//        this.serverEnvTable.setItem(serverEnviNodes);
//    }
//
//    @FXML
//    private void refreshSrvr() {
//        // 服务信息
//        List<ZKEnvNode> srvrNodes = this.zkClient.srvrNodes();
//        this.srvrTable.setItem(srvrNodes);
//    }
//
//    @FXML
//    private void refreshStat() {
//        // 状态信息
//        List<ZKEnvNode> statNodes = this.zkClient.statNodes();
//        this.statTable.setItem(statNodes);
//    }
//
//    @FXML
//    private void refreshConf() {
//        // 配置信息
//        List<ZKEnvNode> confNodes = this.zkClient.confNodes();
//        this.confTable.setItem(confNodes);
//    }
//
//    @FXML
//    private void refreshCluster() {
//        // 集群信息
//        List<ZKClusterNode> clusterNodes = this.zkClient.clusterNodes();
//        this.clusterTable.setItem(clusterNodes);
//    }
//
//    @Override
//    public void onWindowHidden(WindowEvent event) {
//        super.onWindowHidden(event);
//        this.closeRefreshTask();
//    }
//}
