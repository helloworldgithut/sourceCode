package com.sendi.utils;

/**
 *
 *用于数据的封装与解析
 * Created by fengzm on 2019/2/2.
 */
public class MessageUtil {

    /**
     * 拼接数据
     * @param post2 post请求
     * @param name 资源名称
     * @param payload 负载内容
     * @param format 格式（42 text/50 json）
     * @return 拼接完整的字节数组（数据包）
     */
    public static byte[] packData(byte[] post2, byte[] name, byte[] payload, byte format[]) throws Exception {
        int len;
        byte[] post = post2;
        byte[] temp = new byte[post.length + 1];
        if (name.length > 13) {
            temp = new byte[post.length + 2];
        }
        for (int i = 0; i < post.length; i++) {
            temp[i] = post[i];
        }
        len = 176 + name.length;
        if (name.length < 13) {
            byte n = (byte) len;
            temp[post.length] = n;
        } else if (name.length > 13) {
            //0xbd
            int num = len - 189;
            byte n = (byte) num;
            temp[post.length] = (byte) 0xbd;
            temp[post.length + 1] = n;
        }
        post = temp;
        byte[] resName = new byte[post.length + name.length + format.length + payload.length];
        System.arraycopy(post, 0, resName, 0, post.length);
        System.arraycopy(name, 0, resName, post.length, name.length);
        System.arraycopy(format, 0, resName, post.length + name.length, format.length);
        System.arraycopy(payload, 0, resName, post.length + name.length + format.length, payload.length);
        return resName;
    }

    /**
     * @param post2
     * @param name
     * @return
     */
    public static byte[] packData(byte[] post2, byte[] name)  throws Exception {
        int len;
        byte[] post = post2;
        byte[] temp = new byte[post.length + 1];
        if (name.length > 13) {
            temp = new byte[post.length + 2];
        }
        for (int i = 0; i < post.length; i++) {
            temp[i] = post[i];
        }
        len = 176 + name.length;
        if (name.length < 13) {
            byte n = (byte) len;
            temp[post.length] = n;
        } else if (name.length > 13) {
            int num = len - 189;
            byte n = (byte) num;
            temp[post.length] = (byte) 0xbd;
            temp[post.length + 1] = n;
        }
        post = temp;
        byte[] resName = new byte[name.length + post.length];
        System.arraycopy(post, 0, resName, 0, post.length);
        System.arraycopy(name, 0, resName, post.length, name.length);
        return resName;
    }

}
