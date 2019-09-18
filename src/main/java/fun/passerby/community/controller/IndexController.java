package fun.passerby.community.controller;

import fun.passerby.community.domain.User;
import fun.passerby.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        User user = null;
        Cookie[] cookies = request.getCookies();
        //System.out.println("cookies == null : " + (null == cookies));
        if (null != cookies){
            for(Cookie cookie : cookies){
                if ("token".equals(cookie.getName())){
                    String token = cookie.getValue();
                    user = userService.selectByToken(token);
                    break;
                }
            }
        }
        if (null != user){
            request.getSession().setAttribute("user",user);
        }
        return "index";
    }
}
