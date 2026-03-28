package backend.academy.linktracker.scrapper.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.URL;
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

    private List<Long> chatIds;

    @URL
    private String url;

    private Set<String> tags;

    @Builder.Default
    private LocalDateTime lastUpdate = LocalDateTime.MIN;
}