package com.transaction.service;

import com.transaction.entity.Merchant;
import com.transaction.repository.MerchantRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public Optional<Merchant> getMerchantById(UUID merchantUuid) {
        return merchantRepository.findById(merchantUuid);
    }

    public Optional<Merchant> getMerchantByEmail(String email) {
        return merchantRepository.findByMerchantEmail(email);
    }

    public Merchant createMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    public void deleteMerchant(UUID merchantUuid) {
        merchantRepository.deleteById(merchantUuid);
    }
}
