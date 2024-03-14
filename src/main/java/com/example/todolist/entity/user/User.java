package com.example.todolist.entity.user;

import com.example.todolist.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id; // 고유 식별자
    private String userEmail; // 이메일
    private String userPassword; // 비밀번호
    private String userName; // 이름
    private String userPhone; // 전화번호


    /** 생성자 **/
    protected User() {

    }

    public User(String userEmail, String userPassword, String userName, String userPhone) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userPhone = userPhone;
    }
}
