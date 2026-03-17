package controller;

import entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 1. 查询 GET
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id) {
        return "查询成功，正在返回ID为" + id + "的用户信息";
    }

    // 2. 新增 POST
    @PostMapping
    public String createUser(@RequestBody User user) {
        return "新增成功，接收到用户：" + user.getName() + "，年龄：" + user.getAge();
    }

    // 3. 更新 PUT
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User user) {
        return "更新成功，ID" + id + "的用户已修改为：" + user.getName();
    }

    // 4. 删除 DELETE
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return "删除成功，已移除ID为" + id + "的用户";
    }
}