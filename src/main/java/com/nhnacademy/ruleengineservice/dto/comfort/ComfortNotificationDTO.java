package com.nhnacademy.ruleengineservice.dto.comfort;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ComfortNotificationDTO {
    String location;

    @JsonProperty("comfort-index")
    ComfortIndexDTO comfortIndex;

    @JsonProperty("ai-comment")
    AiCommentDTO aiComment;
}
