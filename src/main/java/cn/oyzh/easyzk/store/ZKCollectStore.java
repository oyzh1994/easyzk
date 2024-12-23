package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKCollect;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * zk收藏存储
 *
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKCollectStore extends JdbcStandardStore<ZKCollect> {

    /**
     * 当前实例
     */
    public static final ZKCollectStore INSTANCE = new ZKCollectStore();

    /**
     * 根据zk连接id加载列表
     *
     * @param iid zk连接id
     * @return 收藏列表
     */
    public List<String> list(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        List<ZKCollect> collects = super.selectList(param);
        if (CollectionUtil.isNotEmpty(collects)) {
            return collects.parallelStream().map(ZKCollect::getPath).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 替换
     *
     * @param iid  zk连接id
     * @param path zk路径
     * @return 结果
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public boolean replace(String iid, String path) {
        return this.replace(new ZKCollect(iid, path));
    }

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
    public boolean replace(ZKCollect model) {
        if (model != null && !this.exist(model.getIid(), model.getPath())) {
            return this.insert(model);
        }
        return false;
    }

    /**
     * 根据zk连接id删除收藏
     *
     * @param iid zk连接id
     * @return 结果
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public boolean deleteByIid(String iid) {
        if (StringUtil.isEmpty(iid)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            return this.delete(params);
        }
        return false;
    }

    /**
     * 根据zk连接id和路径删除收藏
     *
     * @param iid  zk连接id
     * @param path zk路径
     * @return 结果
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public boolean delete(String iid, String path) {
        if (StringUtil.isEmpty(iid) && StringUtil.isEmpty(path)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("path", path);
            return this.delete(params);
        }
        return false;
    }

    /**
     * 是否存在
     *
     * @param iid  zk连接id
     * @param path zk路径
     * @return 结果
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public boolean exist(String iid, String path) {
        if (StringUtil.isNotBlank(iid) && StringUtil.isNotBlank(path)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("path", path);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected Class<ZKCollect> modelClass() {
        return ZKCollect.class;
    }
}
