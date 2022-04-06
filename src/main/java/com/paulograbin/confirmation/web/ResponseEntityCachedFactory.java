package com.paulograbin.confirmation.web;

import org.springframework.http.CacheControl;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
public class ResponseEntityCachedFactory {

    public static CacheControl TEN_MINUTES = CacheControl.maxAge(Duration.ofMinutes(10)).cachePrivate();
    public static CacheControl NO_CACHE = CacheControl.noStore();


}
