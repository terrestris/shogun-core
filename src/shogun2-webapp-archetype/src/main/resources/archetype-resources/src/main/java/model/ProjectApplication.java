#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.terrestris.shogun2.model.Application;

/**
 * @author Nils Bühner
 * 
 */
@Entity
public class ProjectApplication extends Application {

	private static final long serialVersionUID = 1L;

	@Column
	private String projectSpecificString;

	@Column
	private Integer projectSpecificInteger;

	public ProjectApplication() {
	}

	public String getProjectSpecificString() {
		return projectSpecificString;
	}

	public void setProjectSpecificString(String projectSpecificString) {
		this.projectSpecificString = projectSpecificString;
	}

	public Integer getProjectSpecificInteger() {
		return projectSpecificInteger;
	}

	public void setProjectSpecificInteger(Integer projectSpecificInteger) {
		this.projectSpecificInteger = projectSpecificInteger;
	}

}
