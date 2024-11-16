//package edu.capstone4.userserver.controllers;
//
//import edu.capstone4.userserver.models.Role;
//import edu.capstone4.userserver.services.RoleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import jakarta.validation.Valid;
//
//import java.util.List;
//
//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
//@RequestMapping("/api/roles")
//public class RoleController {
//
//    @Autowired
//    private RoleService roleService;
//
//    @PostMapping
//    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
//        return ResponseEntity.status(201).body(roleService.createRole(role));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
//        return ResponseEntity.ok(roleService.getRoleById(id));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Role>> getAllRoles() {
//        return ResponseEntity.ok(roleService.getAllRoles());
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Role> updateRole(@PathVariable Long id, @Valid @RequestBody Role role) {
//        return ResponseEntity.ok(roleService.updateRole(id, role));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
//        roleService.deleteRole(id);
//        return ResponseEntity.noContent().build();
//    }
//}
