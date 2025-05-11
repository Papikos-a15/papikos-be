// src/main/java/id/ac/ui/cs/advprog/papikosbe/service/OwnerService.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;

import java.util.List;
import java.util.UUID;

public interface OwnerService {
    /**
     * Approve the owner with the given ID.
     * @param ownerId the ID of the owner to approve
     * @return the updated Owner
     */
    Owner approve(UUID ownerId);
    /**
     * Find all owners that are not approved.
     * @return a list of unapproved owners
     */
    List<Owner> findUnapprovedOwners();
}
