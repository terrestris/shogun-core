package de.terrestris.shogun2.security.acl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.test.context.ContextConfiguration;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.security.aspect.AclManagement;
import de.terrestris.shogun2.service.AbstractCrudService;

/**
 * Abstract test class to test that the aspect annotated class {@link AclManagement}
 * intercepts the AbstractCrudService as expected.
 *
 * @author Nils Bühner
 *
 */
// The context defined here will extend (!) the context of the parent class!
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-context-acl-management.xml" })
public abstract class AbstractAclManagementTest<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>>
		extends AbstractAclTestBase {

	/**
	 * Object that holds a concrete implementation of
	 * {@link PersistentObject} for the tests.
	 */
	protected E implToUse = null;

	private AbstractCrudService<E, D> serviceToUse;

	@Autowired
	private MutableAclService aclService;

	/**
	 *
	 */
	@Before
	public void setUpService() {
		this.serviceToUse = getCrudService();
	}

	/**
	 *
	 */
	@Before
	public void setUpImplToUse() {
		this.implToUse = getImplToUse();
	};

	/**
	 * Set the {@link #implToUse} to null after each test.
	 *
	 * @throws Exception
	 */
	@After
	public void tearDownAfterEachTest() throws Exception {
		implToUse = null;
	}

	/**
	 * This method has to be implemented by subclasses to return a concrete
	 * instance of {@link PersistentObject}.
	 */
	protected abstract E getImplToUse();

	/**
	 * This method has to be implemented by subclasses to return a concrete
	 * implementation of the tested service.
	 *
	 * @return
	 */
	protected abstract AbstractCrudService<E, D> getCrudService();


	/**
	 *
	 */
	@Test
	public void handleAclOnSaveOrUpdate_interceptsAsExpected() {

		// when this method is called, the AclManagement class should
		// automatically insert ACL entries for the currently
		// logged in user (which is defined in the parent class)
		implToUse = serviceToUse.saveOrUpdate(implToUse);

		// read the ACL for the created app
		ObjectIdentity oid = new ObjectIdentityImpl(implToUse);
		Acl acl = aclService.readAclById(oid);

		// assert that the ACL owner equals the user used in this test
		PrincipalSid aclOwner = (PrincipalSid) acl.getOwner();
		assertEquals(user.getAccountName(), aclOwner.getPrincipal());

		// define expected permissions
		Set<Permission> expectedPermissions = new HashSet<Permission>(
			Arrays.asList(
				BasePermission.READ,
				BasePermission.WRITE,
				BasePermission.DELETE
			)
		);

		// assert that there are the expected permissions
		List<AccessControlEntry> aclEntries = acl.getEntries();

		// extract the actual permissions from the aclEntries
		Set<Permission> actualPermissions = new HashSet<Permission>();
		for (AccessControlEntry aclEntry : aclEntries) {
			actualPermissions.add(aclEntry.getPermission());
		}

		assertEquals(expectedPermissions, actualPermissions);
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *
	 */
	@Test
	public void handleAclAfterDelete_interceptsAsExpected()
			throws NoSuchFieldException, IllegalAccessException {

		// save object and assert that ACL entries are available
		implToUse = serviceToUse.saveOrUpdate(implToUse);
		Acl acl = aclService.readAclById(new ObjectIdentityImpl(implToUse));

		assertFalse(acl.getEntries().isEmpty());

		// delete object and assert that ACL entries are empty
		serviceToUse.delete(implToUse);
		acl = aclService.readAclById(new ObjectIdentityImpl(implToUse));

		assertTrue(acl.getEntries().isEmpty());
	}
}
