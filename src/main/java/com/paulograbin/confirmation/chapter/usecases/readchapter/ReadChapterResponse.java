package com.paulograbin.confirmation.chapter.usecases.readchapter;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ReadChapterResponse {

    public long id;
    public String name;
    public boolean master;
    public int memberCount;

    public List<Member> members = new ArrayList();


    @Data
    static class Member {

        public String firstName;
        public String lastName;
        public String userName;
        public String email;
        public boolean active;

        public Member(String firstName, String lastName, String userName, String email, boolean active) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.userName = userName;
            this.email = email;
            this.active = active;
        }
    }
}
