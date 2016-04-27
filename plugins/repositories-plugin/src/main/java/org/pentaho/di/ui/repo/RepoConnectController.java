/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.ui.repo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.List;
import java.util.Map;

/**
 * Created by bmorrise on 4/18/16.
 */
public class RepoConnectController {

  private RepositoriesMeta repositoriesMeta;
  private PluginRegistry pluginRegistry;
  private RepositoryMeta latestRepositoryMeta;

  public RepoConnectController( PluginRegistry pluginRegistry ) {
    this.pluginRegistry = pluginRegistry;
    repositoriesMeta = new RepositoriesMeta();
    try {
      repositoriesMeta.readData();
    } catch ( KettleException ke ) {
      System.out.println( ke );
      // Do something later
    }
  }

  public RepoConnectController() {
    this( PluginRegistry.getInstance() );
  }

  @SuppressWarnings( "unchecked" )
  public String getPlugins() {
    List<PluginInterface> plugins = pluginRegistry.getPlugins( RepositoryPluginType.class );
    JSONArray list = new JSONArray();
    for ( PluginInterface pluginInterface : plugins ) {
      if ( !pluginInterface.getIds()[0].equals( "PentahoEnterpriseRepository" ) ) {
        JSONObject repoJSON = new JSONObject();
        repoJSON.put( "id", pluginInterface.getIds()[ 0 ] );
        repoJSON.put( "name", pluginInterface.getName() );
        repoJSON.put( "description", pluginInterface.getDescription() );
        list.add( repoJSON );
      }
    }
    return list.toString();
  }

  public boolean createRepository( String id, Map<String, Object> items ) {
    try {
      RepositoryMeta repositoryMeta = pluginRegistry.loadClass( RepositoryPluginType.class, id, RepositoryMeta.class );
      repositoryMeta.populate( items );

      if ( repositoryMeta.getName() != null ) {
        repositoriesMeta.addRepository( repositoryMeta );
        repositoriesMeta.writeData();
        latestRepositoryMeta = repositoryMeta;
      }
    } catch ( KettleException ke ) {
      System.out.println( ke );
      // Do something later
    }
    return true;
  }

  @SuppressWarnings( "unchecked" )
  public String getRepositories() {
    JSONArray list = new JSONArray();
    if ( repositoriesMeta != null ) {
      for ( int i = 0; i < repositoriesMeta.nrRepositories(); i++ ) {
        String name = repositoriesMeta.getRepository( i ).getName();
        String description = repositoriesMeta.getRepository( i ).getDescription();
        String id = repositoriesMeta.getRepository( i ).getId();
        JSONObject repoJSON = new JSONObject();
        repoJSON.put( "id", id );
        repoJSON.put( "name", name );
        repoJSON.put( "description", description );
        list.add( repoJSON );
      }
    }
    return list.toString();
  }

  public void connectToRepository() {
    connectToRepository( latestRepositoryMeta );
  }

  public void connectToRepository( RepositoryMeta repositoryMeta ) {
    try {
      Repository repository =
        pluginRegistry.loadClass( RepositoryPluginType.class, repositoryMeta.getId(), Repository.class );
      repository.init( repositoryMeta );
      repository.connect( null, null );
      Spoon.getInstance().setRepository( repository );
    } catch ( KettleException ke ) {
      // Do something later
    }
  }

  public void connectToRepository( String name ) {
    RepositoryMeta repositoryMeta = repositoriesMeta.findRepository( name );
    connectToRepository( repositoryMeta );
  }

  public boolean deleteRepository( String name ) {
    RepositoryMeta repositoryMeta = repositoriesMeta.findRepository( name );
    int index = repositoriesMeta.indexOfRepository( repositoryMeta );
    repositoriesMeta.removeRepository( index );
    try {
      repositoriesMeta.writeData();
    } catch ( KettleException ke ) {
      // Do something later
    }
    return true;
  }
}
