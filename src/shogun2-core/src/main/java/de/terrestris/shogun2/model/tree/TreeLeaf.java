package de.terrestris.shogun2.model.tree;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


/**
*
* Representation of a layer which consists a corresponding data source
* and an appearance
*
* @author Kai Volland
* @author terrestris GmbH & Co. KG
*
*/
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TreeLeaf extends TreeNode {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}
