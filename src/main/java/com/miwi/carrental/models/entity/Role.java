package com.miwi.carrental.models.entity;

import com.miwi.carrental.models.enums.ERoleName;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Table(name = "role")
@Entity
public class Role implements Serializable {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "PK_role")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_name")
  private ERoleName name;

  @ManyToMany(mappedBy = "roles")
  private List<User> userList;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ERoleName getName() {
    return name;
  }

  public void setName(ERoleName name) {
    this.name = name;
  }

  public List<User> getUserList() {
    return userList;
  }

  public void setUserList(List<User> userList) {
    this.userList = userList;
  }
}