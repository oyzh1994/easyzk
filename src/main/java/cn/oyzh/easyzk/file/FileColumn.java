package cn.oyzh.easyzk.file;

import lombok.Data;

/**
 * @author oyzh
 * @since 2024-11-27
 */
@Data
public class FileColumn {

    private String name;

    private int position;

    public FileColumn() {

    }

    public FileColumn(String name) {
        this.name = name;
    }

    public FileColumn(String name, int position) {
        this.name = name;
        this.position = position;
    }
}
