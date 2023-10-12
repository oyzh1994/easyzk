package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controls.FlexComboBox;

import java.util.List;

/**
 * 连接选择框
 *
 * @author oyzh
 * @since 2023/04/08
 */
public class ConnectComboBox extends FlexComboBox<ZKInfo> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(ZKInfo o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
        List<ZKInfo> infos = ZKInfoStore.INSTANCE.load();
        this.getItems().setAll(infos);
    }
}
