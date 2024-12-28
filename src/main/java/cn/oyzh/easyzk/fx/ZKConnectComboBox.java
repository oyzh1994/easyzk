package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.store.ZKConnectStore;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

import java.util.List;

/**
 * 连接选择框
 *
 * @author oyzh
 * @since 2023/04/08
 */
public class ZKConnectComboBox extends FlexComboBox<ZKConnect> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(ZKConnect o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
        List<ZKConnect> connects = ZKConnectStore.INSTANCE.load();
        this.setItem(connects);
    }
}
