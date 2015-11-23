/**
 *
 */
package de.terrestris.shogun2.security.acl;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;

/**
 * Parent class for all tests that requie the ACL test context.
 *
 * @author Nils Bühner
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(value = "aclTransactionManager")
@Rollback(true)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-context-acl.xml" })
public class AclTestBase {

	/**
	 * The ACL service, which is used to check if the AclUtil made correct
	 * entries.
	 */
	@Autowired
	protected MutableAclService aclService;

	/**
	 * The user who will get permissions.
	 */
	protected User user;

	/**
	 * An {@link PersistentObject} that shall be secured.
	 */
	protected PersistentObject objectToSecure;

	/**
	 * This method will run before every test and reset the ACL (in memory)
	 * database.
	 *
	 * A user and an application the user will get permissions for will be
	 * created.
	 *
	 * All existing ACL entries will be deleted by this method, so that the ACL
	 * database is always empty when a new test begins.
	 *
	 * @throws SQLException
	 * @throws ScriptException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	@Before
	public void rebuildAclDatabaseAndAuthAsAdmin() throws NoSuchFieldException,
			IllegalAccessException {

		// auth as admin
		user = new User("First Name", "Last Name", "accountName");
		setIdOnPersistentObject(user, 17);
		authAsAdmin(user);

		objectToSecure = new Application("An app", "with description");
		setIdOnPersistentObject(objectToSecure, 42);

		// remove all existing ACL entries of the object if there are any
		ObjectIdentity oid = new ObjectIdentityImpl(objectToSecure);
		aclService.deleteAcl(oid, true);
	}

	/**
	 * This authenticates as admin, which is required to make use of the
	 * AclUtil.
	 *
	 */
	private void authAsAdmin(User user) {
		Collection<? extends GrantedAuthority> authorities = Collections
				.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
		Authentication adminAuth = new UsernamePasswordAuthenticationToken(
				user, "somePw", authorities);
		SecurityContextHolder.getContext().setAuthentication(adminAuth);
	}

	/**
	 * Helper method that uses reflection to set the (inaccessible) id field of
	 * the given {@link PersistentObject}.
	 *
	 * @param persistentObject
	 *            The object with the inaccessible id field
	 * @param id
	 *            The id to set
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private static final void setIdOnPersistentObject(
			PersistentObject persistentObject, Integer id)
			throws NoSuchFieldException, IllegalAccessException {
		// use reflection to get the inaccessible final field 'id'
		Field idField = PersistentObject.class.getDeclaredField("id");

		// make the field accessible and set the value
		idField.setAccessible(true);
		idField.set(persistentObject, id);
		idField.setAccessible(false);
	}
}
