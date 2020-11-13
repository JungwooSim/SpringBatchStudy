package me.springbatchstudy.model;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DuplicationTest {
    Set<String> value = new HashSet<>();

    public void addText(String string) {
        this.value.add(string);
    }
    public Set<String> getValue() {
        return value;
    }
}
