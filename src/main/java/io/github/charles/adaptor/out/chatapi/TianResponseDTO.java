package io.github.charles.adaptor.out.chatapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TianResponseDTO {
    String code;
    String msg;
    List<News> newslist;
}
