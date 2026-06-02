package fr.efrei.nanoorbit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String roles;
}
