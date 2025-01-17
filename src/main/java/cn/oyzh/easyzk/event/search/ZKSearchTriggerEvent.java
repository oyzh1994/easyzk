package cn.oyzh.easyzk.event.search;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 搜索触发事件
 *
 * @author oyzh
 * @since 2025/01/17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = false, fluent = true)
public class ZKSearchTriggerEvent extends Event<ZKConnect> {

    private ZKSearchParam param;

}
