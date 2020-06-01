package com.mycomp.core.controller.admanage;

import com.mycomp.core.pojo.queryentity.RestResult;
import com.mycomp.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    // 注入属性文件中的值, 需要先在springmvc.xml中加载该属性文件(通过context:property-placeholder标签)
    @Value("${FILE_SERVER_URL}")
    private String file_server_url;

    private FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");

    public FileController() throws Exception {
    }

    @RequestMapping("/uploadFile")
    public RestResult uploadFile(MultipartFile file) {
        try {
            /*
             * 客户端上传文件后, storage服务器会返回的文件ID, 我们一般会把这个ID(path)存到数据库中,
             * 用于以后访问该文件的索引信息;
             */
            String path = fastDFSClient.uploadFile(file.getBytes(), file.getOriginalFilename(), file.getSize());
            String url = file_server_url + path;
            return new RestResult(true, url);
        } catch (Exception e) {
            return new RestResult(false, "文件上传失败...");
        }
    }

}
