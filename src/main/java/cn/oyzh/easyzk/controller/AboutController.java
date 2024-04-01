package cn.oyzh.easyzk.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import javax.annotation.Resource;

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
        value = ZKConst.FXML_BASE_PATH + "about.fxml"
)
public class AboutController extends Controller {

    @FXML
    private FlexText name;

    @FXML
    private FlexText type;

    @FXML
    private FlexText version;

    @FXML
    private FlexText updateDate;

    @FXML
    private FlexText copyright;

    /**
     * 项目信息
     */
    @Resource
    private Project project;

    @Override
    public void onStageShown(WindowEvent event) {
        // 当舞台被显示时，设置名称文本框的文本为项目名称
        this.name.setText(this.project.getName());
        // 设置版本文本框的文本为项目版本号
        this.version.setText("v" + this.project.getVersion());
        // 设置更新日期文本框的文本为项目的更新日期
        this.updateDate.setText(this.project.getUpdateDate());
        // 设置版权文本框的文本为项目的版权信息
        this.copyright.setText(this.project.getCopyright());
        this.type.setText(StrUtil.equals(this.project.getType(), "build") ? "每日构建版" : "正式发布版");
        // 设置标题
        this.stage.setTitleExt("关于" + this.project.getName());
        this.stage.hideOnEscape();
    }
}
