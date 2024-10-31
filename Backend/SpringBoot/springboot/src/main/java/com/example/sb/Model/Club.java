package com.example.sb.Entity;

import jakarta.persistence.*;

@Entity()
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @Column(name = "club_name")
    public String CLUB;
    @Column(name = "club_picture")
    private String CLUB_PICTURE;
    private String member1;
    private String member2;
    private String member3;
    private String member4;
    private String member5;
    private String member6;
    private String member7;
    private String member8;
    private String member9;
    private String member10;
    private String member11;
    private String member12;
    private String member13;
    private String member14;
    private String member15;
    private String member16;
    private String member17;
    private String member18;
    private String member19;
    private String member20;

    public Club() {
    }
    // Constructor with 1 member
    public Club(String CLUB, String pic, String member1) {
        this.CLUB = CLUB;
        this.CLUB_PICTURE = pic;
        this.member1 = member1;
        this.member2 = "empty";
        this.member3 = "empty";
        this.member4 = "empty";
        this.member5 = "empty";
        this.member6 = "empty";
        this.member7 = "empty";
        this.member8 = "empty";
        this.member9 = "empty";
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 2 members
    public Club(String CLUB, String pic, String username1, String username2) {
        this.CLUB = CLUB;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = "empty";
        this.member4 = "empty";
        this.member5 = "empty";
        this.member6 = "empty";
        this.member7 = "empty";
        this.member8 = "empty";
        this.member9 = "empty";
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 3 members
    public Club(String CLUB, String pic, String username1, String username2, String username3) {
        this.CLUB = CLUB;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = "empty";
        this.member5 = "empty";
        this.member6 = "empty";
        this.member7 = "empty";
        this.member8 = "empty";
        this.member9 = "empty";
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 4 members
    public Club(String CLUB, String pic, String username1, String username2, String username3, String username4) {
        this.CLUB = CLUB;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = "empty";
        this.member6 = "empty";
        this.member7 = "empty";
        this.member8 = "empty";
        this.member9 = "empty";
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 5 members
    public Club(String CLUB, String pic, String username1, String username2, String username3, String username4, String username5) {
        this.CLUB = CLUB;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = "empty";
        this.member7 = "empty";
        this.member8 = "empty";
        this.member9 = "empty";
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 6 members
    public Club(String CLUB, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6) {
        this.CLUB = CLUB;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = "empty";
        this.member8 = "empty";
        this.member9 = "empty";
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 7 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = "empty";
        this.member9 = "empty";
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 8 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = "empty";
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 9 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = "empty";
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 10 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = "empty";
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 11 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = "empty";
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 12 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = "empty";
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 13 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12, String username13) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = username13;
        this.member14 = "empty";
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 14 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12, String username13, String username14) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = username13;
        this.member14 = username14;
        this.member15 = "empty";
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 15 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12, String username13, String username14,
                String username15) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = username13;
        this.member14 = username14;
        this.member15 = username15;
        this.member16 = "empty";
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 16 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12, String username13, String username14,
                String username15, String username16) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = username13;
        this.member14 = username14;
        this.member15 = username15;
        this.member16 = username16;
        this.member17 = "empty";
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 17 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12, String username13, String username14,
                String username15, String username16, String username17) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = username13;
        this.member14 = username14;
        this.member15 = username15;
        this.member16 = username16;
        this.member17 = username17;
        this.member18 = "empty";
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 18 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12, String username13, String username14,
                String username15, String username16, String username17, String username18) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = username13;
        this.member14 = username14;
        this.member15 = username15;
        this.member16 = username16;
        this.member17 = username17;
        this.member18 = username18;
        this.member19 = "empty";
        this.member20 = "empty";
    }

    // Constructor with 19 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12, String username13, String username14,
                String username15, String username16, String username17, String username18, String username19) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = username13;
        this.member14 = username14;
        this.member15 = username15;
        this.member16 = username16;
        this.member17 = username17;
        this.member18 = username18;
        this.member19 = username19;
        this.member20 = "empty";
    }

    // Constructor with 20 members
    public Club(String name, String pic, String username1, String username2, String username3, String username4,
                String username5, String username6, String username7, String username8, String username9,
                String username10, String username11, String username12, String username13, String username14,
                String username15, String username16, String username17, String username18, String username19,
                String username20) {
        this.CLUB = name;
        this.CLUB_PICTURE = pic;
        this.member1 = username1;
        this.member2 = username2;
        this.member3 = username3;
        this.member4 = username4;
        this.member5 = username5;
        this.member6 = username6;
        this.member7 = username7;
        this.member8 = username8;
        this.member9 = username9;
        this.member10 = username10;
        this.member11 = username11;
        this.member12 = username12;
        this.member13 = username13;
        this.member14 = username14;
        this.member15 = username15;
        this.member16 = username16;
        this.member17 = username17;
        this.member18 = username18;
        this.member19 = username19;
        this.member20 = username20;
    }

    // Getters and Setters for all member variables

    public String getCLUB() {
        return CLUB;
    }

    public void setCLUB(String CLUB) {
        this.CLUB = CLUB;
    }

    public String getCLUB_PICTURE() {
        return CLUB_PICTURE;
    }

    public void setCLUB_PICTURE(String CLUB_PICTURE) {
        this.CLUB_PICTURE = CLUB_PICTURE;
    }

    public String getMember1() {
        return member1;
    }

    public void setMember1(String member1) {
        this.member1 = member1;
    }

    public String getMember2() {
        return member2;
    }

    public void setMember2(String member2) {
        this.member2 = member2;
    }

    public String getMember3() {
        return member3;
    }

    public void setMember3(String member3) {
        this.member3 = member3;
    }

    public String getMember4() {
        return member4;
    }

    public void setMember4(String member4) {
        this.member4 = member4;
    }

    public String getMember5() {
        return member5;
    }

    public void setMember5(String member5) {
        this.member5 = member5;
    }

    public String getMember6() {
        return member6;
    }

    public void setMember6(String member6) {
        this.member6 = member6;
    }

    public String getMember7() {
        return member7;
    }

    public void setMember7(String member7) {
        this.member7 = member7;
    }

    public String getMember8() {
        return member8;
    }

    public void setMember8(String member8) {
        this.member8 = member8;
    }

    public String getMember9() {
        return member9;
    }

    public void setMember9(String member9) {
        this.member9 = member9;
    }

    public String getMember10() {
        return member10;
    }

    public void setMember10(String member10) {
        this.member10 = member10;
    }

    public String getMember11() {
        return member11;
    }

    public void setMember11(String member11) {
        this.member11 = member11;
    }

    public String getMember12() {
        return member12;
    }

    public void setMember12(String member12) {
        this.member12 = member12;
    }

    public String getMember13() {
        return member13;
    }

    public void setMember13(String member13) {
        this.member13 = member13;
    }

    public String getMember14() {
        return member14;
    }

    public void setMember14(String member14) {
        this.member14 = member14;
    }

    public String getMember15() {
        return member15;
    }

    public void setMember15(String member15) {
        this.member15 = member15;
    }

    public String getMember16() {
        return member16;
    }

    public void setMember16(String member16) {
        this.member16 = member16;
    }

    public String getMember17() {
        return member17;
    }

    public void setMember17(String member17) {
        this.member17 = member17;
    }

    public String getMember18() {
        return member18;
    }

    public void setMember18(String member18) {
        this.member18 = member18;
    }

    public String getMember19() {
        return member19;
    }

    public void setMember19(String member19) {
        this.member19 = member19;
    }

    public String getMember20() {
        return member20;
    }

    public void setMember20(String member20) {
        this.member20 = member20;
    }
}