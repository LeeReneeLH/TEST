package com.coffer.businesses.modules.doorOrder.app.v01.utils;

import com.coffer.core.common.config.Global;
import io.jsonwebtoken.*;

import java.util.Date;
import java.util.Map;

public class TokenUtils {

    /**
     * 根据userId和openid生成token
     */
    public static String generateToken(Map<String, Object> obj) {
        return createJWT(obj);
    }

    /**
     * 生成token
     *
     * @param claims 用户信息的关键部分
     * @return token字符串
     */
    private static String createJWT(Map<String, Object> claims) {
        long issueTime = System.currentTimeMillis(); //获取当前时间，用作token签发时间和token失效的时间计算
        Date now = new Date(issueTime);
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, Global.getConfig("miniProgram.tokenSecret"));
        long expireTime = Long.parseLong(Global.getConfig("miniProgram.tokenExpTime"));
        if (expireTime >= 0) {
            long expMillis = issueTime + expireTime;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp); //设置过期时间
        }
        return builder.compact();
    }

    /**
     * token验证，要保证签发token时使用的密钥和解密时的密钥是一个，密钥只能在服务端使用
     *
     * @param token 客户端发送时带的token
     * @return true：验证通过 false：验证失败
     */
    public static Claims verify(String token) {
        if (token == null) {
            throw new JwtException("token lost");
        }
        try {
            return Jwts.parser().setSigningKey(Global.getConfig("miniProgram.tokenSecret")).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

}
