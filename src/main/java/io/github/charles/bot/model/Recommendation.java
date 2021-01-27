package io.github.charles.bot.model;

import io.github.charles.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {


    private String question;
    private String answer;
    private String createdBy;

    @Builder.Default
    private MessageType type = MessageType.Text;

    @Builder.Default
    private int rank = 1;

    @Builder.Default
    private long creationTimestamp = new Date().getTime();
}
