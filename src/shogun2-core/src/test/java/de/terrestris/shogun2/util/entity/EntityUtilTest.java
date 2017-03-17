package de.terrestris.shogun2.util.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.terrestris.shogun2.model.PersistentObject;


/**
 * @author Marc Jansen
 */
@SuppressWarnings({ "unused", "serial" })
public class EntityUtilTest {

	//
	// Start setup code
	//
	private class TestClassParent extends PersistentObject {
	}

	private class TestClassChild extends TestClassParent {
	}

	private class TestClassGrandChild extends TestClassChild {
	}

	private class EntityWithCollections extends PersistentObject {

		private Set<TestClassParent> privateSetOfTestClassParents;
		private List<TestClassParent> privateListOfTestClassParents;
		public Set<TestClassParent> publicSetOfTestClassParents;
		public List<TestClassParent> publicListOfTestClassParents;

		private Set<TestClassChild> privateSetOfTestClassChildren;
		private List<TestClassChild> privateListOfTestClassChildren;
		public Set<TestClassChild> publicSetOfTestClassChildren;
		public List<TestClassChild> publicListOfTestClassChildren;

		private Set<TestClassGrandChild> privateSetOfTestClassGrandChildren;
		private List<TestClassGrandChild> privateListOfTestClassGrandChildren;
		public Set<TestClassGrandChild> publicSetOfTestClassGrandChildren;
		public List<TestClassGrandChild> publicListOfTestClassGrandChildren;

		private String privateNeitherListNorSet;
		public String publicNeitherListNorSet;
	}

	private class SimpleEntity extends PersistentObject {

		private String privateField;
		protected String protectedField;
		public String publicField;
	}

	private class SimpleChildEntity extends SimpleEntity {

	}

	//
	// End setup code
	//


	// +------------------------------------------+
	// | Tests for the method `isCollectionField` |
	// +------------------------------------------+

	// ----- test the collections that accept parents or any child class of it

