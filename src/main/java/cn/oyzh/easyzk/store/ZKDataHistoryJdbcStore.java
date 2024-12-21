package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.common.exception.InvalidDataException;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.OrderByParam;
import cn.oyzh.store.jdbc.QueryParam;
import cn.oyzh.store.jdbc.SelectParam;
import cn.oyzh.common.util.MD5Util;
import cn.oyzh.common.util.StringUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * zk搜索历史存储
 *
 * @author oyzh
 * @since 2022/12/16
 */
//@Slf4j
public class ZKDataHistoryJdbcStore extends JdbcStandardStore<ZKDataHistory> {

    /**
     * 最大历史数量
     */
    public static int His_Max_Size = 50;

    /**
     * 当前实例
     */
    public static final ZKDataHistoryJdbcStore INSTANCE = new ZKDataHistoryJdbcStore();

    /**
     * 服务端路径
     */
    private static final String SERVER_PATH = "/_dataHistory/";

    public List<ZKDataHistory> listLocal(String infoId, String path) {
        SelectParam param = new SelectParam();
        param.addQueryParam(new QueryParam("infoId", infoId));
        param.addQueryParam(new QueryParam("path", MD5Util.md5Hex(path)));
        param.addQueryColumn("infoId");
        param.addQueryColumn("saveTime");
        param.addQueryColumn("dataLength");
        List<ZKDataHistory> histories = super.selectList(param).reversed();
        return histories.parallelStream().sorted(Comparator.comparingLong(ZKDataHistory::getSaveTime)).collect(Collectors.toList()).reversed();
    }

    public List<ZKDataHistory> listServer(String infoId, String path, ZKClient client) {
        try {
            String dataPath = SERVER_PATH + infoId + "/" + MD5Util.md5Hex(path);
            if (client.exists(dataPath)) {
                List<ZKDataHistory> histories = new ArrayList<>();
                for (String node : client.getChildren(dataPath)) {
                    ZKDataHistory history = new ZKDataHistory();
                    history.setInfoId(infoId);
                    Stat stat = client.checkExists(dataPath + "/" + node);
                    history.setDataLength(stat.getDataLength());
                    try {
                        history.setSaveTime(Long.parseLong(node));
                    } catch (Exception ex) {
                        history.setSaveTime(stat.getMtime());
                    }
                    histories.add(history);
                }
                return histories.parallelStream().sorted(Comparator.comparingLong(ZKDataHistory::getSaveTime)).collect(Collectors.toList()).reversed();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean replace(ZKDataHistory model, ZKClient client) {
        String path = model.getPath();
        String infoId = model.getInfoId();
        if (StringUtil.isBlank(path) || StringUtil.isBlank(infoId)) {
            throw new InvalidDataException("path", "infoId");
        }
        try {
            model.setPath(MD5Util.md5Hex(path));
            this.doLocalReplace(model);
            this.doServerReplace(model, client);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void doLocalReplace(ZKDataHistory model) {
        String path = model.getPath();
        String infoId = model.getInfoId();
        // 新增数据
        super.insert(model);
        // 查询总数
        List<QueryParam> queryParams = new ArrayList<>();
        queryParams.add(new QueryParam("path", path));
        queryParams.add(new QueryParam("infoId", infoId));
        long count = super.selectCount(queryParams);
        // 删除超出限制的节点
        if (count > His_Max_Size) {
            DeleteParam deleteParam = new DeleteParam();
            deleteParam.addQueryParams(queryParams);
            deleteParam.addOrderByParam(new OrderByParam("saveTime", "desc"));
            deleteParam.setLimit(1L);
            super.delete(deleteParam);
        }
    }

    private void doServerReplace(ZKDataHistory model, ZKClient client) throws Exception {
        String path = model.getPath();
        String infoId = model.getInfoId();
        // 添加节点
        long saveTime = model.getSaveTime();
        String pPath = SERVER_PATH + infoId + "/" + path;
        String dataPath = pPath + "/" + saveTime;
        if (client.exists(dataPath)) {
            client.setData(dataPath, model.getData());
        } else {
            // 权限读删
            int params = ZKACLUtil.toPermInt("rd");
            // 类型公开
            ACL acl = new ACL(params, ZooDefs.Ids.ANYONE_ID_UNSAFE);
            client.create(dataPath, model.getData(), List.of(acl), null, CreateMode.PERSISTENT, true);
        }
        // 删除超出限制的节点
        Stat stat = client.checkExists(pPath);
        if (stat != null && stat.getNumChildren() > His_Max_Size) {
            long minTime = -1;
            String beDelNodeNode = null;
            // 便利字节的
            for (String node : client.getChildren(dataPath)) {
                // 获取保存时间
                long nodeSaveTime;
                try {
                    nodeSaveTime = Long.parseLong(node);
                } catch (Exception ex) {
                    Stat stat1 = client.checkExists(pPath + "/" + node);
                    nodeSaveTime = stat1.getMtime();
                }
                // 更新删除节点
                if (nodeSaveTime < minTime) {
                    minTime = nodeSaveTime;
                    beDelNodeNode = node;
                }
            }
            // 删除节点
            if (beDelNodeNode != null) {
                client.delete(pPath + "/" + beDelNodeNode, null, true);
            }
        }
    }

    public boolean deleteLocal(String infoId, String path, long saveTime) {
        DeleteParam deleteParam = new DeleteParam();
        deleteParam.addQueryParam(new QueryParam("infoId", infoId));
        deleteParam.addQueryParam(new QueryParam("saveTime", saveTime));
        deleteParam.addQueryParam(new QueryParam("path", MD5Util.md5Hex(path)));
        deleteParam.setLimit(1L);
        return super.delete(deleteParam);
    }

    public boolean deleteServer(String infoId, String path, long saveTime, ZKClient client) {
        try {
            String dataPath = SERVER_PATH + infoId + "/" + MD5Util.md5Hex(path) + "/" + saveTime;
            if (client.exists(dataPath)) {
                client.delete(dataPath);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected ZKDataHistory newModel() {
        return new ZKDataHistory();
    }

    @Override
    protected Class<ZKDataHistory> modelClass() {
        return ZKDataHistory.class;
    }

    public byte[] getLocalData(String infoId, String path, long saveTime) {
        SelectParam param = new SelectParam();
        param.addQueryParam(new QueryParam("infoId", infoId));
        param.addQueryParam(new QueryParam("saveTime", saveTime));
        param.addQueryParam(new QueryParam("path", MD5Util.md5Hex(path)));
        param.addQueryColumn("data");
        ZKDataHistory history = super.selectOne(param);
        return history == null ? null : history.getData();
    }

    public byte[] getServerData(String infoId, String path, long saveTime, ZKClient client) {
        try {
            String dataPath = SERVER_PATH + infoId + "/" + MD5Util.md5Hex(path) + "/" + saveTime;
            if (client.exists(dataPath)) {
                return client.getData(dataPath);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
