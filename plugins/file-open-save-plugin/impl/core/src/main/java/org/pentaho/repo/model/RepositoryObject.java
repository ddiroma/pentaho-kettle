package org.pentaho.repo.model;

import org.pentaho.di.repository.ObjectId;

import java.util.Date;

/**
 * Created by bmorrise on 5/16/17.
 */
public class RepositoryObject {
  private String name;
  private ObjectId objectId;
  private String path;
  private String type;

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public ObjectId getObjectId() {
    return objectId;
  }

  public void setObjectId( ObjectId objectId ) {
    this.objectId = objectId;
  }

  public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }
}
