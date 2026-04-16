package com.deicmar.checklist.model.entity;

import com.deicmar.checklist.model.enums.ResultadoChecklist;
import com.deicmar.checklist.model.enums.Turno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "checklists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaVistoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Turno turno;

    @Column(nullable = false)
    private Integer horimetroInicial;

    private Integer horimetroFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = false)
    private Usuario operador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empilhadeira_id", nullable = false)
    private Empilhadeira empilhadeira;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoChecklist resultado;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemChecklist> itens = new ArrayList<>();

    @Column(length = 1000)
    private String observacaoGeral;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Métodos auxiliares
    public String getDiaSemana() {
        if (data == null) return "";
        DayOfWeek dayOfWeek = data.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
    }

    public void adicionarItem(ItemChecklist item) {
        itens.add(item);
        item.setChecklist(this);
    }

    public void removerItem(ItemChecklist item) {
        itens.remove(item);
        item.setChecklist(null);
    }
}
