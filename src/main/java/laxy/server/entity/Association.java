package laxy.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "original_tag_id")
    private Tag originalTag;

    @ManyToOne
    @JoinColumn(name = "related_tag_id")
    private Tag relatedTag;

    private int weight;

    public Association(Tag originalTag, Tag relatedTag, int weight) {
        this.originalTag = originalTag;
        this.relatedTag = relatedTag;
        this.weight = weight;
    }
}
