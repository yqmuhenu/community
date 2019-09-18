package fun.passerby.community.controller;

import fun.passerby.community.domain.User;
import fun.passerby.community.dto.AccessTokenDTO;
import fun.passerby.community.dto.GithubUser;
import fun.passerby.community.service.UserService;
import fun.passerby.community.util.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletRequest request){
        //构造accessTokenDTO对象
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        //根据accessTokenDTO获取access_token
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        //根据access_token获取user信息
        GithubUser githubUser = githubProvider.getUser(accessToken);
        //System.out.println(user);
        if (githubUser != null){
            User user = new User();
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setToken(UUID.randomUUID().toString());
            //向数据库中插入
            userService.addUser(user);
            //登录成功
            request.getSession().setAttribute("user",githubUser);
            request.getSession().setMaxInactiveInterval(1800);
            //System.out.println(githubUser);
            return "redirect:/";
        }else {
            //登录失败，重新登录
            return "redirect:/";
        }
    }
}
