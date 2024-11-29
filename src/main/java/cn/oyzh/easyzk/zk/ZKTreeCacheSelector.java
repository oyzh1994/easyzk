package cn.oyzh.easyzk.zk;

import org.apache.curator.framework.recipes.cache.TreeCacheSelector;

/**
 * zk缓存选择器
 *
 * @author oyzh
 * @since 2024/11/29
 */
public class ZKTreeCacheSelector implements TreeCacheSelector {

    @Override
    public boolean traverseChildren(String fullPath) {
       return true;
    }

    @Override
    public boolean acceptChild(String fullPath) {
       return true;
    }
}
