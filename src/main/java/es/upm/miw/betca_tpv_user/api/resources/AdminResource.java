package es.upm.miw.betca_tpv_user.api.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(AdminResource.ADMINS)
@Profile("prod")
public class AdminResource {

    public static final String ADMINS = "/admins";
    public static final String DB = "/db";

    private AdminService adminService;

    @Autowired
    public AdminResource(AdminService adminService) {
        this.adminService = adminService;
    }

    @DeleteMapping(value = DB)
    public void deleteDb() {
        this.adminService.deleteAllAndInitialize();
    }

    @PostMapping(value = DB)
    public void seedDb() {
        this.adminService.seedDataBase();
    }
}
