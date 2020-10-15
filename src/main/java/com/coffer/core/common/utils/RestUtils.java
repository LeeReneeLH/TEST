package com.coffer.core.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RestUtils {

    private static RestTemplate restTemplate = SpringContextHolder.getBean(RestTemplate .class);

    /**
     * 日志对象
     */
    private static Logger logger = LoggerFactory.getLogger(RestUtils.class);

    /**
     * Json实例对象
     */
    protected static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性
            .enableComplexMapKeySerialization() // 支持Map的key为复杂对象的形式
            .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss")// 时间转化为特定格式
            //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)// 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
            .setPrettyPrinting() // 对json结果格式化.
            // .setVersion(1.0)
            // //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
            // @Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
            // @Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
            .create();

    /**
     * 发送请求(json)
     *
     * @param url    请求地址
     * @return 响应信息Map
     * @author wqj
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> sendPostRequest(String json, String url) {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(json, headers);
        // 发送请求,返回结果
        logger.info("-------------------请求发送开始-------------------");
        logger.info("请求地址:{}",url);
        logger.info("请求参数:{}",json);
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, formEntity, String.class);
        String s = stringResponseEntity.getBody();
        logger.info("请求返回结果:{}",s);
        logger.info("---------------------请求结束---------------------");
        Map<String, Object> resultMap = gson.fromJson(s, Map.class);
        return resultMap;
    }
}
