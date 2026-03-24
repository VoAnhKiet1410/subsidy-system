package com.trocap.nguoidung.service;

import com.trocap.common.exception.BadRequestException;
import com.trocap.common.exception.ResourceNotFoundException;
import com.trocap.nguoidung.dto.VaiTroRequest;
import com.trocap.nguoidung.model.VaiTro;
import com.trocap.nguoidung.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaiTroService {

    private final VaiTroRepository vaiTroRepository;

    public List<VaiTro> findAll() {
        return vaiTroRepository.findAll();
    }

    public VaiTro findById(String id) {
        return vaiTroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò: " + id));
    }

    public VaiTro create(VaiTroRequest request) {
        if (vaiTroRepository.existsByMaVaiTro(request.getMaVaiTro())) {
            throw new BadRequestException("Mã vai trò đã tồn tại: " + request.getMaVaiTro());
        }
        VaiTro vaiTro = VaiTro.builder()
                .maVaiTro(request.getMaVaiTro())
                .tenVaiTro(request.getTenVaiTro())
                .moTa(request.getMoTa())
                .build();
        return vaiTroRepository.save(vaiTro);
    }

    public VaiTro update(String id, VaiTroRequest request) {
        VaiTro vaiTro = findById(id);
        // Nếu đổi mã → kiểm tra trùng
        if (!vaiTro.getMaVaiTro().equals(request.getMaVaiTro())
                && vaiTroRepository.existsByMaVaiTro(request.getMaVaiTro())) {
            throw new BadRequestException("Mã vai trò đã tồn tại: " + request.getMaVaiTro());
        }
        vaiTro.setMaVaiTro(request.getMaVaiTro());
        vaiTro.setTenVaiTro(request.getTenVaiTro());
        vaiTro.setMoTa(request.getMoTa());
        return vaiTroRepository.save(vaiTro);
    }

    public void delete(String id) {
        if (!vaiTroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò: " + id);
        }
        vaiTroRepository.deleteById(id);
    }
}
