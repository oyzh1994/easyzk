package cn.oyzh.easyzk.terminal;

import cn.oyzh.fx.terminal.histroy.TerminalHistory;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2024-11-25
 */
@Data
@Table("t_terminal_history")
@EqualsAndHashCode(callSuper = true)
public class ZKTerminalHistory extends TerminalHistory {

    /**
     * 数据id
     */
    @Getter
    @Setter
    @Column
    @PrimaryKey
    private String tid;
}
