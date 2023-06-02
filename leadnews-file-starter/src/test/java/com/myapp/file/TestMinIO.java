package com.myapp.file;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

public class TestMinIO {
    @Test
    public void test(){
        FileInputStream fileInputStream = null;
        try {

            fileInputStream =  new FileInputStream("D:\\data\\test.html");;

            //1.创建minio链接客户端
            MinioClient minioClient = MinioClient.builder()
                    .credentials("minio", "minioadmin")
                    .endpoint("http://myvm.site:9000")
                    .build();
            //2.上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("test.html")//文件名
                    .contentType("text/html")//文件类型
                    .bucket("leadnews")//桶名词  与minio创建的名词一致
                    .stream(fileInputStream, fileInputStream.available(), -1) //文件流
                    .build();
            minioClient.putObject(putObjectArgs);

            System.out.println("http://192.168.200.130:9000/leadnews/test.html");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
