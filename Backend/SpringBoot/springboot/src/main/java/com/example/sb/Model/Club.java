package com.example.sb.Model;

import com.example.sb.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

@Entity
@Data
@Table(name = "clubs")

@AllArgsConstructor
@NoArgsConstructor
public class
Club {

    @Getter
    @Setter
    @jakarta.persistence.Id
    @Id()
    @Column(name = "ID")
    @GeneratedValue
    private int id; // this is a primary key as annotated by @Id() may change/delete in the future
    // Foreign key mapping to (registered_users)
    
    @Column(name = "club_name")
    private String clubname;
    @Column(name = "club_picture")
    private String clubpicture;
    @Column(name = "member1")
    private Integer member1;
    @Column(name = "member2")
    private Integer member2;
    @Column(name = "member3")
    private Integer member3;
    @Column(name = "member4")
    private Integer member4;
    @Column(name = "member5")
    private Integer member5;
    @Column(name = "member6")
    private Integer member6;
    @Column(name = "member7")
    private Integer member7;
    @Column(name = "member8")
    private Integer member8;
    @Column(name = "member9")
    private Integer member9;
    @Column(name = "member10")
    private Integer member10;
    @Column(name = "member11")
    private Integer member11;
    @Column(name = "member12")
    private Integer member12;
    @Column(name = "member13")
    private Integer member13;
    @Column(name = "member14")
    private Integer member14;
    @Column(name = "member15")
    private Integer member15;
    @Column(name = "member16")
    private Integer member16;
    @Column(name = "member17")
    private Integer member17;
    @Column(name = "member18")
    private Integer member18;
    @Column(name = "member19")
    private Integer member19;
    @Column(name = "member20")
    private Integer member20;
}

