package cn.oyzh.easyzk.controller.data;

import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.easyzk.util.ZKI18nHelper;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * zk迁移提醒
 *
 * @author oyzh
 * @since 2024/11/25
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "data/zkMigrationTips.fxml"
)
public class ZKMigrationTipsController extends StageController {

    @FXML
    private FXCheckBox ignoreMigration;

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return ZKI18nHelper.migrationTip6();
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        if (this.ignoreMigration.isSelected()) {
            ZKStoreUtil.ignoreMigration();
        }
    }

    @FXML
    private void close() {
        super.closeWindow();
    }

    @FXML
    private void migration() {
        this.close();
        StageManager.showStage(ZKDataMigrationController.class, StageManager.getPrimaryStage());
    }
}
