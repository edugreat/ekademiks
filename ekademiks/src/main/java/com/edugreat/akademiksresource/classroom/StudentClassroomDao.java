package com.edugreat.akademiksresource.classroom;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.classroom.StudentClassroom.EnrollmentStatus;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;

@Repository
public interface StudentClassroomDao extends JpaRepository<StudentClassroom, Integer> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sc FROM StudentClassroom sc WHERE sc.student.id = :studentId " +
           "AND sc.classroom.id = :classroomId " +
           "AND sc.academicYear = :academicYear " +
           "AND sc.enrollmentStatus = :status")
    Optional<StudentClassroom> findByStudentAndClassroomAndYearAndStatus(
        @Param("studentId") Integer studentId,
        @Param("classroomId") Integer classroomId,
        @Param("academicYear") Integer academicYear,
        @Param("status") EnrollmentStatus status);
}
