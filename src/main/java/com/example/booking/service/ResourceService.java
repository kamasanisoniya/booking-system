package com.example.booking.service;

import com.example.booking.dto.ResourceRequest;
import com.example.booking.dto.ResourceResponse;
import com.example.booking.entity.Resource;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<ResourceResponse> findAll() {
        return resourceRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ResourceResponse findById(Long id) {
        Resource r = resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return toDto(r);
    }

    @Transactional
    public ResourceResponse create(ResourceRequest req) {
        Resource r = Resource.builder()
                .name(req.getName())
                .description(req.getDescription())
                .type(req.getType())
                .available(req.getAvailable() == null ? true : req.getAvailable())
                .build();
        r = resourceRepository.save(r);
        return toDto(r);
    }

    @Transactional
    public ResourceResponse update(Long id, ResourceRequest req) {
        Resource r = resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        r.setName(req.getName());
        r.setDescription(req.getDescription());
        r.setType(req.getType());
        if (req.getAvailable() != null) r.setAvailable(req.getAvailable());
        r = resourceRepository.save(r);
        return toDto(r);
    }

    @Transactional
    public void delete(Long id) {
        if (!resourceRepository.existsById(id)) throw new ResourceNotFoundException("Resource not found");
        resourceRepository.deleteById(id);
    }

    private ResourceResponse toDto(Resource r) {
        return ResourceResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .type(r.getType())
                .available(r.isAvailable())
                .build();
    }
}
