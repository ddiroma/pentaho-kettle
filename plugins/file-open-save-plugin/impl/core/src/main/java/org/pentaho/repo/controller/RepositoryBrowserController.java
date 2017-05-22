package org.pentaho.repo.controller;

import org.pentaho.repo.model.RepositoryDirectory;
import org.pentaho.repo.model.RepositoryFile;
import org.pentaho.di.core.EngineMetaInterface;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryExtended;
import org.pentaho.di.repository.RepositoryObjectInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 5/12/17.
 */
public class RepositoryBrowserController {

  public static final String TRANSFORMATION = "transformation";
  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  public boolean loadFile( String id, String type ) {
    try {
      getSpoon().getDisplay().asyncExec( () -> {
        try {
          getSpoon().loadObjectFromRepository( () -> id,
            type.equals( TRANSFORMATION ) ? RepositoryObjectType.TRANSFORMATION : RepositoryObjectType.JOB, null );
        } catch ( Exception e ) {
          // Log error in console
        }
      } );

    } catch ( Exception e ) {
      return false;
    }

    return true;
  }

  public boolean saveFile( String path, String name ) {

    try {
      RepositoryDirectoryInterface repositoryDirectoryInterface = getRepository().findDirectory( path );
      getSpoon().getDisplay().asyncExec( () -> {
        try {
          EngineMetaInterface meta = getSpoon().getActiveMeta();
          meta.setRepositoryDirectory( repositoryDirectoryInterface );
          meta.setName( name );
          getSpoon().saveToRepository( meta, false, false );
          getSpoon().delegates.tabs.renameTabs();
        } catch ( Exception e ) {
          System.out.println( e );
        }
      } );

    } catch ( Exception e ) {
      return false;
    }

    return true;
  }

  public List<RepositoryDirectory> loadDirectoryTree() {
    if ( getRepository() != null ) {
      RepositoryDirectoryInterface repositoryDirectoryInterface;
      try {
        if ( getRepository() instanceof RepositoryExtended ) {
          repositoryDirectoryInterface = ( (RepositoryExtended) getRepository() )
            .loadRepositoryDirectoryTree( "/", "*.ktr|*.kjb", -1, true, true, true );
        } else {
          repositoryDirectoryInterface = getRepository().loadRepositoryDirectoryTree();
        }
        List<RepositoryDirectory> repositoryDirectories = new LinkedList<>();
        int depth = getRepository().getName().equals( "Pentaho" ) ? -1 : 0;
        createRepositoryDirectory( repositoryDirectoryInterface, repositoryDirectories, depth, null );
        if ( getRepository().getName().equals( "Pentaho" ) ) {
          repositoryDirectories.remove( 0 );
        }
        return repositoryDirectories;
      } catch ( Exception e ) {
        return null;
      }
    }
    return null;
  }

  private void createRepositoryDirectory( RepositoryDirectoryInterface repositoryDirectoryInterface, List<RepositoryDirectory> repositoryDirectories, int depth, RepositoryDirectory parent ) {
    RepositoryDirectory repositoryDirectory = new RepositoryDirectory();
    repositoryDirectory.setName( repositoryDirectoryInterface.getName() );
    repositoryDirectory.setPath( repositoryDirectoryInterface.getPath() );
    repositoryDirectory.setObjectId( repositoryDirectoryInterface.getObjectId() );
    repositoryDirectory.setType( "folder" );
    repositoryDirectory.setDepth( depth );
    repositoryDirectories.add( repositoryDirectory );
    if ( parent != null ) {
      repositoryDirectory.setParent( parent.getPath() );
      parent.addChild( repositoryDirectory );
    }
    if ( !Utils.isEmpty( repositoryDirectoryInterface.getChildren() ) ) {
      repositoryDirectory.setHasChildren( true );
      for ( RepositoryDirectoryInterface child : repositoryDirectoryInterface.getChildren() ) {
        createRepositoryDirectory( child, repositoryDirectories, depth+1, repositoryDirectory );
      }
    }
    List<RepositoryElementMetaInterface> repositoryElementMetaInterfaces = new ArrayList<>();
    if ( repositoryDirectoryInterface.getRepositoryObjects() == null ) {
      try {
        repositoryElementMetaInterfaces =
          getRepository().getJobAndTransformationObjects( repositoryDirectoryInterface.getObjectId(), false );
      } catch ( KettleException ke ) {
        // Ignore for now
      }
    } else {
      repositoryElementMetaInterfaces = repositoryDirectoryInterface.getRepositoryObjects();
    }
    for ( RepositoryObjectInterface repositoryObject : repositoryElementMetaInterfaces ) {
      org.pentaho.di.repository.RepositoryObject ro = (org.pentaho.di.repository.RepositoryObject) repositoryObject;
      RepositoryFile repositoryFile = new RepositoryFile();
      repositoryFile.setName( repositoryObject.getName() );
      repositoryFile.setType( ro.getObjectType().getTypeDescription() );
      repositoryFile.setExtension( ro.getObjectType().getExtension() );
      repositoryFile.setDate( ro.getModifiedDate() );
      repositoryFile.setObjectId( ro.getObjectId() );
      repositoryFile.setPath( ro.getRepositoryDirectory().getPath() );
      repositoryDirectory.addFile( repositoryFile );
    }
  }

  private Spoon getSpoon() {
    return spoonSupplier.get();
  }

  private Repository getRepository() {
    return getSpoon().rep;
  }

}
