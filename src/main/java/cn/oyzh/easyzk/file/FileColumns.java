package cn.oyzh.easyzk.file;

import cn.oyzh.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oyzh
 * @since 2024-11-27
 */
public class FileColumns {

    private final List<FileColumn> columns = new ArrayList<>();

    public void addColumn(String name) {
        this.columns.add(new FileColumn(name));
    }

    public int index(String columnName) {
        FileColumn column = this.column(columnName);
        return column == null ? -1 : this.columns.indexOf(column);
    }

    public List<String> columnNames() {
        return this.columns.parallelStream().map(FileColumn::getName).collect(Collectors.toList());
    }

    public String columnName(int index) {
        FileColumn column = this.columns.get(index);
        return column == null ? null : column.getName();
    }

    public FileColumn column(String columnName) {
        for (FileColumn column : columns) {
            if (StringUtil.equals(columnName, column.getName())) {
                return column;
            }
        }
        return null;
    }

    public List<FileColumn> sortOfPosition() {
        this.columns.sort(Comparator.comparingInt(FileColumn::getPosition));
        return this.columns;
    }
}
