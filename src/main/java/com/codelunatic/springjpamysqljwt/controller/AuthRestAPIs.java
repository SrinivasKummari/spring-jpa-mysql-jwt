package com.codelunatic.springjpamysqljwt.controller;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codelunatic.springjpamysqljwt.jwt.JwtProvider;
import com.codelunatic.springjpamysqljwt.model.Role;
import com.codelunatic.springjpamysqljwt.model.RoleName;
import com.codelunatic.springjpamysqljwt.model.User;
import com.codelunatic.springjpamysqljwt.model.UserRole;
import com.codelunatic.springjpamysqljwt.repository.RoleRepository;
import com.codelunatic.springjpamysqljwt.repository.UserRepository;
import com.codelunatic.springjpamysqljwt.repository.UserRoleRepository;
import com.codelunatic.springjpamysqljwt.request.LoginForm;
import com.codelunatic.springjpamysqljwt.request.SignUpForm;
import com.codelunatic.springjpamysqljwt.response.JwtResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;
    
    @Autowired
    private UserRoleRepository userRoleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<String>("Fail -> Username is already taken!",
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<String>("Fail -> Email is already in use!",
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));
        System.out.println("user name: "+signUpRequest.getName());
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        System.out.println("user roles fresh: "+signUpRequest.getRoles());
        strRoles.forEach(role -> {
        	switch(role) {
	    		case "admin":
	    			Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
	                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
	    			roles.add(adminRole);
	    			
	    			break;
	    		case "pm":
	            	Role pmRole = roleRepository.findByRoleName(RoleName.ROLE_PM)
	                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
	            	roles.add(pmRole);
	            	
	    			break;
	    		default:
	        		Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
	                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
	        		roles.add(userRole);        			
        	}
        });
        
        user.setRoles(roles);
        user =userRepository.save(user);
        for(Role role: user.getRoles()){
        	UserRole userRole = new UserRole();
        	userRole.setRoleId(role.getRoleId());
        	userRole.setUserId(user.getUserId());
        	userRoleRepository.save(userRole);
        }
        return ResponseEntity.ok().body("User registered successfully!");
    }
}