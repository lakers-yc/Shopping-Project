package com.changgou.utils;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

//工具类
public class FastDFSClient {
    private static TrackerClient trackerClient;//3.创建trackerclient对象
    private static TrackerServer trackerServer;//4.获取trackerServer对象
    private static StorageClient storageClient;//5.创建storageClient对象


    static {
        try {
            //1.创建一个配置文件 用于配置tracker_server的ip和端口
            //2.加载配置文件生效
            ClassPathResource classPathResource = new ClassPathResource("fdfs_client.conf");
            String path = classPathResource.getPath();
            ClientGlobal.init(path);

            //3.创建trackerclient对象
            trackerClient = new TrackerClient();

            //4.获取trackerServer对象
            trackerServer = trackerClient.getConnection();

            //5.创建storageClient对象
            storageClient = new StorageClient(trackerServer,null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

    }

    /**
     * 图片/文件上传
     * @param file
     * @return
     * @throws Exception
     */
    public static String[] upload(FastDFSFile file) throws Exception{
        //6.执行一个方法（上传图片的方法）storageClient 提供了许多操作文件的方法
        //参数1 指定要上传文件的本地的路径
        //参数2 指定文件的扩展名 不要带 “.”
        //参数3 指定元数据（图片的作者，拍摄日期 像素 。。。。。。）
        NameValuePair[] meta_list = new NameValuePair[]{
                new NameValuePair(file.getName())
        };

        String[] pngs = storageClient.upload_file(file.getContent(),file.getExt(),meta_list);
        for (String png : pngs) {
            System.out.println(png);
        }
        return pngs;
    }

    /**
     * 图片\文件下载
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static byte[] downFile(String groupName, String remoteFileName) throws Exception {
        //参数1 指定要下载的组名
        //参数2 指定要下载的远程文件路径
        byte[] group1s = storageClient.download_file(groupName, remoteFileName);
        return group1s;
    }


    /**
     *删除图片/文件
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static boolean deleteFile(String groupName, String remoteFileName) throws Exception {

        int group1 = storageClient.delete_file(groupName, remoteFileName);

        if (group1 == 0) {
            return true;
        } else {
            return false;
        }
    }
}
