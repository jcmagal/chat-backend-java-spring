package com.infinity.chat.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserStatusEvent {
        private String nickName;
        private String fullName;
        private String status;
}
