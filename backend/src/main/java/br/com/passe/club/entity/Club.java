package br.com.passe.club.entity;

import br.com.passe.club.converter.FormationConverter;
import br.com.passe.club.enums.Formation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "clubes")
@Getter
@Setter
@NoArgsConstructor
public class Club {

    public static final BigDecimal INITIAL_CASH = new BigDecimal("300.00");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 60)
    private String name;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "sigla", nullable = false, length = 3)
    private String acronym;

    @Column(name = "caixa", nullable = false, precision = 10, scale = 2)
    private BigDecimal cash = INITIAL_CASH;

    @Convert(converter = FormationConverter.class)
    @Column(name = "formacao", length = 5)
    private Formation formation;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}