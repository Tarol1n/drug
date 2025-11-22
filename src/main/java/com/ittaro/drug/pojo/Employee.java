package com.ittaro.drug.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @className: Employee
 * @author: Tarol1n
 * @date: 2025/11/22 18:38
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;  // 用户名（登录用）
    private String password;  // 密码（明文存储，仅用于演示）
    private Role role;        // 角色：管理员 / 普通员工

    // 角色枚举
    public enum Role {
        ADMIN("管理员"),
        STAFF("普通员工");

        private final String desc;

        Role(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return desc;
        }
    }
}