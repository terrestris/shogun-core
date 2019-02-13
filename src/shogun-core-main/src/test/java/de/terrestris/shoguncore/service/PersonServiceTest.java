package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.PersonDao;
import de.terrestris.shoguncore.model.Person;

public class PersonServiceTest extends
    PermissionAwareCrudServiceTest<Person, PersonDao<Person>, PersonService<Person, PersonDao<Person>>> {

    /**
     * @throws Exception
     */
    public void setUpImplToTest() throws Exception {
        implToTest = new Person();
    }

    @Override
    protected PersonService<Person, PersonDao<Person>> getCrudService() {
        return new PersonService<Person, PersonDao<Person>>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<PersonDao<Person>> getDaoClass() {
        return (Class<PersonDao<Person>>) new PersonDao<Person>().getClass();
    }

}
