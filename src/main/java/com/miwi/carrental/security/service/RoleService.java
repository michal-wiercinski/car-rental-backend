package com.miwi.carrental.security.service;

import com.miwi.carrental.control.exception.MyResourceNotFoundException;
import com.miwi.carrental.control.repository.RoleDao;
import com.miwi.carrental.control.service.generic.GenericService;
import com.miwi.carrental.domain.entity.Role;
import com.miwi.carrental.domain.enums.RoleName;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoleService extends GenericService<Role> {

  private final RoleDao roleDao;

  public RoleService(final RoleDao roleDao) {
    this.roleDao = roleDao;
  }

  public Role findByRoleName(RoleName roleName) {
    try {
      return checkFound(roleDao.findByName(roleName));
    } catch (MyResourceNotFoundException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "The role name: " + roleName + " was not found",
          ex);
    }
  }

  @Override
  public Role save(Role role) {
    return roleDao.save(role);
  }

  @Override
  public Role findById(Long id) {
    try {
      return checkFound(roleDao.findById(id));
    } catch (MyResourceNotFoundException ex) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "The role with id: " + id + " was not found",
          ex);
    }
  }
}