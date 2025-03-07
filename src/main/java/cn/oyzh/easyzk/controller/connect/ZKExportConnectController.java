package cn.oyzh.easyzk.controller.connect;

import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.dto.ZKConnectExport;
import cn.oyzh.easyzk.store.ZKConnectStore;
import cn.oyzh.easyzk.store.ZKGroupStore;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.chooser.FXChooser;
import cn.oyzh.fx.plus.chooser.FileChooserHelper;
import cn.oyzh.fx.plus.chooser.FileExtensionFilter;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;

import java.io.File;
import java.util.List;


/**
 * zk连接导出业务
 *
 * @author oyzh
 * @since 2025/02/21
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "connect/zkExportConnect.fxml"
)
public class ZKExportConnectController extends StageController {

    /**
     * 导出文件
     */
    private File exportFile;

    /**
     * 文件名
     */
    @FXML
    private FXText fileName;

    /**
     * 包含分组
     */
    @FXML
    private FXCheckBox includeGroup;

    /**
     * 分组存储
     */
    private final ZKGroupStore groupStore = ZKGroupStore.INSTANCE;

    /**
     * 连接存储
     */
    private final ZKConnectStore connectStore = ZKConnectStore.INSTANCE;

    /**
     * 执行导出
     */
    @FXML
    private void doExport() {
        List<ZKConnect> connects = this.connectStore.loadFull();
        if (CollectionUtil.isEmpty(connects)) {
            MessageBox.warn(I18nHelper.connectionIsEmpty());
            return;
        }
        if (this.exportFile == null) {
            MessageBox.warn(I18nHelper.pleaseSelectFile());
            return;
        }
        ZKConnectExport export = ZKConnectExport.fromConnects(connects);
        // 分组
        if (this.includeGroup.isSelected()) {
            export.setGroups(this.groupStore.load());
        }
        try {
            FileUtil.writeUtf8String(export.toJSONString(), this.exportFile);
            MessageBox.okToast(I18nHelper.exportConnectionSuccess());
            this.closeWindow();
        } catch (Exception ex) {
            MessageBox.exception(ex, I18nHelper.exportConnectionFail());
        }
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.exportConnect();
    }

    /**
     * 选择文件
     */
    @FXML
    private void selectFile() {
        FileExtensionFilter filter = FXChooser.jsonExtensionFilter();
        String fileName = "Zookeeper-" + I18nHelper.connect() + ".json";
        this.exportFile = FileChooserHelper.save(fileName, fileName, filter);
        if (this.exportFile != null) {
            this.fileName.setText(this.exportFile.getPath());
        } else {
            this.fileName.clear();
        }
    }
}
