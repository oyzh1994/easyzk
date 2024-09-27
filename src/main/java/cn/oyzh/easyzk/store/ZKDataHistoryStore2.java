package cn.oyzh.easyzk.store;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.fx.common.exception.InvalidDataException;
import cn.oyzh.fx.common.jdbc.JdbcStore;
import cn.oyzh.fx.common.jdbc.QueryParam;
import cn.oyzh.fx.common.jdbc.SelectParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk搜索历史存储
 *
 * @author oyzh
 * @since 2022/12/16
 */
//@Slf4j
public class ZKDataHistoryStore2 extends JdbcStore<ZKDataHistory> {

    /**
     * 最大历史数量
     */
    public static int His_Max_Size = 50;

    /**
     * 当前实例
     */
    public static final ZKDataHistoryStore2 INSTANCE = new ZKDataHistoryStore2();

    public List<ZKDataHistory> list(String infoId, String path) {
        SelectParam param = new SelectParam();
        param.addQueryParam(new QueryParam("path", path));
        param.addQueryParam(new QueryParam("infoId", infoId));
        param.addQueryColumn("path");
        param.addQueryColumn("infoId");
        param.addQueryColumn("saveTime");
        param.addQueryColumn("dataLength");
        return super.selectList(param);
    }

    public boolean replace(ZKDataHistory model) {
        String path = model.getPath();
        String infoId = model.getInfoId();
        if (StrUtil.isBlank(path) || StrUtil.isBlank(infoId)) {
            throw new InvalidDataException("path", "infoId");
        }
        // 新增数据
        boolean result = super.insert(model);
        // 查询总数
        long count = super.selectCount(new QueryParam("infoId", infoId));
        // 删除超过部分数据
        if (count > His_Max_Size) {
            Map<String, Object> params = new HashMap<>();
            params.put("path", path);
            params.put("infoId", infoId);
            super.delete(params, count - His_Max_Size);
        }
        return result;
    }

    public boolean delete(String infoId, String path) {
        Map<String, Object> params = new HashMap<>();
        params.put("path", path);
        params.put("infoId", infoId);
        return super.delete(params);
    }

    public boolean delete(String infoId) {
        Map<String, Object> params = new HashMap<>();
        params.put("infoId", infoId);
        return super.delete(params);
    }

    @Override
    protected ZKDataHistory newModel() {
        return new ZKDataHistory();
    }

    @Override
    protected Class<ZKDataHistory> modelClass() {
        return ZKDataHistory.class;
    }

    public byte[] getData(String infoId, String path,long saveTime) {
        SelectParam param = new SelectParam();
        param.addQueryParam(new QueryParam("path", path));
        param.addQueryParam(new QueryParam("infoId", infoId));
        param.addQueryParam(new QueryParam("saveTime", saveTime));
        param.addQueryColumn("data");
        ZKDataHistory history = super.selectOne(param);
        if (history != null) {
            return history.getData();
        }
        return null;
    }
}
