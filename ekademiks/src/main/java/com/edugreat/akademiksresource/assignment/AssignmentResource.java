package com.edugreat.akademiksresource.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import lombok.Data;

// Base class of theory, objective and PDF based assignments

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Data
public abstract class AssignmentResource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String answer = "";

//	represents the question number
	@Column(nullable = false)
	private int _index = 0;

	@Column(nullable = false)
	private String problem = "";

	private String fileName = "";

	private String fileType = "";

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] fileByte = {};

	public AssignmentResource() {
	}

	public AssignmentResource(String answer, int _index, String problem) {
		this.answer = answer;
		this._index = _index;
		this.problem = problem;
	}

	public AssignmentResource(String fileName, String fileType, byte[] fileByte) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileByte = fileByte;
	}

	public Integer getId() {
		return id;
	}

	public abstract String getType();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Objectives that = (Objectives) o;
		return getId() != null && getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
