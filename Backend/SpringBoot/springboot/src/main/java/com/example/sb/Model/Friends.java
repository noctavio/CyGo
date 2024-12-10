package com.example.sb.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "friends")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int friends_id; // Primary key

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // The user who owns this friends list

    @ElementCollection
    @CollectionTable(name = "friend_list", joinColumns = @JoinColumn(name = "friends_id"))
    @Column(name = "friend_name")
    private List<String> friends = new ArrayList<>(); // List of friends

    public Friends(User user){
        this.user = user;
    }
}
