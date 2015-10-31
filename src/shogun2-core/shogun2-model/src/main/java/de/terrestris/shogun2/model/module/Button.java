/**
 *
 */
package de.terrestris.shogun2.model.module;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
	 * The buttonAction describes the action/handler of the button.
	 * 
	 * The String can be any of the ButtonClasses available in the
	 * BasiGX Package (https://github.com/terrestris/BasiGX).
	 * e.g. "ToggleLegend"
	 * Use "Measurearea" or "Measureline" to specify the type of the
	 * BasiGX.view.button.Measure.
	 * 
	 * Additional there are two ActionTypes to be more flexible:
	 * 
	 * OPENMODULEWINDOW (this.connectedModule required)
	 *    Opens any module in an Ext.window.Window.
	 * TOGGLEINTERACTION (this.interaction required)
	 *    Toggles the functionality of an OpenLayers 3 interaction.
	 */
	private String buttonAction;
	
	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Button() {
	}
	
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
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
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
