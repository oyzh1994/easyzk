package cn.oyzh.easyzk.query;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.terminal.TerminalTextArea;
import cn.oyzh.fx.terminal.command.TerminalCommandHandler;
import cn.oyzh.fx.terminal.util.TerminalManager;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author oyzh
 * @since 2025/01/21
 */
public class ZKQueryTextArea extends TerminalTextArea {

    @Getter
    @Setter
    private ZKClient client;

    /**
     * 提示词组件
     */
    private final ZKQueryPromptPopup promptPopup = new ZKQueryPromptPopup();

    {
        this.showLineNum();
        this.setOnMouseReleased(e -> this.promptPopup.hide());
//        this.addTextChangeListener((observable, oldValue, newValue) -> this.initTextStyle());
        this.promptPopup.setOnItemSelected(item -> this.promptPopup.autoComplete(this, item));
        this.focusedProperty().addListener((observable, oldValue, newValue) -> this.promptPopup.hide());
        this.setOnKeyReleased(event -> this.promptPopup.prompt(this, event));
    }


    @Override
    protected void init() {
        super.init();
        this.initContentPrompts();
    }

    @Override
    protected void initFont() {
        // 禁用字体管理
        super.disableFont();
        // 初始化字体
        ZKSetting setting = ZKSettingStore.SETTING;
        this.setFontSize(setting.getQueryFontSize());
        this.setFontFamily(setting.getQueryFontFamily());
        this.setFontWeight2(setting.getQueryFontWeight());
    }

    @Override
    protected void initContentPrompts() {
        // 设置内容提示符
        Set<String> set = ZKQueryUtil.getKeywords();
        set.addAll(ZKQueryUtil.getParams());
        this.setContentPrompts(set);
    }
}
