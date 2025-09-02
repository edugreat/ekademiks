
package com.edugreat.akademiksresource.classroom;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;

@Data
public class ClassroomSubjectId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
	private Integer classroomId;
	private Integer subjectId;

	public ClassroomSubjectId() {
	}

	public ClassroomSubjectId(Integer classroomId, Integer subjectId) {
		this.classroomId = classroomId;
		this.subjectId = subjectId;
	}

	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof ClassroomSubjectId)) return false;
	        ClassroomSubjectId that = (ClassroomSubjectId) o;
	        return Objects.equals(classroomId, that.classroomId) &&
	               Objects.equals(subjectId, that.subjectId);
	    }
	    
	    @Override
	    public int hashCode() {
	        return Objects.hash(classroomId, subjectId);
	    }
}