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

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.ui.thin.ThinDialog;
import org.pentaho.platform.settings.ServerPort;
import org.pentaho.platform.settings.ServerPortRegistry;

import java.util.HashMap;

/**
 * Created by bmorrise on 2/21/16.
 */
public class RepoConnectionDialog extends ThinDialog {

  private static final int WIDTH = 630;
  private static final int HEIGHT = 630;
  private static final String TITLE = "New Repository Connection";
  private static final String WEB_CLIENT_PATH = "/repositories/web/index.html";
  private static final String OSGI_SERVICE_PORT = "OSGI_SERVICE_PORT";
  private RepoConnectController controller;
  private Shell shell;

  public RepoConnectionDialog( Shell shell ) {
    this( shell, new RepoConnectController() );
    System.out.println( "This loaded." );
  }

  public RepoConnectionDialog( Shell shell, RepoConnectController controller ) {
    super( shell, WIDTH, HEIGHT, TITLE, getRepoURL() );
    this.controller = controller;
    this.shell = shell;
  }

  public void open() {
    super.createDialog();

    new BrowserFunction( browser, "close" ) {
      @Override public Object function( Object[] arguments ) {
        dialog.dispose();
        return null;
      }
    };

    new BrowserFunction( browser, "getRepos" ) {
      @Override public Object function( Object[] objects ) {
        return controller.getPlugins();
      }
    };

    new BrowserFunction( browser, "selectLocation" ) {
      @Override public Object function( Object[] objects ) {
        DirectoryDialog directoryDialog = new DirectoryDialog( shell );
        return directoryDialog.open();
      }
    };

    new BrowserFunction( browser, "createKettleFileRepository" ) {
      @Override public Object function( Object[] objects ) {
        try {
          HashMap<String, String> fileRepository = new ObjectMapper().readValue( (String) objects[ 0 ], HashMap.class );
        } catch ( Exception e ) {
          // Do something later
        }
      }
    };

    while ( !dialog.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
  }


  private static Integer getOsgiServicePort() {
    // if no service port is specified try getting it from
    ServerPort osgiServicePort = ServerPortRegistry.getPort( OSGI_SERVICE_PORT );
    if ( osgiServicePort != null ) {
      return osgiServicePort.getAssignedPort();
    }
    return null;
  }

  private static String getRepoURL() {
    return "http://localhost:" + getOsgiServicePort() + WEB_CLIENT_PATH;
  }
}
