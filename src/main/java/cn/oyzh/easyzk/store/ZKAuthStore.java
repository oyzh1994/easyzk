package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.FileUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.store.json.ArrayFileStore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * zk认证存储
 *
 * @author oyzh
 * @since 2022/12/16
 */
//@Slf4j
@Deprecated
public class ZKAuthStore extends ArrayFileStore<ZKAuth> {

    /**
     * 当前实例
     */
    public static final ZKAuthStore INSTANCE = new ZKAuthStore();

    // {
    //     this.filePath(ZKConst.STORE_PATH + "zk_auth.json");
    //     JulLog.info("ZKAuthStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    // }

    public ZKAuthStore() {
        this.filePath(ZKConst.STORE_PATH + "zk_auth.json");
    }

    @Override
    public synchronized List<ZKAuth> load() {
        // 从存储文件中读取权限列表的文本信息
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StringUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        // 将文本信息解析为权限列表
        List<ZKAuth> auths = JSONUtil.toList(text, ZKAuth.class);
        if (CollectionUtil.isNotEmpty(auths)) {
            // 过滤掉空的权限对象，并按照用户名称进行排序
            auths = auths.parallelStream().filter(Objects::nonNull).sorted((o1, o2) -> o1.getUser().compareToIgnoreCase(o2.getUser())).collect(Collectors.toList());
        }
        return auths;
    }

    @Override
    public synchronized boolean add(@NonNull ZKAuth auth) {
        try {
            List<ZKAuth> auths = this.load();
            Optional<ZKAuth> optional = auths.parallelStream().filter(z -> z.compare(auth)).findFirst();
            if (optional.isEmpty()) {
                // 添加到集合
                auths.add(auth);
                // 更新数据
                return this.save(auths);
            }
            return true;
        } catch (Exception e) {
            JulLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull ZKAuth auth) {
        try {
            List<ZKAuth> auths = this.load();
            Optional<ZKAuth> optional = auths.parallelStream().filter(auth::compare).findFirst();
            if (optional.isPresent()) {
                optional.get().copy(auth);
                // 更新数据
                return this.save(auths);
            }
        } catch (Exception e) {
            JulLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull ZKAuth auth) {
        try {
            List<ZKAuth> zkAuths = this.load();
            if (CollectionUtil.isEmpty(zkAuths)) {
                return false;
            }
            Optional<ZKAuth> authOptional = zkAuths.parallelStream().filter(auth::compare).findFirst();
            if (authOptional.isPresent()) {
                // 移除zk信息
                zkAuths.remove(authOptional.get());
                // 更新数据
                return this.save(zkAuths);
            }
        } catch (Exception e) {
            JulLog.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Paging<ZKAuth> getPage(int limit, Map<String, Object> params) {
        // 加载数据
        List<ZKAuth> list = this.load();
        // 分页对象
        Paging<ZKAuth> paging = new Paging<>(list, limit);
        // 数据为空
        if (CollectionUtil.isNotEmpty(list)) {
            String searchKeyWord = params == null ? null : (String) params.get("searchKeyWord");
            // 过滤数据
            if (StringUtil.isNotBlank(searchKeyWord)) {
                final String kw = searchKeyWord.toLowerCase().trim();
                list = list.parallelStream().filter(z -> z.getPassword().contains(kw) || z.getUser().contains(kw)).collect(Collectors.toList());
            }
            // 添加到分页数据
            paging.dataList(list);
        }
        return paging;
    }

    public synchronized boolean exist(@NonNull String user, @NonNull String password) {
        List<ZKAuth> zkAuths = this.load();
        if (CollectionUtil.isEmpty(zkAuths)) {
            return false;
        }
        Optional<ZKAuth> optional = zkAuths.parallelStream().filter(z -> z.compare(user, password)).findFirst();
        return optional.isPresent();
    }

}
