package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.BEtfUG66g7ufTMKRTeeVIj8xwLE5SbcyBKwUM0Als7rqDf8RP3Vl6zd7vUJ9HKXAip250_F-aUCiXrFDzGNE3BSqn2-TQq_gfoDMSIBicspmiz1rnJy86rineQYT016WRFoH6Hhs8HOvsmF5dNuBIQaqGy9eTy3iNn5cLqyKmdzN7_8wy8EOJP40qEkrWb88knVqqy2lisk--JUuxSuRKLID6KpQEQI8fBb0DMKt08pYF6yMR5fuTFuiRDlDgToZ10_02A8jSatVg8ClC4rEG4F-4BKJ2zNL6Y8WUk7n4B9AgckDICLsbJ8fzgt6rmOLsXsMMsGxuLqmSezwRO7hog";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgyjQNCFHiAuAcP4e8/2XAzN1VOkbp8QP98JrH1HkkuNXnMUCcmoB3hvDOQI15YtUTvJxo5j0Nx/ZwbqyQAb1IBNKjCPwd7SvsrCmgM4Ivp/GnsdLZGv42/9qJh1Tn7TzkFN0aJppFk2jalKhc/ypT9TvIpEdEW2WG3eyC1flV0nPWg8Y4WD8VlO9yA3xnp6K+5skN6Kqsth8bU0x8GGjztKSbl0dWv+nLglU9gFLlhQVp+wfvd7Ar7kMTL9lffuGUlY+zdIcMZvHMVcRcZd+YBlcXlXub6tXQ5FbnXMT6o091d6BYxfAyLvETL6bIKpAsGdA61OtgTKFd9rJhP1U/QIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
