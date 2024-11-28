package cn.oyzh.easyzk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * zk环境节点
 *
 * @author oyzh
 * @since 2024/1286
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZKEnvNode {

    /**
     * id
     */
    private String name;

    /**
     * 类型
     */
    private String value;
}
