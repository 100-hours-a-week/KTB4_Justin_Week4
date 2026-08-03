package com.example.community.entity;

import lombok.Getter;

@Getter
public enum Genre {

    POP("팝"),
    ROCK("록"),
    HIP_HOP("힙합"),
    RNB("R&B"),
    ELECTRONIC("일렉트로닉"),
    JAZZ("재즈"),
    BLUES("블루스"),
    FOLK_COUNTRY("포크/컨트리"),
    BALLAD("발라드"),
    CLASSICAL("클래식"),
    METAL("메탈"),
    REGGAE("레게"),
    ETC("기타");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }
}
