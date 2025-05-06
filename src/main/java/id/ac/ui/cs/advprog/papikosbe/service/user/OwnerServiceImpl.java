// src/main/java/id/ac/ui/cs/advprog/papikosbe/service/impl/OwnerServiceImpl.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.repository.user.OwnerRepository;
import id.ac.ui.cs.advprog.papikosbe.service.user.OwnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepo;

    public OwnerServiceImpl(OwnerRepository ownerRepo) {
        this.ownerRepo = ownerRepo;
    }

    @Override
    @Transactional
    public Owner approve(Long ownerId) {
        // TODO: fetch owner, set approved, save & return
        throw new UnsupportedOperationException("approve not implemented");
    }
}
