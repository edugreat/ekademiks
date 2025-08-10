package com.edugreat.akademiksresource.classroom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomDao extends JpaRepository<Classroom, Integer> {
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Classroom c WHERE c.primaryInstructor.id = :instr")
    boolean has(@Param("instr") Integer instr);

    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    @Query("SELECT c FROM Classroom c JOIN c.institution i WHERE i.createdBy = :id")
    Page<Classroom> getAdminManagedClassrooms(@Param("id") Integer id, Pageable pageable);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    Page<Classroom> findByPrimaryInstructorId(@Param("primaryInstructorId") Integer primaryInstructorId, Pageable pageable);
    
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    Page<Classroom> findByInstitutionCreatedByAndInstitutionId(
            @Param("adminId") Integer adminId, 
            @Param("institutionId") Integer institutionId, 
            Pageable pageable);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    Page<Classroom> findByPrimaryInstructorIdAndInstitutionId(
            @Param("primaryInstructorId") Integer primaryInstructorId, 
            @Param("institutionId") Integer institutionId, 
            Pageable pageable);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    Page<Classroom> findByLevelIdAndInstitutionCreatedBy(
    		@Param("categoryId") Integer categoryId,
            @Param("adminId") Integer adminId, 
            Pageable pageable);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    Page<Classroom> findByPrimaryInstructorIdAndLevelId(
            @Param("instructorId") Integer instructorId, 
            @Param("categoryId") Integer categoryId, 
            Pageable pageable);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    Page<Classroom> findByInstitutionCreatedByAndInstitutionIdAndLevelId(
    		                   @Param("adminId")Integer adminId,
    		                   @Param("categoryId")Integer categoryId,
    		                   @Param("institutionId")Integer institutionId,
    		                   Pageable pageable);
    
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    Page<Classroom> findByPrimaryInstructorIdAndLevelIdAndInstitutionId(
    		                   @Param("primaryInstructorId")Integer primaryInstructorId,
    		                   @Param("categoryId")Integer categoryId,
    		                   @Param("institutionId")Integer institutionId,
    		                   Pageable pageable);
    
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    @Query("SELECT DISTINCT c FROM Classroom c JOIN c.classroomSubjects cs WHERE cs.instructor.id = :instructorId AND c.level.id = :categoryId")
    Page<Classroom> findInstructorInSubjectManagedClassroomsBy(
            @Param("instructorId") Integer instructorId,
            @Param("categoryId") Integer categoryId,
            Pageable pageable);
    


    
    @RestResource(exported = false)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Classroom c WHERE c.institution.id = :institutionId AND c.primaryInstructor.id = :userId")
    boolean isPrimaryInstructor(
            @Param("userId") Integer userId, 
            @Param("institutionId") Integer institutionId);
    
    @RestResource(exported = false)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Classroom c WHERE c.primaryInstructor.id = :userId")
    boolean isPrimaryInstructor(@Param("userId") Integer userId);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    @Query("SELECT DISTINCT c FROM Classroom c JOIN c.classroomSubjects cs WHERE cs.instructor.id = :instructorId")
    Page<Classroom> findByInstructorInSubject(@Param("instructorId") Integer instructorId, Pageable pageable);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    @Query("SELECT c FROM Classroom c JOIN c.classroomSubjects cs WHERE cs.instructor.id = :userId AND c.institution.id = :institutionId")
    Page<Classroom> findByInstructorInSubjectBy(
            @Param("userId") Integer userId, 
            @Param("institutionId") Integer institutionId,
            Pageable pageable);
    
    @RestResource(exported = false)
    @EntityGraph(attributePaths = {"level", "primaryInstructor"})
    @Query("SELECT DISTINCT c FROM Classroom c JOIN c.classroomSubjects cs WHERE c.level.id = :categoryId AND c.institution.id = :institutionId AND cs.instructor.id = :userId")
    List<Classroom> findByInstructorInSubject(
            @Param("userId") Integer userId, 
            @Param("institutionId") Integer institutionId, 
            @Param("categoryId") Integer categoryId);
    
//    Method that verifies that a particular classroom belongs to the institution referenced by the ID
    @RestResource(exported= false)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Classroom c WHERE c.id =:classroomId AND"
    		+ " c.institution.id =:institutionId")
   boolean isFoundInTheInstitution(@Param("classroomId")Integer classroomId,
		                           @Param("institutionId")Integer institutionId);
    @RestResource(exported = false)
    Optional<Classroom> findByIdAndInstitutionId(@Param("classroomId")Integer classroomId, @Param("institutionId")Integer institutionId);
   
    
    @RestResource(exported = false)
    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN TRUE ELSE FALSE END FROM Classroom c JOIN c.classroomSubjects cs WHERE c.id =:classroomId AND c.institution.id =:institutionId AND cs.instructor.id =:instructorId")
    boolean isSubjectInstructor( @Param("instructorId") Integer instructorId, @Param("institutionId") Integer institutionId, @Param("classroomId") Integer classroomId);

    @RestResource(exported = false)
    Optional<Classroom> findTop1ByInstitutionCreatedByAndInstitutionIdAndNameContaining(@Param("userId") Integer userId, @Param("institutionId")Integer institutionId, @Param("searchQuery")String searchQuery);
    
    
    @RestResource(exported = false)
    Optional<Classroom> findTop1ByPrimaryInstructorIdAndInstitutionIdAndNameContaining(@Param("userId") Integer userId, @Param("institutionId")Integer institutionId, @Param("searchQuery")String searchQuery);
    

    @RestResource(exported = false)
    Optional<Classroom> findTop1ByClassroomSubjectsInstructorIdAndInstitutionIdAndNameContaining(@Param("userId") Integer userId, @Param("institutionId")Integer institutionId, @Param("searchQuery")String searchQuery);
    
}