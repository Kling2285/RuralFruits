package com.example.ruralfruits.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import java.util.Date;

public class TokenUtils {
    /**
     * 生成token（核心修改：新增 type 参数，存入角色类型）
     * @param data 载荷（原来的 userId）
     * @param sign 签名秘钥（原来的 password）
     * @param type 角色类型（manager/admin/user/merchant，新增参数）
     * @return 携带角色类型的 JWT Token
     */
    public static String createToken(String data, String sign, String type) {
        return JWT.create()
                .withAudience(data) // 保留原有：存入 userId
                .withClaim("type", type) // 新增：存入角色类型（关键！）
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1)) // 保持原有：1天过期
                .sign(Algorithm.HMAC256(sign)); // 保持原有：HMAC256 加密
    }

    /**
     * 新增：从 Token 中解析角色类型（给订单接口用）
     * @param token 前端传递的 Token
     * @return 角色类型（manager/user/merchant），解析失败返回 null
     */
    public static String getUserType(String token) {
        try {
            return JWT.decode(token).getClaim("type").asString();
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保留原有：从 Token 中解析 userId（如果需要）
     * @param token 前端传递的 Token
     * @return userId，解析失败返回 null
     */
    public static String getUserId(String token) {
        try {
            return JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
