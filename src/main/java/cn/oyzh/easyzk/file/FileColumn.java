package cn.oyzh.easyzk.file;

import lombok.Data;

/**
 * @author oyzh
 * @since 2024-11-27
 */
@Data
public class FileColumn {

    private String name;

    private String type;

    private int position;

    public FileColumn() {

    }

    public FileColumn(String name) {
        this.name = name;
    }

    public FileColumn(String name, String type, int position) {
        this.name = name;
        this.type = type;
        this.position = position;
    }
}
