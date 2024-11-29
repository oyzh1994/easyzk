package cn.oyzh.easyzk.terminal.fourlw;

import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKDirsCommandHandler extends ZKFourLetterWordCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final ZKFourLetterWordCommand furLetterWordCommand = new ZKDirsCommand();

    @Override
    public String commandDesc() {
        return I18nResourceBundle.i18nString("base.add", "base.auth");
    }

}
