package com.example.todolist.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Data // anotasi lombok buat bikin geterr otomatis
@AllArgsConstructor // buat constructor yang membuhtukan semua field(argument)
@NoArgsConstructor  // buat constructor tampah argument
@Entity
@Table(name = "category")
public class Category {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@Column(name = "name", nullable = false)
    private String name;

@Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

@Column(name = "updated_at")
    private LocalDateTime updatedAt;

@PrePersist // anotasi buat data waktu secara otomatis ketika data pertama kali di buat
    public void prePersits(){
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
}

@PreUpdate // anotasi buat data wkatu secara otomatis ketika data di update
    public  void  onUpdate(){
    this.updatedAt= LocalDateTime.now();
}
}
