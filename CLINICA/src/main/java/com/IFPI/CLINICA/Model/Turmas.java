package com.IFPI.CLINICA.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbTurmas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turmas {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    @Column(nullable = false, length = 100)
    private String nome;

    
    @Column(length = 255)
    private String descricao;

    
    @Override
    public String toString() {
        return nome;
    }
}
