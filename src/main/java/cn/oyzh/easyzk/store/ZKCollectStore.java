package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.domain.ZKCollect;
import cn.oyzh.fx.common.sqlite.QueryParam;
import cn.oyzh.fx.common.sqlite.SqliteStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKCollectStore extends SqliteStore<ZKCollect> {

    /**
     * 当前实例
     */
    public static final ZKCollectStore INSTANCE = new ZKCollectStore();

    public List<String> list(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        List<ZKCollect> collects = super.selectList(param);
        if (CollUtil.isNotEmpty(collects)) {
            return collects.parallelStream().map(ZKCollect::getPath).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean replace(ZKCollect model) {
        if (model != null && !this.exist(model.getIid(), model.getPath())) {
            return this.insert(model);
        }
        return false;
    }

    public boolean delete(String iid, String path) {
        if (StrUtil.isEmpty(iid) || StrUtil.isEmpty(path)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("path", path);
            return this.delete(params);
        }
        return false;
    }

    public boolean exist(String iid, String path) {
        if (StrUtil.isNotBlank(iid) && StrUtil.isNotBlank(path)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("path", path);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected ZKCollect newModel() {
        return new ZKCollect();
    }

    @Override
    protected Class<ZKCollect> modelClass() {
        return ZKCollect.class;
    }
}
