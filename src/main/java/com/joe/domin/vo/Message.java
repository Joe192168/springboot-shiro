package com.joe.domin.vo;

import org.apache.http.HttpStatus;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 前后端统一消息定义协议 Message  之后前后端数据交互都按照规定的类型进行交互
 */
public class Message {

    // 消息头meta 存放状态信息 code message
    private Map<String,Object> meta = new HashMap<String,Object>();
    // 消息内容  存储实体交互数据
    private Map<String,Object> data = new HashMap<String,Object>();

    public Map<String, Object> getMeta() {
        return meta;
    }

    public Message setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Message setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }
    public Message addMeta(String key, Object object) {
        this.meta.put(key,object);
        return this;
    }
    public Message addData(String key,Object object) {
        this.data.put(key,object);
        return this;
    }
    public Message ok(int statusCode,String statusMsg) {
        this.addMeta("success",Boolean.TRUE);
        this.addMeta("code",statusCode);
        this.addMeta("msg",statusMsg);
        this.addMeta("timestamp",new Timestamp(new Date().getTime()));
        return this;
    }
    public Message error(int statusCode,String statusMsg) {
        this.addMeta("success",Boolean.FALSE);
        this.addMeta("code",statusCode);
        this.addMeta("msg",statusMsg);
        this.addMeta("timestamp",new Timestamp(new Date().getTime()));
        return this;
    }

    public Message ok() {
        this.addMeta("success",Boolean.TRUE);
        this.addMeta("code",200);
        this.addMeta("msg","操作成功");
        this.addMeta("timestamp",new Timestamp(new Date().getTime()));
        return this;
    }


    public Message error() {
        this.addMeta("success",Boolean.FALSE);
        this.addMeta("code",500);
        this.addMeta("msg","操作失败");
        this.addMeta("timestamp",new Timestamp(new Date().getTime()));
        return this;
    }

    /**
     * 接口调用成功使用，参数为自定义信息
     * @param statusMsg
     * @return
     */
    public Message ok(String statusMsg) {
        this.addMeta("success",Boolean.TRUE);
        this.addMeta("code", HttpStatus.SC_OK);
        this.addMeta("msg",statusMsg);
        this.addMeta("timestamp",new Timestamp(new Date().getTime()));
        return this;
    }


    /**
     * 接口调用失败或者调用异常时使用，参数为失败信息或者异常信息
      * @param statusMsg
     * @return
     */
    public Message error(String statusMsg) {
        this.addMeta("success",Boolean.FALSE);
        this.addMeta("code",HttpStatus.SC_INTERNAL_SERVER_ERROR);
        this.addMeta("msg",statusMsg);
        this.addMeta("timestamp",new Timestamp(new Date().getTime()));
        return this;
    }
}
