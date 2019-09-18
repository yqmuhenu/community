package fun.passerby.community.provider;

import com.alibaba.fastjson.JSON;
import fun.passerby.community.dto.AccessTokenDTO;
import fun.passerby.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {

    /**
     * 根据accessTokenDTO获取access_token
     * @param accessTokenDTO
     * @return
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            //OkHttp请求回调中response.body().string()只能有效调用一次,
            //若后面代码再次调用，则会报异常：java.lang.IllegalStateException: closed
            String string = response.body().string();
            //access_token=5231deeb58c0936423034f798195b92c9b86e23d
            //&scope=user
            //&token_type=bearer
            String access_token = string.split("&")[0].split("=")[1];
            //System.out.println(string);
            return access_token;
        } catch (Exception e){
            //throw new RuntimeException(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据access_token获取user信息
     * @param accessToken
     * @return
     */
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            //将String对象转换成指定类对象
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
