package cn.oyzh.easyzk.controller.tool;

import cn.oyzh.common.util.NumberUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;


/**
 * zk工具箱业务
 *
 * @author oyzh
 * @since 2023/11/09
 */
@StageAttribute(
        modality = Modality.WINDOW_MODAL,
        value = FXConst.FXML_PATH + "tool/zkTool.fxml"
)
public class ZKToolController extends StageController {

    /**
     * 用户
     */
    @FXML
    private ClearableTextField user;

    /**
     * 密码
     */
    @FXML
    private ClearableTextField pwd;

    /**
     * 摘要
     */
    @FXML
    private TextField digest;

    /**
     * 生成摘要
     */
    @FXML
    private void genDigest() {
        try {
            if(StringUtil.isBlank(this.user.getText())){
                this.user.requestFocus();
                return;
            }
            if(StringUtil.isBlank(this.pwd.getText())){
                this.pwd.requestFocus();
                return;
            }
            String digest1 = ZKAuthUtil.digest(this.user.getText(), this.pwd.getText());
            this.digest.setText(digest1);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 复制摘要
     */
    @FXML
    private void copyDigest() {
        this.digest.copy();
        MessageBox.info(I18nHelper.copySuccess());
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
        this.user.addTextChangeListener((observableValue, s, t1) -> {
            // 内容包含“:”，则直接切割字符为用户名密码
            if (t1 != null && t1.contains(":")) {
                this.user.setText(t1.split(":")[0]);
                this.pwd.setText(t1.split(":")[1]);
            } else {
                this.digest.clear();
            }
        });
        this.pwd.addTextChangeListener((observableValue, s, t1) -> this.digest.clear());
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.tools();
    }

    /**
     * 缓存文本域
     */
    @FXML
    private FlexTextArea cacheArea;

    /**
     * 计算缓存
     */
    @FXML
    private void calcCache() {
        this.disable();
        try {
            this.cacheArea.setText("calc cache start.");
            File dir = new File(ZKConst.CACHE_PATH);
            this.doCalcCache(dir, new AtomicInteger(0), new LongAdder());
            this.cacheArea.appendLine("calc cache finish.");
        } finally {
            this.enable();
        }
    }

    /**
     * 计算缓存
     *
     * @param file      文件
     * @param fileCount 文件总数
     * @param fileSize  文件大小
     */
    private void doCalcCache(File file, AtomicInteger fileCount, LongAdder fileSize) {
        if (file.isFile()) {
            fileSize.add(file.length());
            fileCount.incrementAndGet();
            String sizeInfo = NumberUtil.formatSize(fileSize.longValue());
            this.cacheArea.setText("find file: " + fileCount.get() + " total size: " + sizeInfo);
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    this.doCalcCache(file1, fileCount, fileSize);
                }
            }
        }
    }

    /**
     * 清理缓存
     */
    @FXML
    private void clearCache() {
        try {
            this.cacheArea.setText("clear cache start.");
            File dir = new File(ZKConst.CACHE_PATH);
            this.doClearCache(dir);
            this.cacheArea.appendLine("clear cache finish.");
        } finally {
            this.enable();
        }
    }

    /**
     * 清理缓存
     *
     * @param file 文件
     */
    private void doClearCache(File file) {
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    this.doClearCache(file1);
                }
            }
        }
    }
}
