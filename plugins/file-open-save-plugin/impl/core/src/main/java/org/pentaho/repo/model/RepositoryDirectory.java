package org.pentaho.repo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 5/16/17.
 */
public class RepositoryDirectory extends RepositoryObject {

  private List<RepositoryDirectory> children = new ArrayList<>();
  private List<RepositoryFile> files = new ArrayList<>();
  private int depth;
  private String parent;
  private boolean hasChildren = false;

  public void addChild( RepositoryDirectory repositoryDirectory ) {
    this.children.add( repositoryDirectory );
  }

  public List<RepositoryDirectory> getChildren() {
    return children;
  }

  public List<RepositoryFile> getFiles() {
    return files;
  }

  public void addFile( RepositoryFile repositoryFile ) {
    this.files.add( repositoryFile );
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth( int depth ) {
    this.depth = depth;
  }

  public String getParent() {
    return parent;
  }

  public void setParent( String parent ) {
    this.parent = parent;
  }

  public boolean isHasChildren() {
    return hasChildren;
  }

  public void setHasChildren( boolean hasChildren ) {
    this.hasChildren = hasChildren;
  }
}
