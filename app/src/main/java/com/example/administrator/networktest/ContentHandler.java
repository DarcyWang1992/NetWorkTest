package com.example.administrator.networktest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ContentHandler extends DefaultHandler {
    private String nodename;
    private StringBuilder id;
    private StringBuilder name;
    private StringBuilder version;
    private static final String TAG = "ContentHandler";
    /**
     * 开始解析XML的时候调用
     * @throws SAXException
     */
    @Override
    public void startDocument() throws SAXException {
        id=new StringBuilder();
        name=new StringBuilder();
        version=new StringBuilder();
    }

    /***
     * 完成整个解析的时候调用
     * @throws SAXException
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * 开始解析XML的某个节点的时候调用
     * @param uri
     * @param localName 记录着当前节点的名字
     * @param qName
     * @param attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //记录当前的节点名
       nodename=localName;
    }

    /**
     * 完成解析某个节点的时候调用
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("app".equals(localName)){
            //因为在解析的数据中有可能包含回车和换位符，所以在这里调用了trim（）方法
            Log.d(TAG, "endElement: id is"+id.toString().trim());
            Log.d(TAG, "endElement: name is"+name .toString().trim());
            Log.d(TAG, "endElement: version is"+version.toString().trim());
            //将StringBuilder清空，不然会影响下一次内容的读取
            id.setLength(0);
            name.setLength(0);
            version.setLength(0);
        }
    }

    /**
     * 获取节点中的内容的时候调用
     * 再获取节点的内容时会被调用多次，一些换位符也会被当作内容解析出来，需要再代码中做好控制
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //根据当前的节点名判断将内容添加到哪一个StringBuilder对象中
       if ("id".equals(nodename)){
           id.append(ch,start,length);
       }else if ("name".equals(nodename)){
           name.append(ch,start,length);
       }else if ("version".equals(nodename)){
           version.append(ch,start,length);
       }
    }


}
