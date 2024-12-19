package cn.oyzh.easyzk.vo;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKAuthJdbcStore;
import cn.oyzh.common.Index;
import cn.oyzh.fx.gui.toggle.EnabledToggleSwitch;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * zk认证vo信息
 *
 * @author oyzh
 * @since 2022/6/6
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ZKAuthVO extends ZKAuth implements Index {

    /**
     * 索引
     */
    private int index;

    /**
     * 复制
     *
     * @param auth  zk认证信息
     * @param index 索引
     * @return zk认证vo
     */
    public static ZKAuthVO copy(ZKAuth auth, int index) {
        ZKAuthVO authVO = new ZKAuthVO();
        authVO.copy(auth);
        authVO.setIndex(index);
        return authVO;
    }

    /**
     * 转换
     *
     * @param list zk认证列表
     * @return zk认证vo列表
     */
    public static List<ZKAuthVO> convert(@NonNull List<ZKAuth> list) {
        List<ZKAuthVO> voList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            voList.add(copy(list.get(i), i + 1));
        }
        return voList;
    }

    /**
     * 认证储存
     */
    private final ZKAuthJdbcStore authStore = ZKAuthJdbcStore.INSTANCE;

    /**
     * 状态控件
     */
    public FXToggleSwitch getStatusControl() {
        EnabledToggleSwitch toggleSwitch = new EnabledToggleSwitch();
        toggleSwitch.setFontSize(11);
        toggleSwitch.setSelected(this.getEnable());
        toggleSwitch.selectedChanged((abs, o, n) -> {
            this.setEnable(n);
            if (this.authStore.replace(this)) {
                ZKEventUtil.authEnabled(this);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        });
        return toggleSwitch;
    }
}
