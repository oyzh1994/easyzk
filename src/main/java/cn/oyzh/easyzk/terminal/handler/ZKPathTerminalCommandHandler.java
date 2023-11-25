package cn.oyzh.easyzk.terminal.handler;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
import cn.oyzh.easyzk.terminal.ZKTerminalUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.fx.common.util.TextUtil;
import cn.oyzh.fx.terminal.command.TerminalCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/21
 */
public abstract class ZKPathTerminalCommandHandler<C extends TerminalCommand> extends ZKCliTerminalCommandHandler<C> {

    @Override
    public boolean completion(String line, ZKTerminalTextArea terminal) {
        // 获取路径
        String path = ZKTerminalUtil.getPath(line);
        if (!StrUtil.contains(path, "/")) {
            return false;
        }
        try {
            // 父节点路径
            String parentPath = ZKNodeUtil.getParentPath(path);
            // 获取父节点的子节点列表，并判断是否以输入路径为起始内容
            List<String> children = terminal.client().getChildren(parentPath);
            // 节点列表
            List<String> list = new ArrayList<>();
            for (String child : children) {
                String cPath = ZKNodeUtil.concatPath(parentPath, child);
                if (cPath.startsWith(path)) {
                    list.add(child);
                }
            }
            if (list.size() > 1) {
                String text = TextUtil.beautifyFormat(list, 4);
                terminal.outputLine(text);
                terminal.outputPrompt();
                terminal.outputByAppend(line);
                terminal.moveCaretEnd(50);
            } else {
                String newInput = line.substring(0, line.indexOf("/")) + ZKNodeUtil.concatPath(parentPath, list.get(0));
                terminal.coverInput(newInput);
            }
            return true;
        } catch (Exception ex) {
            terminal.onError(ex);
        }
        return false;
    }
}
