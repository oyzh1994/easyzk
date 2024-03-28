package cn.oyzh.easyzk.controller.info;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.fx.ConnectComboBox;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKFilterStore;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.Counter;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.common.util.TextUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.combo.CharsetComboBox;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.handler.StateManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * zk数据传输业务
 *
 * @author oyzh
 * @since 2023/04/07
 */
@StageAttribute(
        title = "数据传输",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        value = ZKConst.FXML_BASE_PATH + "info/zkInfoTransport.fxml"
)
public class ZKInfoTransportController extends Controller {

    /**
     * 状态管理器
     */
    @FXML
    private StateManager stateManager;

    /**
     * 节点已存在时跳过
     */
    @FXML
    private FlexCheckBox existHandle;

    /**
     * 适用过滤配置
     */
    @FXML
    private FlexCheckBox applyFilter;

    /**
     * 传输按钮
     */
    @FXML
    private FlexButton transportBtn;

    /**
     * 结束传输按钮
     */
    @FXML
    private FlexButton stopTransportBtn;

    /**
     * 传输状态
     */
    @FXML
    private FXLabel transportStatus;

    /**
     * 传输消息
     */
    @FXML
    private MsgTextArea transportMsg;

    /**
     * 来源连接
     */
    @FXML
    private ConnectComboBox formConnect;

    /**
     * 来源信息
     */
    private ZKInfo formInfo;

    /**
     * 传输字符集
     */
    @FXML
    private CharsetComboBox formCharset;

    /**
     * 目标连接
     */
    @FXML
    private ConnectComboBox targetConnect;

    /**
     * 目标字符集
     */
    @FXML
    private CharsetComboBox targetCharset;

    /**
     * 当前传输zk对象
     */
    private ZKClient formClient;

    /**
     * 当前目标zk对象
     */
    private ZKClient targetClient;

    /**
     * 传输操作任务
     */
    private Thread exportTask;

    /**
     * 过滤内容列表
     */
    private List<ZKFilter> filters;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 过滤配置储存
     */
    private final ZKFilterStore filterStore = ZKFilterStore.INSTANCE;

    /**
     * 执行传输
     */
    @FXML
    private void doTransport() {
        // 检查连接
        if (this.formConnect.getValue() == null) {
            MessageBox.tipMsg("请选择一个传输连接", this.formConnect);
            return;
        }
        if (this.targetConnect.getValue() == null) {
            MessageBox.tipMsg("请选择一个目标连接", this.formConnect);
            return;
        }
        ZKInfo formInfo = this.formConnect.getValue();
        ZKInfo targetInfo = this.targetConnect.getValue();
        if (formInfo == targetInfo) {
            MessageBox.tipMsg("传输目标不能是自己", this.formConnect);
            return;
        }

        // 开始传输
        this.transportStart();
        // 检查传输连接
        if (this.formClient == null || !this.formClient.isConnected() || this.formClient.zkInfo() != formInfo) {
            try {
                this.transportMsg.appendLine("传输连接初始化中...");
                if (this.formClient != null) {
                    this.formClient.close();
                }
                this.formClient = new ZKClient(formInfo);
                this.stage.appendTitle("===传输连接初始化===");
                this.formClient.start();
                if (!this.formClient.isConnected()) {
                    this.formConnect.requestFocus();
                    MessageBox.warn("传输连接[" + formInfo.getName() + "]初始化失败，连接异常");
                    return;
                }
            } finally {
                this.transportEnd();
            }
        }

        // 检查目标连接
        if (this.targetClient == null || !this.targetClient.isConnected() || this.targetClient.zkInfo() != formInfo) {
            try {
                this.transportMsg.appendLine("目标连接初始化中...");
                if (this.targetClient != null) {
                    this.targetClient.close();
                }
                this.targetClient = new ZKClient(targetInfo);
                this.stage.appendTitle("===目标连接初始化===");
                this.targetClient.start();
                if (!this.targetClient.isConnected()) {
                    this.targetConnect.requestFocus();
                    MessageBox.warn("目标连接[" + targetInfo.getName() + "]初始化失败，连接异常");
                    return;
                }
            } finally {
                this.transportEnd();
            }
        }

        // 重置参数
        this.counter.reset();
        // 开始传输
        this.transportStart();
        this.stage.appendTitle("===传输执行中===");
        this.transportMsg.appendLine("传输即将开始...");
        // 执行传输
        this.exportTask = ThreadUtil.start(() -> {
            this.stopTransportBtn.enable();
            try {
                // 适用过滤
                if (this.applyFilter.isSelected()) {
                    this.filters = this.filterStore.loadEnable();
                }
                this.transport("/");
                this.updateStatus("数据传输收尾中....");
                // this.transportMsg.waitTextExpend();
                this.updateStatus("数据传输结束");
                MessageBox.okToast("传输数据结束！");
            } catch (Exception ex) {
                if (ex.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus("数据传输取消");
                    MessageBox.okToast("传输数据取消！");
                } else {
                    ex.printStackTrace();
                    this.updateStatus("数据传输失败");
                    MessageBox.exception(ex, "传输数据失败！");
                }
            } finally {
                // 结束传输
                this.transportEnd();
                this.closeZKClient();
            }
        });
    }

    /**
     * 传输开始
     */
    private void transportStart() {
        this.stateManager.disable();
        this.transportMsg.clear();
        // this.existHandle.disable();
        // this.transportBtn.disable();
        // this.applyFilter.disable();
        // this.formCharset.disable();
        // this.targetCharset.disable();
        // this.targetConnect.disable();
        this.formConnect.disable();
    }

