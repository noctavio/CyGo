package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clubs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Club {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int club_id; // Primary key

    @Column(name = "club_name")
    private String clubName;

    @Column(name = "club_picture")
    private String clubPicture;

    @ElementCollection
    @CollectionTable(name = "club_members", joinColumns = @JoinColumn(name = "club_id"))
    @Column(name = "member_name")
    private List<String> members = new ArrayList<>(); // List of member names

}