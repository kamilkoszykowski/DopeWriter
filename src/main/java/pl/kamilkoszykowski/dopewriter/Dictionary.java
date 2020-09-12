package pl.kamilkoszykowski.dopewriter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dictionary {

    private String Word;

    public Dictionary(String word) {
        Word = word;
    }
}
