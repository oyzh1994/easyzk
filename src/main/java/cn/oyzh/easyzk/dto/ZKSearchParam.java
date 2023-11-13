package cn.oyzh.easyzk.dto;

import lombok.Data;

import java.util.Objects;

/**
 * zk搜索参数
 *
 * @author oyzh
 * @since 2023/3/10
 */
@Data
public class ZKSearchParam {

    /**
     * 关键词
     */
    private String kw;

    /**
     * 全文匹配
     */
    private boolean fullMatch;

    /**
     * 搜索数据
     */
    private boolean searchData;

    /**
     * 搜索路径
     */
    private boolean searchPath;

    /**
     * 搜索模式
     * true 搜索模式
     * false 过滤模式
     */
    private boolean searchMode;

    /**
     * 匹配大小写
     */
    private boolean compareCase;

    /**
     * 是否匹配
     *
     * @param data 数据
     * @return 结果
     */
    public boolean isMatch(String data) {
        if (data == null) {
            return false;
        }
        // 处理大小写，如果不需要匹配大小写，则直接把数据转小写即可
        String kw = this.getKw();
        // 搜索词大于数据长度，直接返回false
        if (kw.length() > data.length()) {
            return false;
        }
        // 如果不匹配大小写，则全部转小写比较
        if (!this.compareCase) {
            kw = kw.toLowerCase();
            data = data.toLowerCase();
        }
        // 全文匹配
        if (this.fullMatch) {
            return data.equals(kw);
        }
        // 部分匹配
        return data.contains(kw);
    }

    /**
     * 比较另外一个搜索参数，判断参数是否一致
     *
     * @param param 搜索参数
     * @return 比较结果
     */
    public boolean equalsTo(ZKSearchParam param) {
        if (param == null) {
            return false;
        }
        if (param == this) {
            return true;
        }
        if (!Objects.equals(this.kw, param.kw)) {
            return false;
        }
        if (!Objects.equals(this.fullMatch, param.fullMatch)) {
            return false;
        }
        if (!Objects.equals(this.searchMode, param.searchMode)) {
            return false;
        }
        if (!Objects.equals(this.searchData, param.searchData)) {
            return false;
        }
        if (!Objects.equals(this.searchPath, param.searchPath)) {
            return false;
        }
        return Objects.equals(this.compareCase, param.compareCase);
    }

    public boolean isEmpty() {
        return this.kw == null || this.kw.isEmpty();
    }
}
