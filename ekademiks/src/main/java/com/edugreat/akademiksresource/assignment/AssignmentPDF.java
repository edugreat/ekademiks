package com.edugreat.akademiksresource.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Table
@Entity
@Data
public class AssignmentPDF {
	
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
	 @Column
	  private String fileName;
	  
	 @Column
	  private String fileType;
	  
	  @Column(length = 500000)
	  private byte[] fileByte;
	  
	  public  AssignmentPDF() {}
	  
	
	public AssignmentPDF(String fileName, String fileType, byte[] fileByte) {
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileByte = fileByte;
	}



	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) return true;
		
		if(obj == null || getClass() != obj.getClass()) return false;
		
	
		
		
		return this.id.equals(((AssignmentPDF)obj).id);
	}

	@Override
	public int hashCode() {
		
		return id.hashCode();
	}
	
	

}
