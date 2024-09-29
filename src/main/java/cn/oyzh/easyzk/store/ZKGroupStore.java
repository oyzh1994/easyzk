package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.fx.common.json.JSONUtil;
import cn.oyzh.fx.common.log.JulLog;
import cn.oyzh.fx.common.store.ArrayFileStore;
import cn.oyzh.fx.common.util.CollectionUtil;
import cn.oyzh.fx.common.util.FileUtil;
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.common.util.UUIDUtil;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * zk分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
@Deprecated
//@Slf4j
public class ZKGroupStore extends ArrayFileStore<ZKGroup> {

    /**
     * 当前实例
     */
    public static final ZKGroupStore INSTANCE = new ZKGroupStore();

    /**
     * 已加载的zk节点
     */
    private List<ZKGroup> groups;

    // {
    //     this.filePath(ZKConst.STORE_PATH + "zk_group.json");
    //     JulLog.info("ZKInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    //     this.groups = this.load();
    // }

    public ZKGroupStore() {
        this.filePath(ZKConst.STORE_PATH + "zk_group.json");
    }

    @Override
    public synchronized List<ZKGroup> load() {
        if (this.groups == null) {
            // 读取存储文件中的文本
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StringUtil.isBlank(text)) {
                return new ArrayList<>();
            }
            // 将文本转换为ZKGroup列表
            List<ZKGroup> groups = JSONUtil.toList(text, ZKGroup.class);
            if (CollectionUtil.isNotEmpty(groups)) {
                // 对ZKGroup列表进行排序
                groups = groups.parallelStream().sorted().collect(Collectors.toList());
            }
            return groups;
        }
        return this.groups;
    }

    /**
     * 添加分组
     *
     * @param groupName 分组名称
     * @return 结果
     */
    public synchronized ZKGroup add(@NonNull String groupName) {
        ZKGroup group = new ZKGroup(UUIDUtil.uuid(), groupName, false);
        if (this.add(group)) {
            return group;
        }
        return null;
    }

    @Override
    public synchronized boolean add(@NonNull ZKGroup zkGroup) {
        try {
            if (!this.groups.contains(zkGroup)) {
                // 添加到集合
                this.groups.add(zkGroup);
                // 更新数据
                return this.save(this.groups);
            }
        } catch (Exception e) {
            JulLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull ZKGroup zkGroup) {
        try {
            // 更新数据
            if (this.groups.contains(zkGroup)) {
                return this.save(this.groups);
            }
        } catch (Exception e) {
            JulLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull ZKGroup zkGroup) {
        try {
            // 删除数据
            if (this.groups.remove(zkGroup)) {
                return this.save(this.groups);
            }
        } catch (Exception e) {
            JulLog.warn("delete error,err:{}", e.getMessage());
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
    public synchronized boolean exist(ZKGroup zkGroup) {
        // 如果传入的分组信息为空，则直接返回false
        if (zkGroup == null) {
            return false;
        }
        // 遍历this.zkGroups列表，检查是否存在与传入的分组信息相同的分组
        for (ZKGroup group : this.groups) {
            if (Objects.equals(group.getName(), zkGroup.getName()) && group != zkGroup) {  // 如果分组名称相同且不是同一个对象，则说明存在相同的分组信息，返回true
                return true;
            }
        }
        // 循环结束后仍未找到相同的分组信息，返回false
        return false;
    }

}
