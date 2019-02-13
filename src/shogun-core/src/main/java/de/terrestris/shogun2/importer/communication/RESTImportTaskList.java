package de.terrestris.shogun2.importer.communication;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonRootName;

import de.terrestris.shogun2.importer.communication.RESTImportTask;

/**
 * terrestris GmbH & Co. KG
 *
 * @author ahenn
 * @date 15.04.2016
 */
@JsonRootName("tasks")
public class RESTImportTaskList extends ArrayList<RESTImportTask> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RESTImportTaskList() {

    }

}
