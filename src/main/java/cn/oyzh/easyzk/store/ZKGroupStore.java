package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.fx.common.util.FileStore;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * zk分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
@Slf4j
public class ZKGroupStore extends FileStore<ZKGroup> {

    /**
     * 当前实例
     */
    public static final ZKGroupStore INSTANCE = new ZKGroupStore();

    /**
     * 已加载的zk节点
     */
    private final List<ZKGroup> zkGroups;

    {
        this.filePath(ZKConst.STORE_PATH + "zk_group.json");
        log.info("ZKInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
        this.zkGroups = this.load();
    }

    @Override
    public synchronized List<ZKGroup> load() {
        if (this.zkGroups == null) {
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isBlank(text)) {
                return new ArrayList<>();
            }
            List<ZKGroup> zkGroups = JSON.parseArray(text, ZKGroup.class);
            if (CollUtil.isNotEmpty(zkGroups)) {
                zkGroups = zkGroups.parallelStream().sorted().collect(Collectors.toList());
            }
            return zkGroups;
        }
        return this.zkGroups;
    }

    /**
     * 添加分组
     *
     * @param groupName 分组名称
     * @return 结果
     */
    public synchronized ZKGroup add(@NonNull String groupName) {
        ZKGroup group = new ZKGroup(UUID.fastUUID().toString(true), groupName, false);
        if (this.add(group)) {
            return group;
        }
        return null;
    }

    @Override
    public synchronized boolean add(@NonNull ZKGroup zkGroup) {
        try {
            if (!this.zkGroups.contains(zkGroup)) {
                // 添加到集合
                this.zkGroups.add(zkGroup);
                // 更新数据
                return this.save(this.zkGroups);
            }
        } catch (Exception e) {
            log.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull ZKGroup zkGroup) {
        try {
            // 更新数据
            if (this.zkGroups.contains(zkGroup)) {
                return this.save(this.zkGroups);
            }
        } catch (Exception e) {
            log.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull ZKGroup zkGroup) {
        try {
            // 删除数据
            if (this.zkGroups.remove(zkGroup)) {
                return this.save(this.zkGroups);
            }
        } catch (Exception e) {
            log.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 是否存在此分组信息
     *
     * @param zkGroup 分组信息
     * @return 结果
     */
    public boolean exist(ZKGroup zkGroup) {
        if (zkGroup == null) {
            return false;
        }
        for (ZKGroup group : this.zkGroups) {
            if (Objects.equals(group.getName(), zkGroup.getName()) && group != zkGroup) {
                return true;
            }
        }
        return false;
    }
}
