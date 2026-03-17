package com.Yargin.reservation;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationById(
            Long id
    ) {
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Нет бронирования у клиента = " + id));
        return toDomainReservation(reservationEntity);
    }

    public List<Reservation> findAllReservation() {
        List<ReservationEntity> allEntities = repository.findAll();
        return allEntities.stream().map(this::toDomainReservation).toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if (reservationToCreate.id() != null){
            throw new IllegalArgumentException("Нельзя задавать id, задается системой");
        }
        if (reservationToCreate.status() != null){
            throw new IllegalArgumentException("Нельзя задавать статус, задается системой");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );
        var savedEntity = repository.save(entityToSave);
        return toDomainReservation(savedEntity);
    }

    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("По этому id брони не существует"));
        if (reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Не возможно изменить, статус =" + reservationEntity.getStatus());
        }
        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );
        var updatedReservation = repository.save(reservationToSave);
        return toDomainReservation(updatedReservation);
    }

    public void deleteReservation(Long id) {
        if (!repository.existsById(id)){
            throw new NoSuchElementException("По этому id брони не существует");
        }
        repository.deleteById(id);
    }

    public Reservation approveReservation(Long id) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("По этому id брони не существует"));
        if(reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Нельзя подтвердить бронь, статус=" + reservationEntity.getStatus());
        }
        var isConflict = reservationConflict(reservationEntity);
        if(isConflict){
            throw new IllegalStateException("Нельзя подтвердить бронь, есть пересечение сдругими датами");
        }
        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);
        return toDomainReservation(reservationEntity);

    }

    private Boolean reservationConflict(
            ReservationEntity reservation
    ) {
        var allReservation = repository.findAll();
        for (ReservationEntity existingReservation: allReservation){
            if(reservation.getId().equals(existingReservation.getId())) {
                continue;
            }
            if(!reservation.getRoomId().equals(existingReservation.getRoomId())){
                continue;
            }
            if(!reservation.getStatus().equals(ReservationStatus.APPROVED)){
                continue;
            }
            if(reservation.getStartDate().isBefore(existingReservation.getEndDate())
                && existingReservation.getStartDate().isBefore(reservation.getEndDate())){
                return true;
            }
        }
        return false;
    }
    private Reservation toDomainReservation(
            ReservationEntity reservation
    ) {
        return new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );

    }
}
