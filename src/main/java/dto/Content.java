package dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Content {
    private String content;
    private String due_string;
    private String due_lang;
    private Integer priority;
}
