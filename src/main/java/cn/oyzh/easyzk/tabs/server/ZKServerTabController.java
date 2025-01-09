package cn.oyzh.easyzk.tabs.server;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.ExecutorUtil;
import cn.oyzh.easyzk.dto.ZKEnvNode;
import cn.oyzh.easyzk.vo.ZKServerInfo;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.gui.tabs.ParentTabController;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.concurrent.Future;


/**
 * zk配置信息业务
 *
 * @author oyzh
 * @since 2022/08/25
 */
public class ZKServerTabController extends ParentTabController {

    /**
     * zk客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private ZKClient client;

    /**
     * 服务信息
     */
    @FXML
    private FlexTableView<ZKServerInfo> serverTable;

    /**
     * 延迟信息
     */
    @FXML
    private FlexTableColumn<ZKServerInfo, String> latency;

    /**
     * 命令信息
     */
    @FXML
    private FlexTableColumn<ZKServerInfo, String> command;

    /**
     * 汇总信息
     */
    @FXML
    private ZKAggregationController aggregationController;

    /**
     * 服务信息
     */
    @FXML
    private ZKSrvrController srvrController;

    /**
     * 状态信息
     */
    @FXML
    private ZKStatController statController;

    /**
     * 本地信息
     */
    @FXML
    private ZKLocalController localController;

    /**
     * 配置信息
     */
    @FXML
    private ZKConfController confController;

    /**
     * 环境信息
     */
    @FXML
    private ZKEnviController enviController;

    /**
     * 集群信息
     */
    @FXML
    private ZKClusterController clusterController;

    /**
     * 刷新任务
     */
    private Future<?> refreshTask;

    /**
     * 设置zk客户端
     *
     * @param client zk客户端
     */
    public void init(ZKClient client) {
        this.client = client;
        // 设置信息
        String command = this.command.getText() + "(" + I18nHelper.received() + "/" + I18nHelper.sent() + "/" + I18nHelper.outstanding() + ")";
        this.command.setText(command);
        String latency = this.latency.getText() + "(" + I18nHelper.min() + "/" + I18nHelper.avg() + "/" + I18nHelper.max() + ")" + I18nHelper.millisecond();
        this.latency.setText(latency);
        // 初始化刷新任务
        this.initRefreshTask();
    }

    /**
     * 初始化自动刷新任务
     */
    private void initRefreshTask() {
        this.refreshTask = ExecutorUtil.start(this::renderPane, 0, 3_000);
        JulLog.debug("RefreshTask started.");
    }

    /**
     * 关闭自动刷新任务
     */
    public void closeRefreshTask() {
        try {
            ExecutorUtil.cancel(this.refreshTask);
            JulLog.debug("RefreshTask closed.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.error("closeRefreshTask error", ex);
        }
    }

    /**
     * 渲染主面板
     */
    private void renderPane() {
        try {
            // 服务信息
            ZKServerInfo serverInfo;
            // 初始化
            if (this.serverTable.isItemEmpty()) {
                serverInfo = new ZKServerInfo();
                this.serverTable.setItem(serverInfo);
            } else {// 获取
                serverInfo = (ZKServerInfo) this.serverTable.getItem(0);
            }
            // 获取信息
            List<ZKEnvNode> envNodes = this.client.srvrNodes();
            // 更新信息
            serverInfo.update(envNodes);
            // 初始化图表
            this.aggregationController.init(serverInfo);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.error("renderPane error", ex);
        }
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        super.onTabClose(tab, event);
        this.closeRefreshTask();
    }

    @Override
    public List<? extends DynamicTabController> getSubControllers() {
        return List.of(this.aggregationController, this.localController, this.srvrController,
                this.statController, this.clusterController, this.localController,
                this.confController, this.enviController);
    }
}