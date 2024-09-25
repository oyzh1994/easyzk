package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.fx.common.sqlite.SqliteStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk信息存储
 *
 * @author oyzh
 * @since 2020/5/23
 */
public class ZKInfoStore2 extends SqliteStore<ZKInfo> {

    @Override
    protected ZKInfo newModel() {
        return null;
    }

    @Override
    protected Class<ZKInfo> modelClass() {
        return null;
    }
}
