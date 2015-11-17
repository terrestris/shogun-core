/**
 *
 */
package de.terrestris.shogun2.model.module;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A search module working with the OSM Overpass API.
 *
 * @author Kai Volland
 *
 */
@Entity
@Table
public class OverpassSearch extends Module {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 * A enum type for the allowed response format.
	 */
	public enum OverpassFormatType {
		XML("xml"),
		JSON("json"),
		CSV("csv"),
		CUSTOM("custom"),
		POPUP("popup");

		private final String value;

		/**
		 * Enum constructor
		 *
		 * @param value
		 */
		private OverpassFormatType(String value) {
			this.value = value;
		}

		/**
		 * Static method to get an enum based on a string value.
		 * This method is annotated with {@link JsonCreator},
		 * which allows the client to send case insensitive string
		 * values (like "jSon"), which will be converted to the
		 * correct enum value.
		 *
		 * @param inputValue
		 * @return
		 */
		@JsonCreator
		public static OverpassFormatType fromString(String inputValue) {
			if (inputValue != null) {
				for (OverpassFormatType type : OverpassFormatType.values()) {
					if (inputValue.equalsIgnoreCase(type.value)) {
						return type;
					}
				}
			}
			return null;
		}

		/**
		 * This method is annotated with {@link JsonValue},
		 * so that jackson will serialize the enum value to
		 * the (lowercase) {@link #value}.
		 */
		@Override
		@JsonValue
		public String toString() {
			return value;
		}
	}

	/**
	 * The response format.
	 */
	@Enumerated(EnumType.STRING)
	private OverpassFormatType format;

	/**
	 * Limits the response.
	 */
	private Integer resultLimit;

	/**
	 * A list of EPSG-Codes the should be available in the module.
	 */
	@ElementCollection
	@CollectionTable(name = "OVERPASS_VIEWBOXLBRT", joinColumns = @JoinColumn(name = "OVERPASS_ID") )
	@Column(name = "VIEWBOXINTEGER")
	@OrderColumn(name = "IDX")
	private List<Integer> viewboxlbrt = new ArrayList<Integer>();

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public OverpassSearch() {
	}

	/**
	 * @param groupHeaderTpl the groupHeaderTpl to set
	 */
	public void setGroupHeaderTpl(String groupHeaderTpl) {
		this.groupHeaderTpl = groupHeaderTpl;
	}

	/**
	 * Characters needed to send a request.
	 */
	private Integer minSearchTextChars;

	/**
	 * The delay between hitting a key and sending the request in ms.
	 */
	private Integer typeDelay;

	/**
	 * The template of the grouping Header.
	 * See: http://docs.sencha.com/extjs/6.0/6.0.0-classic/#!/api/Ext.grid.feature.Grouping-cfg-groupHeaderTpl
	 */
	private String groupHeaderTpl;

	/**
	 * @return the format
	 */
	public OverpassFormatType getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(OverpassFormatType format) {
		this.format = format;
	}

	/**
	 * @return the limit
	 */
	public Integer getResultLimit() {
		return resultLimit;
	}

	/**
	 * @param resultLimit the limit to set
	 */
	public void setResultLimit(Integer resultLimit) {
		this.resultLimit = resultLimit;
	}

	/**
	 * @return the viewboxlbrt
	 */
	public List<Integer> getViewboxlbrt() {
		return viewboxlbrt;
	}

	/**
	 * @param viewboxlbrt the viewboxlbrt to set
	 */
	public void setViewboxlbrt(List<Integer> viewboxlbrt) {
		this.viewboxlbrt = viewboxlbrt;
	}

	/**
	 * @return the minSearchTextChars
	 */
	public Integer getMinSearchTextChars() {
		return minSearchTextChars;
	}

	/**
	 * @param minSearchTextChars the minSearchTextChars to set
	 */
	public void setMinSearchTextChars(Integer minSearchTextChars) {
		this.minSearchTextChars = minSearchTextChars;
	}

	/**
	 * @return the typeDelay
	 */
	public Integer getTypeDelay() {
		return typeDelay;
	}

	/**
	 * @param typeDelay the typeDelay to set
	 */
	public void setTypeDelay(Integer typeDelay) {
		this.typeDelay = typeDelay;
	}

	/**
	 * @return the groupHeaderTpl
	 */
	public String getGroupHeaderTpl() {
		return groupHeaderTpl;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(31, 3).
				appendSuper(super.hashCode()).
				append(getFormat()).
				append(getResultLimit()).
				append(getViewboxlbrt()).
				append(getMinSearchTextChars()).
				append(getTypeDelay()).
				append(getGroupHeaderTpl()).
				toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof OverpassSearch))
			return false;
		OverpassSearch other = (OverpassSearch) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getFormat(), other.getFormat()).
				append(getResultLimit(), other.getResultLimit()).
				append(getViewboxlbrt(), other.getViewboxlbrt()).
				append(getMinSearchTextChars(), other.getMinSearchTextChars()).
				append(getTypeDelay(), other.getTypeDelay()).
				append(getGroupHeaderTpl(), other.getGroupHeaderTpl()).
				isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
