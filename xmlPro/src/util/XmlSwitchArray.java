package util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XmlSwitchArray {
    public static Object convertObject(String data, String type) {  //类型转换函数
        Object obj = null;
        switch (type) {
            case "String":
                obj = new String(data);
                break;
            case "double":
            case "Double":
                obj = new Double(data);
                break;
            case "float":
            case "Float":
                obj = new Float(data);
                break;
            case "int":
            case "Integer":
                obj = new Integer(data);
                break;
            //后面可以扩充转换为其他数据类型
        }
        return obj;
    }

    public static List<Object> getXMLAsArray(String path, String className) {
        List<Object> resultList = new ArrayList<Object>();
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(path));
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            Class aClass = Class.forName(className);

            for (Element child : childElements) {
                Object obj = aClass.newInstance();
                List<Element> elementList = child.elements();
                for (Element ele : elementList) {
                    String fieldName = ele.getName();
                    Field field = aClass.getDeclaredField(fieldName);
                    String getMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method method = aClass.getMethod(getMethodName, field.getType());
                    method.invoke(obj, convertObject(ele.getText(), field.getType().getSimpleName()));
                }
                resultList.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static void arrayToXML(List<Object> list, String path) {   //list转XML
        Document document = DocumentHelper.createDocument();
        XMLWriter writer = null;
        Object obj = null;
        try {
            Object obj1 = list.get(0).getClass().newInstance();
            if (list != null && list.size() != 0) {
                Element root = document.addElement(obj1.getClass().getSimpleName().substring(0, 1).toLowerCase() + obj1.getClass().getSimpleName().substring(1) + "s");
                root.addAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
                root.addAttribute("xsi:noNamespaceSchemaLocation","CustomersInfo.xsd");
                for (int i = 0; i < list.size(); i++) {

                    obj = list.get(i);

                    Element childElement = root.addElement(obj.getClass().getSimpleName().substring(0, 1).toLowerCase() + obj.getClass().getSimpleName().substring(1));
                    Field[] field = obj.getClass().getDeclaredFields();
                    for (int j = 0; j < field.length; j++) {
                        field[j].setAccessible(true);
                        String fieldName = field[j].getName();
                        /*判断所属对象是否为空，如果是则剔除*/
                        if (field[j].get(obj) != null) {
                            String keyValue = field[j].get(obj).toString();
                            Element key = childElement.addElement(fieldName);
                            key.setText(keyValue);
                        }
                    }
                }
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding("UTF-8");
                File file = new File(path);
                writer = new XMLWriter(new FileOutputStream(file), format);
                writer.write(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
