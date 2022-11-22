package com.lab4.demo.device;

import com.lab4.demo.device.model.Device;
import com.lab4.demo.device.model.dto.DeviceDTO;
import com.lab4.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final UserService userService;
    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    private Device findById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + id));
    }

    public List<DeviceDTO> findAll() {
        return deviceRepository.findAll().stream()
                .map(deviceMapper::toDto)
                .collect(Collectors.toList());
    }

    public DeviceDTO create(DeviceDTO item) {
        return deviceMapper.toDto(deviceRepository.save(
                deviceMapper.fromDto(item)
        ));
    }
    public void delete(Long id){
        Device device = findById(id);
        deviceRepository.delete(device);
    }
    public DeviceDTO edit(Long id, DeviceDTO item) {
        Device actDevice = findById(id);
        if(userService.existsById(item.getUserId())) {
            actDevice.setDescription(item.getDescription());
            actDevice.setAddress(item.getAddress());
            actDevice.setConsumption(item.getConsumption());
            actDevice.setUserId(item.getUserId());
        }
        return deviceMapper.toDto(
                deviceRepository.save(actDevice)
        );
    }
    public DeviceDTO linkDeviceToUser(Long userId, Long deviceId)
    {
        Device actDevice = findById(deviceId);
        if(userService.existsById(userId))
        {actDevice.setUserId(userId);}
        return deviceMapper.toDto(
                deviceRepository.save(actDevice));
    }
    public List <DeviceDTO> getDevicesForUser(Long userId)
    {
        return deviceRepository.findAllByUserIdEquals(userId).stream()
                .map(deviceMapper::toDto)
                .collect(Collectors.toList());
    }
}