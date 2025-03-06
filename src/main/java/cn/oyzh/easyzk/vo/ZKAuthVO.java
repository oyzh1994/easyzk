package cn.oyzh.easyzk.vo;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.toggle.EnabledToggleSwitch;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * zk认证vo信息
 *
 * @author oyzh
 * @since 2022/6/6
 */
public class ZKAuthVO extends ZKAuth {

    /**
     * 复制
     *
     * @param auth zk认证信息
     * @return zk认证vo
     */
    public static ZKAuthVO convert(ZKAuth auth) {
        ZKAuthVO authVO = new ZKAuthVO();
        authVO.copy(auth);
        return authVO;
    }

    /**
     * 转换
     *
     * @param list zk认证列表
     * @return zk认证vo列表
     */
    public static List<ZKAuthVO> convert(@NonNull List<ZKAuth> list) {
        List<ZKAuthVO> voList = new ArrayList<>(list.size());
        for (ZKAuth zkAuth : list) {
            voList.add(convert(zkAuth));
        }
        return voList;
    }

    /**
     * 用户名控件
     */
    public ClearableTextField getUserControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setFlexWidth("100% - 12");
        textField.setValue(this.getUser());
        textField.addTextChangeListener((obs, o, n) -> this.setUser(n));
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 密码控件
     */
    public ClearableTextField getPasswordControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setFlexWidth("100% - 12");
        textField.setValue(this.getPassword());
        textField.addTextChangeListener((obs, o, n) -> this.setPassword(n));
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /**
     * 状态控件
     */
    public FXToggleSwitch getStatusControl() {
        EnabledToggleSwitch toggleSwitch = new EnabledToggleSwitch();
        toggleSwitch.setFontSize(11);
        toggleSwitch.setSelected(this.getEnable());
        toggleSwitch.selectedChanged((abs, o, n) -> {
            this.setEnable(n);
        });
        return toggleSwitch;
    }
}
