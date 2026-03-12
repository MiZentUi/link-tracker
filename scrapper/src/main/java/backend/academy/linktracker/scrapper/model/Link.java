package backend.academy.linktracker.scrapper.model;

import java.util.List;

import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.validation.annotation.Validated;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Validated
public class Link {
    private Long id;

    @UniqueElements
    private List<Long> chatIds;

    @URL
    private String url;

    private List<String> tags;
}