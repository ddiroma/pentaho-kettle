package org.pentaho.repo.endpoint;

import org.pentaho.di.core.LastUsedFile;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.repo.controller.RepositoryBrowserController;
import org.pentaho.repo.model.RepositoryDirectory;
import org.pentaho.repo.model.RepositoryFile;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 5/12/17.
 */
public class RepositoryBrowserEndpoint {

  private RepositoryBrowserController repositoryBrowserController;

  public RepositoryBrowserEndpoint( RepositoryBrowserController repositoryBrowserController ) {
    this.repositoryBrowserController = repositoryBrowserController;
  }

  @GET
  @Path( "/loadDirectoryTree" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response loadDirectoryTree() {
    List<RepositoryDirectory> repositoryDirectories = repositoryBrowserController.loadDirectoryTree();
    if ( repositoryDirectories != null ) {
      return Response.ok( repositoryDirectories ).build();
    }

    return Response.noContent().build();
  }

  @GET
  @Path( "/loadFile" )
  public Response loadFile( @QueryParam( "id" ) String id, @QueryParam( "type" ) String type ) {
    if ( repositoryBrowserController.loadFile( id, type ) ) {
      return Response.ok().build();
    }

    return Response.noContent().build();
  }

  @GET
  @Path( "/saveFile" )
  public Response saveFile( @QueryParam( "path" ) String path, @QueryParam( "name" ) String name ) {
    if ( repositoryBrowserController.saveFile( path, name ) ) {
      return Response.ok().build();
    }

    return Response.noContent().build();
  }

  @GET
  @Path( "/recentFiles" )
  @Produces( { MediaType.APPLICATION_JSON } )
  public Response recentFiles() {
    PropsUI props = PropsUI.getInstance();

    List<RepositoryFile> repositoryFiles = new ArrayList<>();
    List<LastUsedFile> lastUsedFiles = props.getLastUsedFiles();
    for ( LastUsedFile lastUsedFile : lastUsedFiles ) {
      RepositoryFile repositoryFile = new RepositoryFile();
      repositoryFile.setType( lastUsedFile.isTransformation() ? "transformation" : "job" );
      repositoryFile.setName( lastUsedFile.getFilename() );
      repositoryFile.setPath( lastUsedFile.getDirectory() );
      repositoryFile.setDate( lastUsedFile.getLastOpened() );
      repositoryFiles.add( repositoryFile );
    }

    return Response.ok( repositoryFiles ).build();
  }
}
