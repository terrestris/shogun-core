package de.terrestris.shoguncore.importer.communication;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.ArrayList;

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
