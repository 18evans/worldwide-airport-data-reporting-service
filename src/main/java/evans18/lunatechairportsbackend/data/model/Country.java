package evans18.lunatechairportsbackend.data.model;

import lombok.Data;

@Data
public class Country {
    private String id;
    private String code;
    private String name;
    private String continent;
    private String wikipedia_link;
    private String keywords;
}