    /**
     * 传输结束
     */
    private void transportEnd() {
        this.stateManager.enable();
        // this.existHandle.enable();
        // this.transportBtn.enable();
        // this.applyFilter.enable();
        // this.formCharset.enable();
        // this.targetCharset.enable();
        // this.targetConnect.enable();
        this.stopTransportBtn.disable();
        this.stage.restoreTitle();
        SystemUtil.gcLater();
        if (this.formInfo == null) {
            this.formConnect.enable();
        }
    }

    /**
     * 关闭zk客户端
     */
    private void closeZKClient() {
        if (this.formClient != null) {
            this.formClient.close();
            // this.formClient = null;
        }
        if (this.targetClient != null) {
            this.targetClient.close();
            // this.targetClient = null;
        }
    }

    /**
     * 结束传输
     */
    @FXML
    private void stopTransport() {
        ThreadUtil.interrupt(this.exportTask);
        this.exportTask = null;
    }

    @Override
    public void onStageShown(WindowEvent event) {
        // 来源连接不为null，则禁用来源选项
        this.formInfo = this.stage.getProp("formConnect");
        if (this.formInfo != null) {
            this.formConnect.select(this.formInfo);
            this.formConnect.disable();
        } else {// 开启来源选项
            this.formConnect.selectedItemChanged((observableValue, zkInfo, t1) -> {
                if (t1 == null || this.targetConnect.getValue() == t1) {
                    this.transportBtn.disable();
                } else if (this.targetConnect.getValue() != null && this.targetConnect.getValue() != t1) {
                    this.transportBtn.enable();
                }
                // if (t1 != null) {
                //     this.formCharset.select(t1.getCharset());
                // }
            });
        }
        this.targetConnect.selectedItemChanged((observableValue, zkInfo, t1) -> {
            if (t1 == null || this.formConnect.getValue() == t1) {
                this.transportBtn.disable();
            } else if (this.formConnect.getValue() != null && this.formConnect.getValue() != t1) {
                this.transportBtn.enable();
            }
            // if (t1 != null) {
            //     this.targetCharset.select(t1.getCharset());
            // }
        });
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        this.transportEnd();
        this.stopTransport();
        this.closeZKClient();
    }

    /**
     * 执行传输
     *
     * @param path 节点路径
     */
    private void transport(@NonNull String path) {
        try {
            // 取消操作
            if (ThreadUtil.isInterrupted(this.exportTask)) {
                return;
            }
            // 获取节点
            Stat stat = this.formClient.checkExists(path);
            byte[] bytes = this.formClient.getData(path);

            // 节点查询失败
            if (stat == null || bytes == null) {
                this.updateStatus(path, 0, null);
                return;
            }

            // 临时节点跳过
            if (stat.getEphemeralOwner() > 0) {
                return;
            }

            // 适用过滤
            if (this.applyFilter.isSelected() && ZKNodeUtil.isFiltered(path, this.filters)) {
                this.updateStatus(path, 3, null);
                return;
            }

            // 节点存在
            if (this.targetClient.exists(path)) {
                // 节点已存在时，跳过节点
                if (this.existHandle.isSelected()) {
                    this.updateStatus(path, 2, null);
                } else { // 设置数据
                    // 转换字符集
                    bytes = TextUtil.changeCharset(bytes, this.formCharset.getCharsetName(), this.targetCharset.getCharsetName());
                    this.targetClient.setData(path, bytes);
                    this.updateStatus(path, 1, null);
                }
            } else {// 创建节点
                this.targetClient.createIncludeParents(path, bytes, CreateMode.PERSISTENT);
                this.updateStatus(path, 1, null);
            }
            // 获取子节点
            List<String> subs = this.formClient.getChildren(path);
            // 递归传输节点
            for (String sub : subs) {
                if (ThreadUtil.isInterrupted(this.exportTask)) {
                    break;
                }
                this.transport(ZKNodeUtil.concatPath(path, sub));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.updateStatus(path, 0, ex);
        }
    }

    /**
     * 更新状态
     *
     * @param path   路径
     * @param status 状态 0:失败 1:成功 2:节点已存在 3:适用过滤
     * @param ex     异常信息
     */
    private void updateStatus(String path, int status, Exception ex) {
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        if (status == 1) {
            this.counter.update(1);
            this.transportMsg.appendLine("传输节点：" + path + " 成功");
        } else if (status == 2) {
            this.counter.update(2);
            this.transportMsg.appendLine("传输节点：" + path + " 已忽略，此节点已存在");
        } else if (status == 3) {
            this.counter.update(2);
            this.transportMsg.appendLine("传输节点：" + path + " 已忽略，此节点适用过滤配置");
        } else {
            this.counter.update(0);
            String msg = "传输节点：" + path + " 失败";
            if (ex != null) {
                msg += "，错误信息：" + ZKExceptionParser.INSTANCE.apply(ex);
            }
            this.transportMsg.appendLine(msg);
        }
        this.updateStatus(null);
    }

    /**
     * 更新状态
     *
     * @param extraMsg 额外信息
     */
    private void updateStatus(String extraMsg) {
        if (extraMsg != null) {
            this.counter.setExtraMsg(extraMsg);
        }
        FXUtil.runLater(() -> this.transportStatus.setText(this.counter.unknownFormat()));
    }
}
