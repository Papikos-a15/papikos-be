package id.ac.ui.cs.advprog.papikosbe.repository.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface KosRepository extends JpaRepository<Kos, UUID> {
}