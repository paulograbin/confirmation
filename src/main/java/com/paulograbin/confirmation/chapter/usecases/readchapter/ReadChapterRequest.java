package com.paulograbin.confirmation.chapter.usecases.readchapter;

import lombok.Data;


@Data
public class ReadChapterRequest {

    public long requestingUser;
    public long chapterId;

}
