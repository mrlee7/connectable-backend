package com.backend.connectable.artist.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtistComment {

    private Long id;
    private String nickname;
    private LocalDateTime createdDate;
    private String contents;

    @QueryProjection
    public ArtistComment(Long id, String nickname, LocalDateTime createdDate, String contents) {
        this.id = id;
        this.nickname = nickname;
        this.createdDate = createdDate;
        this.contents = contents;
    }
}
