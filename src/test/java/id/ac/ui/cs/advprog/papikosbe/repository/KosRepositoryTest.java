package id.ac.ui.cs.advprog.papikosbe.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KosRepositoryTest {

    @Autowired
    private KosRepository kosRepository;

    @BeforeEach
    public void setUp() {}

    @Test
    public void testAddKos() {}

    @Test
    public void testGetKostById() {}

    @Test
    public void testGetAllKos() {}

    @Test
    public void testUpdateKos() {}

    @Test
    public void testDeleteKos() {}
}
