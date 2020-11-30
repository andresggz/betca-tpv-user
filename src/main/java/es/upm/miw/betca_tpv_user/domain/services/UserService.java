package es.upm.miw.betca_tpv_user.domain.services;

import es.upm.miw.betca_tpv_user.data.daos.UserRepository;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.data.model.User;
import es.upm.miw.betca_tpv_user.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_user.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_user.domain.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional< String > login(String mobile) {
        return this.userRepository.findByMobile(mobile)
                .map(user -> JwtUtil.createToken(user.getMobile(), user.getFirstName(), user.getRole().name()));
    }

    public void createUser(User user, Role roleClaim) {
        if (!authorizedRoles(roleClaim).contains(user.getRole())) {
            throw new ForbiddenException("Insufficient role to create this user: " + user);
        }
        this.noExistByMobile(user.getMobile());
        user.setRegistrationDate(LocalDateTime.now());
        this.userRepository.save(user);
    }

    public Stream< User > readAll(Role roleClaim) {
        return this.userRepository.findByRoleIn(authorizedRoles(roleClaim)).stream();
    }

    private List< Role > authorizedRoles(Role roleClaim) {
        List< Role > roles = new ArrayList<>();
        switch (roleClaim) {
            case ADMIN:
                roles.add(Role.ADMIN);
            case MANAGER:
                roles.add(Role.MANAGER);
                roles.add(Role.OPERATOR);
            case OPERATOR:
                roles.add(Role.CUSTOMER);
                break;
            default:
                throw new ForbiddenException("Insufficient role to create users");
        }
        return roles;
    }

    private void noExistByMobile(String mobile) {
        if (this.userRepository.findByMobile(mobile).isPresent()) {
            throw new ConflictException("The mobile already exists: " + mobile);
        }
    }

    public Stream< User > findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
            String mobile, String firstName, String familyName, String email, String dni, Role roleClaim) {
        return this.userRepository.findByMobileAndFirstNameAndFamilyNameAndEmailAndDniContainingNullSafe(
                mobile, firstName, familyName, email, dni, this.authorizedRoles(roleClaim)
        ).stream();
    }

    public User ReadByMobile(String mobile) {
        return this.userRepository.findByMobile(mobile).orElseThrow(() -> new NotFoundException("The mobile don't exist: " + mobile));
    }
}
