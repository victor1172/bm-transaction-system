package com.transaction.service;

import com.transaction.dto.MerchantRequest;
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

    public Merchant createMerchant(MerchantRequest request) {
        Merchant merchant = new Merchant();
        merchant.setMerchantName(request.getMerchantName());
        merchant.setMerchantEmail(request.getMerchantEmail());
        merchant.setMerchantPassword(request.getMerchantPassword());
        return merchantRepository.save(merchant);
    }

    public void deleteMerchant(UUID merchantUuid) {
        merchantRepository.deleteById(merchantUuid);
    }
}
