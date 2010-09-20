package com.ldodds.slug.framework.config;

import java.util.logging.Level;

import com.hp.hpl.jena.db.*;
import com.hp.hpl.jena.shared.DoesNotExistException;

/**
 * Implementation of the Memory interface that stores data in a database. This implementation is intended 
 * for use where multiple threads are accessing a single shared memory held in a database, i.e. all 
 * accesses to the data are funnelled through read/write locks to the underlying Model.
 * 
 * @author ldodds
 */
class SharedDatabaseMemory extends MemoryImpl 
{
  private String _user;
  private String _pass;
  private String _modelURI;
  private String _dbURL;
  private String _dbName;
  private String _driver;
  
  public SharedDatabaseMemory(String user, String pass, 
      String modelURI, String dbURL, String dbName, 
      String driver) 
  {
    _user = user;
    _pass = pass;
    _modelURI = modelURI;
    _dbURL = dbURL;
    _dbName = dbName;
    _driver = driver;
  }

  public void load() 
  {
    if (_model != null)
    {
      return;
    }
    try {
		Class.forName(_driver);
	} catch (ClassNotFoundException e) {
		throw new RuntimeException(e);
	}
    DBConnection dbConnection = 
      new DBConnection(_dbURL, _user, _pass, _dbName);
    
    try
    {
      _model = ModelRDB.open(dbConnection, _modelURI);
    } catch (DoesNotExistException e)
    {
      _model = ModelRDB.createModel(dbConnection, _modelURI);
    }
	_logger.log(Level.INFO, "Memory Loaded");    
    return;
  }

  public void save() 
  {
    _model.close();
	_logger.log(Level.INFO, "Memory Saved");    
  }

}
