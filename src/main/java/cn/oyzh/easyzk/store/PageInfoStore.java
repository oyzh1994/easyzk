package cn.oyzh.easyzk.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.PageInfo;
import cn.oyzh.fx.common.store.ObjectFileStore;


/**
 * 页面信息储存
 *
 * @author oyzh
 * @since 2023/01/17
 */
//@Slf4j
public class PageInfoStore extends ObjectFileStore<PageInfo> {

    /**
     * 当前实例
     */
    public static final PageInfoStore INSTANCE = new PageInfoStore();

    /**
     * 当前设置
     */
    public static final PageInfo PAGE_INFO = INSTANCE.load();

    {
        this.filePath(ZKConst.STORE_PATH + "page_info.json");
        StaticLog.info("PageInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized PageInfo load() {
        PageInfo pageInfo = null;
        try {
            // 从文件中读取文本内容
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isNotBlank(text)) {
                // 将文本内容转换为页面信息对象
                pageInfo = JSONUtil.toBean(text, PageInfo.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 如果页面信息为空，则创建一个新的页面信息对象
        if (pageInfo == null) {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }
}
