package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clubs")
@AllArgsConstructor
@NoArgsConstructor
public class Club {

    @Getter
    @Setter
    @jakarta.persistence.Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key

    @Getter
    @Setter
    @Column(name = "club_name")
    private String clubName;

    @Getter
    @Setter
    @Column(name = "club_picture")
    private String clubPicture;

    @ElementCollection
    @Getter
    @Setter
    @CollectionTable(name = "club_members", joinColumns = @JoinColumn(name = "club_id"))
    @Column(name = "member_name")
    private List<String> members = new ArrayList<>(); // List of member names

}

