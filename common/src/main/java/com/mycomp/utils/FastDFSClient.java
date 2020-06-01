package com.mycomp.utils;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

public class FastDFSClient {

    private StorageClient1 storageClient;

    public FastDFSClient(String conf) throws Exception {
        if (conf.contains("classpath:")) {
            /*
             * 此处获取fdfs_client.conf配置文件绝对路径(项目编译部署后, 在Tomcat容器中的绝对路径);
             * 因为我们的fdfs_client.conf是放在resources资源目录下的, 项目在编译部署后,
             * resources目录下的文件会被放进\WEB-INF\classes\下, 所以,
             * 可以使用Class.getResource()的方式获取项目的编译文件的根目录的绝对路径(即从盘符到WEB-INF\classes\为止的路径),
             * 如:
             *     C:\dev\Tomcat\apache-tomcat-9.0.30\webapps\项目名称\WEB-INF\classes\
             * Spring配置文件、或者web.xml中经常使用的"classpath:"其实就代表着这个路径;
             * (不在资源目录中的文件, 例如直接放在普通文件夹下、或者webapp文件夹下的文件, 在项目编译部署后,
             * 会被直接放在项目根目录下, 而不是WEB-INF\classes下,
             * 例如C:\dev\Tomcat\apache-tomcat-9.0.30\webapps\项目名称\)
             *
             * Class.getResource(name)和ClassLoader.getResource(name)的区别:
             * 1. 两者作用一样, 都是接收一个表示资源名称的参数(name), 返回一个URL字符串,
             *    该URL字符串表示name对应的资源在工程中的绝对路径(从盘符开始的路径);
             *    其实就是在传入的name前拼接上项目编译后编译文件的根目录的路径, 即拼接上例如:
             *    C:\dev\Tomcat\apache-tomcat-9.0.30\webapps\项目名称\WEB-INF\classes\
             *    (前提是找到了name所指向的资源, 没找到的话会抛异常)
             * 2. 但是, 不同的是, ClassLoader.getResource(name)只能接收一个相对路径,
             *    不能接收绝对路径, 并且, 接收的相对路径是相对于项目的编译文件的根目录来说的(即\WEB-INF\classes\);
             * 3. Class.getResource(name)接收的name可以是文件的相对路径(相对于该class类来说),
             *    也可以是绝对路径(绝对路径的话, 根目录符号"/"代表的是项目的编译文件的根目录, 即\WEB-INF\classes\,
             *    而不是磁盘的根目录);
             */
            conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
        }
        ClientGlobal.init(conf);
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageServer storageServer = null;  // 传入的StorageServer为null, 即不指定上传的storage服务器, 让tracker服务器自己指定storage服务器
        storageClient = new StorageClient1(trackerServer, storageServer);
    }

    /**
     * @param file     二进制文件
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @return 客户端上传文件后, storage服务器返回的文件ID, 我们一般会把这个ID存到数据库中, 用于以后访问该文件的索引信息
     */
    public String uploadFile(byte[] file, String fileName, long fileSize) throws Exception {
        NameValuePair[] metas = new NameValuePair[3];
        metas[0] = new NameValuePair("fileName", fileName);
        metas[1] = new NameValuePair("fileSize", String.valueOf(fileSize));
        metas[2] = new NameValuePair("fileExt", FilenameUtils.getExtension(fileName));
        return storageClient.upload_file1(file, FilenameUtils.getExtension(fileName), metas);
    }

    /**
     * @param storagePath 文件的全部路径(上传文件时storage服务器返回的文件ID),
     *                    如: group1/M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return 删除是否成功, 0: 成功, -1: 失败
     */
    public Integer delete_file(String storagePath) {
        int result = -1;
        try {
            result = storageClient.delete_file1(storagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}