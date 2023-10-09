package cn.oyzh.easyzk.msg;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.zk.ZKClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.data.Stat;

import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKNodeUpdatedMsg implements ZKMsg {

    private final String name = ZKEventTypes.ZK_NODE_UPDATED;

    private final String group = ZKEventGroups.NODE_MSG;

    @Setter
    private Stat stat;

    @Setter
    private String path;

    @Setter
    private byte[] data;

    @Setter
    private ZKClient client;

    public String decodeNodePath() {
        if (StrUtil.containsAny(this.path, "%", "+")) {
            return URLDecoder.decode(this.path, Charset.defaultCharset());
        }
        return this.path;
    }

    public ZKInfo info(){
        return this.client.zkInfo();
    }
}
