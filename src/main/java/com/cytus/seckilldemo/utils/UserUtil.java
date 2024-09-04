package com.cytus.seckilldemo.utils;


import com.cytus.seckilldemo.pojo.User;
import com.cytus.seckilldemo.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.lang.invoke.SwitchPoint;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* 生成不同测试用户
* */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        for(int i = 0; i < count; i++)
        {
            User user = new User();
            user.setId(13000000000L+i);
            user.setNickname("user"+i);
            user.setSlat("1a2b3c4d");
            user.setPassword(MD5Util.inputPassToDBPass("123456",user.getSlat()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user");
//        // 插入数据库
//        Connection conn = getConn();
//
//        String sql = "insert into t_user(login_count,nickname,register_date,slat,password,id) values(?,?,?,?,?,?)";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        for(int i = 0;i < users.size();i++)
//        {
//            User user = users.get(i);
//            pstmt.setInt(1,user.getLoginCount());
//            pstmt.setString(2,user.getNickname());
//            pstmt.setTimestamp(3,new Timestamp(user.getRegisterDate().getTime()));
//            pstmt.setString(4,user.getSlat());
//            pstmt.setString(5,user.getPassword());
//            pstmt.setLong(6,user.getId());
//            pstmt.addBatch();
//        }
//        pstmt.executeBatch();
//        pstmt.clearParameters();
//        conn.close();
//        System.out.println("insert to db");

        // 登录获取userTicket
        String urlString = "http://localhost:8080/login/dologin";
        File file = new File("D:\\data\\工作\\javaDemoData\\config.txt");
        if(file.exists())
        {
            file.delete();
        }
        RandomAccessFile ref = new RandomAccessFile(file, "rw");
        ref.seek(0);
        for(int i=0; i<users.size();i++)
        {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff))>=0)
            {
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            String reponse = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(reponse, RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket:"+user.getId());
            String row = user.getId()+","+userTicket;
            ref.seek(ref.length());
            ref.write(row.getBytes());
            ref.write("\r\n".getBytes());
            System.out.println("write to file:"+user.getId());
        }
        ref.close();
        System.out.println("over");
    }
    private static Connection getConn() throws Exception {
        String url  = "jdbc:mysql://localhost:3306/seckill?userUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "root";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
