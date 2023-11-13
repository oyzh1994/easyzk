package cn.oyzh.easyzk.controller;


import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.common.spring.SpringUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * 关于业务
 *
 * @author oyzh
 * @since 2020/10/26
 */
@StageAttribute(
        resizeable = false,
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "about.fxml"
)
public class AboutController extends Controller {

    @FXML
    private FlexText name;

    @FXML
    private FlexText version;

    @FXML
    private FlexText updateDate;

    @FXML
    private FlexText copyright;

    /**
     * 项目信息
     */
    private final Project project = SpringUtil.getBean(Project.class);

    @Override
    public void onStageShown(WindowEvent event) {
        this.name.setText(this.project.getName());
        this.version.setText("v" + this.project.getVersion());
        this.updateDate.setText(this.project.getUpdateDate());
        this.copyright.setText(this.project.getCopyright());
        this.stage.hideOnEscape();
    }
}
