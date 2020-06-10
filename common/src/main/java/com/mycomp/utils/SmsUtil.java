package com.mycomp.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

public class SmsUtil {

    /**
     * 发送短信
     *
     * @param phoneNumbers  手机号
     * @param templateCode  短信模板号
     * @param signName      短信签名
     * @param templateParam 验证码
     */
    public static void sendSms(String phoneNumbers, String templateCode, String signName, String templateParam) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4GH4gqwuNJnmijHh15F1", "R7bglZy91kFxs2F675Rb15f2UuznfD");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        /*
         * "TemplateParam"参数要求传一个JSON字符串, 如: "{\"code\":\"271137\"}",
         * 里面要传入你这个短信模板中的变量;
         */
        request.putQueryParameter("TemplateParam", templateParam);
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
