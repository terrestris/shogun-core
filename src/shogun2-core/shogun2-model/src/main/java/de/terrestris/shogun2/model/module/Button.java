/**
 *
 */
package de.terrestris.shogun2.model.module;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The Image Module is the Ext JS representation of an HTML img element.
 *
 * @author Kai Volland
 *
 */
@Entity
@Table
public class Button extends Module {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Button() {
	}
	
	/**
	 * A text to display on the button.
	 */
	private String text;
	
	/**
	 * The text showing up when hovering the button.
	 */
	private String tooltip;
	
	/**
	 * A glyph String to display an iconFont sign. e.g. 'xf059@FontAwesome'
	 */
	private String glyph;
	
	/**
	 * The module which is connected to the button, if action OPENMODULEWINDOW
	 * set. 
	 */
	private Module connectedModule;
	
	/**
	 * If action TOGGLEINTERACTION is set: This string represents the ol3
	 * interaction class. e.g. 'ol.interaction.DragZoom'
	 */
	private String interaction;

	/**
	 * A enum type for possible buttonActions. They represent
	 * the available buttons from BasiGX (https://github.com/terrestris/BasiGX).
	 * 
	 * Additional there are two ActionTypes to be more flexible:
	 * 
	 * OPENMODULEWINDOW (this.connectedModule required)
	 *    Opens any module in an Ext.window.Window.
	 * TOGGLEINTERACTION (this.interaction required)
	 *    Toggles the functionality of an OpenLayers 3 interaction.
	 */
	public static enum ButtonActionType {
		OPENMODULEWINDOW("openmodulewindow"),
		TOGGLEINTERACTION("toggleinteraction"),
		ADDWMS("addwms"),
		COORDINATETRANSFORM("coordinatetransform"),
		HELP("help"),
		HSI("hsi"),
		MEASURELINE("measureline"),
		MEASUREARE("measurearea"),
		PERMALINK("permalink"),
		TOGGLELEGEND("togglelegend"),
		ZOOMIN("zoomin"),
		ZOOMOUT("zoomut"),
		ZOOMTOEXTENT("zoomtoextent");

		private final String value;

		/**
		 * Enum constructor
		 *
		 * @param value
		 */
		private ButtonActionType(String value) {
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
		public static ButtonActionType fromString(String inputValue) {
			if (inputValue != null) {
				for (ButtonActionType type : ButtonActionType.values()) {
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
	 * The action gives informations about. A common action
	 * would be to open the connectedModule in an Ext JS Window.
	 */
	@Enumerated(EnumType.STRING)
	private ButtonActionType buttonAction;
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * @return the glyph
	 */
	public String getGlyph() {
		return glyph;
	}

	/**
	 * @param glyph the glyph to set
	 */
	public void setGlyph(String glyph) {
		this.glyph = glyph;
	}

	/**
	 * @return the connectedModule
	 */
	public Module getConnectedModule() {
		return connectedModule;
	}

	/**
	 * @param connectedModule the connectedModule to set
	 */
	public void setConnectedModule(Module connectedModule) {
		this.connectedModule = connectedModule;
	}

	/**
	 * @return the interaction
	 */
	public String getInteraction() {
		return interaction;
	}

	/**
	 * @param interaction the interaction to set
	 */
	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}

	/**
	 * @return the buttonAction
	 */
	public ButtonActionType getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(ButtonActionType buttonAction) {
		this.buttonAction = buttonAction;
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
		return new HashCodeBuilder(37, 3).
				appendSuper(super.hashCode()).
				append(getText()).
				append(getTooltip()).
				append(getGlyph()).
				append(getConnectedModule()).
				append(getInteraction()).
				append(getButtonAction()).
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
		if (!(obj instanceof Button))
			return false;
		Button other = (Button) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getText(), other.getText()).
				append(getTooltip(), other.getTooltip()).
				append(getGlyph(), other.getGlyph()).
				append(getConnectedModule(), other.getConnectedModule()).
				append(getInteraction(), other.getInteraction()).
				append(getButtonAction(), other.getButtonAction()).
				isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).
				appendSuper(super.toString()).
				append(getText()).
				append(getTooltip()).
				append(getGlyph()).
				append(getConnectedModule()).
				append(getInteraction()).
				append(getButtonAction()).
				toString();
	}

}