	@Test
	public void test_isCollectionField_returnsTrueWhenSetFieldParent_passedParent() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassParents", TestClassParent.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenListFieldParent_passedParent() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassParents", TestClassParent.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenSetFieldParent_passedChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassParents", TestClassChild.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenListFieldParent_passedChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassParents", TestClassChild.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenSetFieldParent_passedGrandChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassParents", TestClassGrandChild.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenListFieldParent_passedGrandChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassParents", TestClassGrandChild.class, true
		);
		assertTrue(isCollectionField);
	}


	// ----- test the collections that accept children or any child class of it

	@Test
	public void test_isCollectionField_returnsFalseWhenSetFieldChild_passedParent() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassChildren", TestClassParent.class, true
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsFalseWhenListFieldChild_passedParent() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassChildren", TestClassParent.class, true
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenSetFieldChild_passedChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassChildren", TestClassChild.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenListFieldChild_passedChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassChildren", TestClassChild.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenSetFieldChild_passedGrandChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassChildren", TestClassGrandChild.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenListFieldChild_passedGrandChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassChildren", TestClassGrandChild.class, true
		);
		assertTrue(isCollectionField);
	}


	// ----- test the collections that accept only grandchilds

	@Test
	public void test_isCollectionField_returnsFalseWhenSetFieldGrandChild_passedParent() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassGrandChildren", TestClassParent.class, true
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsFalseWhenListFieldGrandChild_passedParent() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassGrandChildren", TestClassParent.class, true
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenSetFieldGrandChild_passedChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassGrandChildren", TestClassChild.class, true
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenListFieldGrandChild_passedChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassGrandChildren", TestClassChild.class, true
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenSetFieldGrandChild_passedGrandChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateSetOfTestClassChildren", TestClassGrandChild.class, true
		);
		assertTrue(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenListFieldGrandChild_passedGrandChild() {
		boolean isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "publicListOfTestClassChildren", TestClassGrandChild.class, true
		);
		assertTrue(isCollectionField);
	}


	// ---- test private / public visibility of fields

	@Test
	public void test_isCollectionField_returnsFalseWhenForceAccessIsFalse_forPrivateFields() {
		boolean isCollectionField;

		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassParents", TestClassGrandChild.class, false
		);
		assertFalse(isCollectionField);

		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassChildren", TestClassGrandChild.class, false
		);
		assertFalse(isCollectionField);

		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateListOfTestClassGrandChildren", TestClassGrandChild.class, false
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsTrueWhenForceAccessIsFalse_forPublicFields() {
		boolean isCollectionField;

		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "publicListOfTestClassParents", TestClassGrandChild.class, false
		);
		assertTrue(isCollectionField);

		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "publicListOfTestClassChildren", TestClassGrandChild.class, false
		);
		assertTrue(isCollectionField);

		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "publicListOfTestClassGrandChildren", TestClassGrandChild.class, false
		);
		assertTrue(isCollectionField);
	}


	// ---- test non-collection field

	@Test
	public void test_isCollectionField_returnsFalse_forNonCollectionFields_passedParent() {
		boolean isCollectionField;

		// concrete entity class has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateNeitherListNorSet", TestClassParent.class, true
		);
		assertFalse(isCollectionField);

		// forceAccess has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "publicNeitherListNorSet", TestClassParent.class, false
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsFalse_forNonCollectionFields_passedChild() {
		boolean isCollectionField;

		// concrete entity class has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateNeitherListNorSet", TestClassChild.class, true
		);
		assertFalse(isCollectionField);

		// forceAccess has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "publicNeitherListNorSet", TestClassChild.class, false
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsFalse_forNonCollectionFields_passedGrandChild() {
		boolean isCollectionField;

		// concrete entity class has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "privateNeitherListNorSet", TestClassGrandChild.class, true
		);
		assertFalse(isCollectionField);

		// forceAccess has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "publicNeitherListNorSet", TestClassGrandChild.class, false
		);
		assertFalse(isCollectionField);
	}


	// ---- test not existing fields

	@Test
	public void test_isCollectionField_returnsFalse_forNotExistingFields_passedParent() {
		boolean isCollectionField;

		// concrete entity class has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "thisFieldIsNotThere", TestClassParent.class, true
		);
		assertFalse(isCollectionField);

		// forceAccess has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "thisFieldIsNotThere", TestClassParent.class, false
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsFalse_forNotExistingFields_passedChild() {
		boolean isCollectionField;

		// concrete entity class has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "thisFieldIsNotThere", TestClassChild.class, true
		);
		assertFalse(isCollectionField);

		// forceAccess has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "thisFieldIsNotThere", TestClassChild.class, false
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isCollectionField_returnsFalse_forNotExistingFields_passedGrandChild() {
		boolean isCollectionField;

		// concrete entity class has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "thisFieldIsNotThere", TestClassGrandChild.class, true
		);
		assertFalse(isCollectionField);

		// forceAccess has no effect:
		isCollectionField = EntityUtil.isCollectionField(
			EntityWithCollections.class, "thisFieldIsNotThere", TestClassGrandChild.class, false
		);
		assertFalse(isCollectionField);
	}

	@Test
	public void test_isField_forPrivateField() {
		boolean isField;

		isField = EntityUtil.isField(SimpleEntity.class, "privateField", String.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleEntity.class, "privateField", String.class, true);
		assertTrue(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "privateField", String.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "privateField", String.class, true);
		assertTrue(isField);

		// field is not integer
		isField = EntityUtil.isField(SimpleEntity.class, "privateField", Integer.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleEntity.class, "privateField", Integer.class, true);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "privateField", Integer.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "privateField", Integer.class, true);
		assertFalse(isField);

		// "unknown" fieldEntityType
		isField = EntityUtil.isField(SimpleEntity.class, "privateField", null, true);
		assertTrue(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "privateField", null, true);
		assertTrue(isField);
	}

	@Test
	public void test_isField_forProtectedField() {
		boolean isField;

		isField = EntityUtil.isField(SimpleEntity.class, "protectedField", String.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleEntity.class, "protectedField", String.class, true);
		assertTrue(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "protectedField", String.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "protectedField", String.class, true);
		assertTrue(isField);

		// field is not integer
		isField = EntityUtil.isField(SimpleEntity.class, "protectedField", Integer.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleEntity.class, "protectedField", Integer.class, true);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "protectedField", Integer.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "protectedField", Integer.class, true);
		assertFalse(isField);

		// "unknown" fieldEntityType
		isField = EntityUtil.isField(SimpleEntity.class, "protectedField", null, true);
		assertTrue(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "protectedField", null, true);
		assertTrue(isField);
	}

	@Test
	public void test_isField_forPublicField() {
		boolean isField;

		isField = EntityUtil.isField(SimpleEntity.class, "publicField", String.class, false);
		assertTrue(isField);

		isField = EntityUtil.isField(SimpleEntity.class, "publicField", String.class, true);
		assertTrue(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "publicField", String.class, false);
		assertTrue(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "publicField", String.class, true);
		assertTrue(isField);

		// field is not integer
		isField = EntityUtil.isField(SimpleEntity.class, "publicField", Integer.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleEntity.class, "publicField", Integer.class, true);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "publicField", Integer.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "publicField", Integer.class, true);
		assertFalse(isField);

		// "unknown" fieldEntityType
		isField = EntityUtil.isField(SimpleEntity.class, "publicField", null, true);
		assertTrue(isField);

		isField = EntityUtil.isField(SimpleChildEntity.class, "publicField", null, false);
		assertTrue(isField);
	}

	@Test
	public void test_isField_forNonExistingField() {
		boolean isField;

		isField = EntityUtil.isField(SimpleEntity.class, "non_existing_field", String.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleEntity.class, "non_existing_field", String.class, true);
		assertFalse(isField);

		// field is not integer
		isField = EntityUtil.isField(SimpleEntity.class, "non_existing_field", Integer.class, false);
		assertFalse(isField);

		isField = EntityUtil.isField(SimpleEntity.class, "non_existing_field", Integer.class, true);
		assertFalse(isField);

		// "unknown" fieldEntityType
		isField = EntityUtil.isField(SimpleEntity.class, "non_existing_field", null, true);
		assertFalse(isField);
	}
}
