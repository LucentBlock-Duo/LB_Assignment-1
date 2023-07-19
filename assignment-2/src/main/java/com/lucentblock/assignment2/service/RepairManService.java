package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.RepairMan;
import com.lucentblock.assignment2.entity.Reserve;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.entity.item.ItemDetail;
import com.lucentblock.assignment2.exception.RepairManNotFoundException;
import com.lucentblock.assignment2.model.RepairManInfoDTO;
import com.lucentblock.assignment2.model.maintenanceItem.ItemDetailDTO;
import com.lucentblock.assignment2.repository.RepairManRepository;
import com.lucentblock.assignment2.repository.ReserveRepository;
import com.lucentblock.assignment2.repository.item.ItemDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RepairManService {
    private final RepairManRepository repairManRepository;
    private final ReserveRepository reserveRepository;
    private final ItemDetailRepository itemDetailRepository;

    public List<RepairManInfoDTO> getRecommendRepairMenByUser(User user) {
        List<Long> recommendRepairMenIdsByUser = repairManRepository.findRecommendRepairMenIdsByUser(user);

        ArrayList<RepairMan> repairMen = new ArrayList<>();

        for (Long id : recommendRepairMenIdsByUser) {
            repairMen.add(repairManRepository.findById(id).orElseThrow(() -> new RepairManNotFoundException(id.toString())));
        }

        return repairMen.stream()
                .map(repairMan -> RepairManInfoDTO.builder()
                        .id(repairMan.getId())
                        .name(repairMan.getName())
                        .licenseId(repairMan.getLicenseId())
                        .careerStartAt(repairMan.getCareerStartAt())
                        .availableItems(itemDetailRepository.findItemDetailByRepairManAndDeletedAtIsNull(repairMan).stream()
                                .map(
                                        itemDetail -> ItemDetailDTO.builder()
                                                .itemDetailId(itemDetail.getId())
                                                .maintenanceItemId(itemDetail.getMaintenanceItem().getId())
                                                .itemName(itemDetail.getMaintenanceItem().getItemName())
                                                .requiredLicense(itemDetail.getMaintenanceItem().getRequiredLicense())
                                                .requiredTime(itemDetail.getMaintenanceItem().getRequiredTime())
                                                .price(itemDetail.getPrice())
                                                .build()
                                ).toList()
                        ).build())
                .toList();
    }

    /*
        날짜가 정해진 경우, 해당 날짜에 예약 가능한 정비공 정보 및 모든 정비항목에 대한 가격 정보를 담아서 응답.
     */
    public List<RepairManInfoDTO> fetchRepairManAvailableAtDateAndTime(LocalDate date, LocalTime startTime) {
        List<RepairMan> repairMen = repairManRepository.findAll();
        return repairMen.stream()
                .filter(repairMan -> reserveRepository.
                    findReservesByRepairManAndDateAndStartTimeLessThanAndEndTimeGreaterThanAndDeletedAtIsNull(
                            repairMan, date, startTime.plusMinutes(30), startTime).size() == 0)
                .map(repairMan -> RepairManInfoDTO.builder()
                        .id(repairMan.getId())
                        .name(repairMan.getName())
                        .licenseId(repairMan.getLicenseId())
                        .careerStartAt(repairMan.getCareerStartAt())
                        .evaluatedNum(repairMan.getEvaluatedNum())
                        .evaluationGrade(repairMan.getEvaluationGrade())
                        .availableItems(itemDetailRepository.findItemDetailByRepairManAndDeletedAtIsNull(repairMan).stream()
                            .map(
                                    itemDetail -> ItemDetailDTO.builder()
                                    .itemDetailId(itemDetail.getId())
                                    .maintenanceItemId(itemDetail.getMaintenanceItem().getId())
                                    .itemName(itemDetail.getMaintenanceItem().getItemName())
                                    .requiredLicense(itemDetail.getMaintenanceItem().getRequiredLicense())
                                    .requiredTime(itemDetail.getMaintenanceItem().getRequiredTime())
                                    .price(itemDetail.getPrice())
                                    .build()
                            ).toList()
                ).build())
                .sorted(Comparator.comparingDouble(RepairManInfoDTO::getEvaluationGrade).reversed())
                .toList();
    }

    /*
        정비항목이 정해진 경우, 수리공 가격 정보에 해당 정비항목만 담아서 응답
     */
    public List<RepairManInfoDTO> fetchRepairManAvailableByItem(Long maintenanceItemId) {
        List<ItemDetail> items = itemDetailRepository.findItemDetailsByMaintenanceItem_IdAndDeletedAtIsNull(maintenanceItemId);
        return items.stream()
                .map(itemDetail -> RepairManInfoDTO.builder()
                        .id(itemDetail.getRepairMan().getId())
                        .name(itemDetail.getRepairMan().getName())
                        .licenseId(itemDetail.getRepairMan().getLicenseId())
                        .careerStartAt(itemDetail.getRepairMan().getCareerStartAt())
                        .evaluatedNum(itemDetail.getRepairMan().getEvaluatedNum())
                        .evaluationGrade(itemDetail.getRepairMan().getEvaluationGrade())
                        .availableItems(List.of(ItemDetailDTO.builder()
                                            .itemDetailId(itemDetail.getId())
                                            .maintenanceItemId(itemDetail.getMaintenanceItem().getId())
                                            .itemName(itemDetail.getMaintenanceItem().getItemName())
                                            .requiredLicense(itemDetail.getMaintenanceItem().getRequiredLicense())
                                            .requiredTime(itemDetail.getMaintenanceItem().getRequiredTime())
                                            .price(itemDetail.getPrice())
                                            .build()
                                )
                        ).build())
                .sorted(Comparator.comparingDouble(RepairManInfoDTO::getEvaluationGrade).reversed())
                .toList();
    }

    public List<RepairManInfoDTO> filterRepairMenByDateTime(LocalDate date, LocalTime startTime, List<Long> ids) {
        List<RepairMan> repairMen = repairManRepository.findAllById(ids);
        return repairMen.stream()
                .filter(repairMan -> reserveRepository
                        .findReservesByRepairManAndDateAndStartTimeLessThanAndEndTimeGreaterThanAndDeletedAtIsNull(repairMan, date, startTime.plusMinutes(30), startTime).size() == 0)
                .map(repairMan -> RepairManInfoDTO.builder()
                        .id(repairMan.getId())
                        .name(repairMan.getName())
                        .licenseId(repairMan.getLicenseId())
                        .careerStartAt(repairMan.getCareerStartAt())
                        .evaluatedNum(repairMan.getEvaluatedNum())
                        .evaluationGrade(repairMan.getEvaluationGrade())
                        .availableItems(itemDetailRepository.findItemDetailByRepairManAndDeletedAtIsNull(repairMan).stream()
                                .map(
                                        itemDetail -> ItemDetailDTO.builder()
                                                .itemDetailId(itemDetail.getId())
                                                .maintenanceItemId(itemDetail.getMaintenanceItem().getId())
                                                .itemName(itemDetail.getMaintenanceItem().getItemName())
                                                .requiredLicense(itemDetail.getMaintenanceItem().getRequiredLicense())
                                                .requiredTime(itemDetail.getMaintenanceItem().getRequiredTime())
                                                .price(itemDetail.getPrice())
                                                .build()
                                ).toList()
                        ).build())
                .sorted(Comparator.comparingDouble(RepairManInfoDTO::getEvaluationGrade).reversed())
                .toList();
    }

    public List<RepairManInfoDTO> filterRepairMenByItem(Long maintenanceItemId, List<Long> ids) {
        List<RepairMan> repairMen = repairManRepository.findAllById(ids);
        return repairMen.stream()
                .filter(repairMan -> itemDetailRepository.findByRepairManAndMaintenanceItem_Id(repairMan, maintenanceItemId).isPresent())
                .map(repairMan -> RepairManInfoDTO.builder()
                        .id(repairMan.getId())
                        .name(repairMan.getName())
                        .licenseId(repairMan.getLicenseId())
                        .careerStartAt(repairMan.getCareerStartAt())
                        .evaluatedNum(repairMan.getEvaluatedNum())
                        .evaluationGrade(repairMan.getEvaluationGrade())
                        .availableItems(itemDetailRepository.findItemDetailByRepairManAndDeletedAtIsNull(repairMan).stream()
                                .map(
                                        itemDetail -> ItemDetailDTO.builder()
                                                .itemDetailId(itemDetail.getId())
                                                .maintenanceItemId(itemDetail.getMaintenanceItem().getId())
                                                .itemName(itemDetail.getMaintenanceItem().getItemName())
                                                .requiredLicense(itemDetail.getMaintenanceItem().getRequiredLicense())
                                                .requiredTime(itemDetail.getMaintenanceItem().getRequiredTime())
                                                .price(itemDetail.getPrice())
                                                .build()
                                ).toList()
                        ).build())
                .sorted(Comparator.comparingDouble(RepairManInfoDTO::getEvaluationGrade).reversed())
                .toList();
    }

    public Boolean[] fetchRepairManScheduleByDate(Long repairManId, LocalDate date) {
        Boolean[] availableTimeSlot = new Boolean[18];
        Arrays.fill(availableTimeSlot, true);
        List<Reserve> reserves = reserveRepository.findReservesByRepairMan_IdAndDateAndDeletedAtIsNull(repairManId, date);

        for (Reserve reserve : reserves) {
            int timeSlotIndex = (reserve.getStartTime().getHour() - 9) * 2;
            long length = ChronoUnit.MINUTES.between(reserve.getStartTime(), reserve.getEndTime()) / 30;

            for (int i = timeSlotIndex; i < timeSlotIndex + length; i++) {
                availableTimeSlot[timeSlotIndex] = false;
            }
        }

        return availableTimeSlot;
    }
}
