package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopUpServiceImpl implements TopUpService {

    private final Map<UUID, TopUp> topUpRepository = new HashMap<>();

    @Override
    public TopUp create(TopUp topUp) {
        topUpRepository.put(topUp.getId(), topUp);
        return topUp;
    }

    @Override
    public List<TopUp> findAll() {
        return new ArrayList<>(topUpRepository.values());
    }

    @Override
    public TopUp findById(UUID id) {
        return topUpRepository.get(id);
    }
}
