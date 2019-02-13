package de.terrestris.shogun2.init;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.interceptor.InterceptorRule;
import de.terrestris.shogun2.service.InitializationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ContentInitializer}
 *
 * @author Andre Henn
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LogManager.class})
@PowerMockIgnore({"javax.management.*", "javax.xml.*", "org.apache.xerces.*", "org.w3c.dom.*", "org.xml.sax.*"})
public class ContentInitializerTest {

    @Mock
    private Logger loggerMock;

    @Mock(name = "initializationService")
    private InitializationService initializationService;

    private ContentInitializer contentInitializer;

    @Before
    public void setUp(){
        PowerMockito.mockStatic(LogManager.class);
        when(LogManager.getLogger(any(Class.class))).
            thenReturn(loggerMock);
        contentInitializer = new ContentInitializer();
    }

    @Test
    public void initializeDatabaseContent_doesNothingIfShogunInitDisabled () {
        contentInitializer.setShogunInitEnabled(false);
        contentInitializer.initializeDatabaseContent();

        verify(loggerMock).info("Not initializing anything for SHOGun.");
    }

    @Test
    public void initializeDatabaseContent_callsSaveUserIfPersistentObjectIsAnUser() {
        contentInitializer.setShogunInitEnabled(true);
        final List<PersistentObject> objToCreate = new LinkedList<>();
        objToCreate.add(new User("peter", "peter", "peter"));
        contentInitializer.setObjectsToCreate(objToCreate);
        contentInitializer.setInitService(this.initializationService);
        contentInitializer.initializeDatabaseContent();

        // should not be called since mapModuleId and layerIds are empty
        Mockito.verify(initializationService, Mockito.times(1)).
            saveUser(ArgumentMatchers.any(User.class)
            );
        Mockito.verify(initializationService,  Mockito.never()).
            savePersistentObject(ArgumentMatchers.any(PersistentObject.class)
            );
        Mockito.verifyNoMoreInteractions(initializationService);
    }

    @Test
    public void initializeDatabaseContent_callsSavePersistentObjectIfPersistentObjectIsNotAnUser() {
        contentInitializer.setShogunInitEnabled(true);
        final List<PersistentObject> objToCreate = new LinkedList<>();
        objToCreate.add(new InterceptorRule());
        contentInitializer.setObjectsToCreate(objToCreate);
        contentInitializer.setInitService(this.initializationService);
        contentInitializer.initializeDatabaseContent();

        // should not be called since mapModuleId and layerIds are empty
        Mockito.verify(initializationService, Mockito.never()).
            saveUser(ArgumentMatchers.any(User.class)
            );
        Mockito.verify(initializationService, Mockito.times(1)).
            savePersistentObject(ArgumentMatchers.any(InterceptorRule.class)
            );
        Mockito.verifyNoMoreInteractions(initializationService);
    }
}
