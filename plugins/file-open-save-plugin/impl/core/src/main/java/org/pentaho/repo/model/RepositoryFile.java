package org.pentaho.repo.model;

import java.util.Date;

/**
 * Created by bmorrise on 5/16/17.
 */
public class RepositoryFile extends RepositoryObject {

  private String extension;
  private Date date;
  private String type;

  public String getExtension() {
    return extension;
  }

  public void setExtension( String extension ) {
    this.extension = extension;
  }

  public Date getDate() {
    return date;
  }

  public void setDate( Date date ) {
    this.date = date;
  }
}
