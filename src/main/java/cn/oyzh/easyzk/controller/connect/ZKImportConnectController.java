package cn.oyzh.easyzk.controller.connect;

import cn.oyzh.common.file.FileNameUtil;
import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.dto.ZKConnectExport;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKConnectStore;
import cn.oyzh.easyzk.store.ZKGroupStore;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXButton;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.file.FXChooser;
import cn.oyzh.fx.plus.file.FileChooserHelper;
import cn.oyzh.fx.plus.file.FileExtensionFilter;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.List;


/**
 * zk连接导入业务
 *
 * @author oyzh
 * @since 2025/02/21
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "connect/zkImportConnect.fxml"
)
public class ZKImportConnectController extends StageController {

    /**
     * 导入文件
     */
    private File importFile;

    /**
     * 文件名
     */
    @FXML
    private FXText fileName;

    /**
     * 选择文件
     */
    @FXML
    private FXButton selectFile;

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
     * 执行导入
     */
    @FXML
    private void doImport() {
        try {
            String text = FileUtil.readUtf8String(this.importFile);
            ZKConnectExport export = ZKConnectExport.fromJSON(text);
            List<ZKConnect> connects = export.getConnects();
            boolean success = true;
            if (CollectionUtil.isNotEmpty(connects)) {
                for (ZKConnect connect : connects) {
                    if (!this.connectStore.replace(connect)) {
                        success = false;
                    }
                }
            }
            List<ZKGroup> groups = export.getGroups();
            if (this.includeGroup.isSelected() && CollectionUtil.isNotEmpty(groups)) {
                for (ZKGroup group : groups) {
                    if (!this.groupStore.replace(group)) {
                        success = false;
                    }
                }
            }
            if (success) {
                MessageBox.okToast(I18nHelper.importConnectionSuccess());
                ZKEventUtil.connectImported();
                this.closeWindow();
            } else {
                MessageBox.warn(I18nHelper.importConnectionFail());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, I18nHelper.importConnectionFail());
        }
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.importConnect();
    }

    /**
     * 选择文件
     */
    @FXML
    private void selectFile() {
        FileExtensionFilter filter = FXChooser.jsonExtensionFilter();
        this.importFile = FileChooserHelper.choose(I18nHelper.pleaseSelectFile(), filter);
        this.parseFile();
    }

    private void parseFile() {
        if (this.importFile == null) {
            this.fileName.clear();
            return;
        }
        this.fileName.setText(this.importFile.getPath());
        if (!this.importFile.exists()) {
            MessageBox.warn(I18nHelper.fileNotExists());
            return;
        }
        if (this.importFile.isDirectory()) {
            MessageBox.warn(I18nHelper.notSupportFolder());
            return;
        }
        if (!FileNameUtil.isJsonType(FileNameUtil.extName(this.importFile.getName()))) {
            MessageBox.warn(I18nHelper.invalidFormat());
            return;
        }
        if (this.importFile.length() == 0) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.importFile = this.getWindowProp("file");
        if (this.importFile != null) {
            this.selectFile.disable();
            this.parseFile();
        }
    }
}
