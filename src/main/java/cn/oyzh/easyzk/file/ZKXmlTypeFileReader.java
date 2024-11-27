package cn.oyzh.easyzk.file;

import lombok.NonNull;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-03
 */
public class ZKXmlTypeFileReader extends ZKTypeFileReader {

    /**
     * xml读取器
     */
    private XMLEventReader reader;

    /**
     * 导入配置
     */
    private ZKDataImportConfig config;

    public ZKXmlTypeFileReader(@NonNull File file, ZKDataImportConfig config) throws Exception {
        this.config = config;
        this.reader = XMLInputFactory.newInstance().createXMLEventReader(new FileInputStream(file), config.charset());
        this.init();
    }

    @Override
    protected void init() throws Exception {
        while (this.reader.hasNext()) {
            XMLEvent event = this.reader.nextEvent();
            if (event.isStartElement()) {
                break;
            }
        }
    }

    @Override
    public Map<String, Object> readObject() throws Exception {
        String name = null;
        String value = null;
        Map<String, Object> map = null;
        boolean objStart = false;
        boolean childStart = false;
        while (this.reader.hasNext()) {
            XMLEvent event = this.reader.nextEvent();
            // 读取结束
            if (event.isEndElement() && objStart && !childStart) {
                break;
            }
            // 属性读取为字段
            if (this.config.attrToColumn()) {
                // 读取开始
                if (event.isStartElement() && !objStart) {
                    objStart = true;
                    StartElement element = event.asStartElement();
                    Iterator<Attribute> attributes = element.getAttributes();
                    while (attributes.hasNext()) {
                        Attribute attribute = attributes.next();
                        if (map == null) {
                            map = new HashMap<>();
                        }
                        map.put(attribute.getName().getLocalPart(), attribute.getValue());
                    }
                }
            } else {// 属性为子节点
                // 读取开始
                if (event.isStartElement() && !objStart) {
                    objStart = true;
                    continue;
                }
                // 子节点开始
                if (event.isStartElement() && !childStart) {
                    childStart = true;
                    StartElement element = event.asStartElement();
                    name = element.getName().getLocalPart();
                    continue;
                }
                // 子节点结束
                if (event.isEndElement() && childStart) {
                    childStart = false;
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    map.put(name, value);
                    name = null;
                    value = null;
                    continue;
                }
                // 子节点数据
                if (event.isCharacters() && name != null) {
                    value = event.asCharacters().getData();
                }
            }
        }
        return map;
    }

    @Override
    public void close() {
        try {
            if (this.reader != null) {
                this.reader.close();
            }
            this.reader = null;
            this.config = null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
