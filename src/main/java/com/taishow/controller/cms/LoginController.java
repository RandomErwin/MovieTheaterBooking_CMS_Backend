package com.taishow.controller.cms;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.taishow.annotation.PermissionAnnotation;
import com.taishow.dao.UserDao;
import com.taishow.entity.User;
import com.taishow.util.JwtUtilForCms;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/permission")
public class LoginController {

    private UserDao userDao;

    @Autowired
    private JwtUtilForCms jwtUtil;

    public LoginController(UserDao userDao) {
        this.userDao = userDao;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginPost(@RequestBody User user, HttpServletRequest request) {
        // user => 使用者輸入
        Optional<User> optionalUser = userDao.findByAccount(user.getAccount());
        if(optionalUser.isPresent()) {
            // AdminInfo => 從數據庫取得
            User AdminInfo = optionalUser.get();
            if("admin".equals(AdminInfo.getAccount())) {
                // 將用戶的密碼轉換成字串陣列 user.getPasswd().toCharArray() 比對 AdminInfo.getPasswd()從數據庫中查詢到使用者對象
                BCrypt.Result result = BCrypt.verifyer().verify(user.getPasswd().toCharArray(), AdminInfo.getPasswd());
                if(result.verified){
                    String token = jwtUtil.generateToken(
                            AdminInfo.getId(),
                            AdminInfo.getAccount()
                    );
                    return ResponseEntity.ok(token);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號密碼有誤");
    }

}
