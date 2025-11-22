package com.ittaro.drug.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @className: Customer
 * @author: Tarol1n
 * @date: 2025/11/22 18:38
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String phone;       // 手机号（主键）
    private String name;        // 姓名
    private Level level;        // 会员等级
    private double totalSpent;  // 累计消费金额

    // 枚举定义会员等级
    public enum Level {
        NORMAL("普通会员"),
        VIP("VIP会员");

        private final String desc;

        Level(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return desc;
        }
    }
}