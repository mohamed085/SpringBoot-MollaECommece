package com.molla.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StateDTO {
    private Integer id;
    private String name;

    @Override
    public String toString() {
        return "StateDTO [id=" + id + ", name=" + name + "]";
    }

}
