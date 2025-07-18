/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.di.job.entries.unzip;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;

import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.bowl.DefaultBowl;
import org.pentaho.di.job.entry.loadSave.JobEntryLoadSaveTestSupport;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;

public class JobEntryUnZipTest extends JobEntryLoadSaveTestSupport<JobEntryUnZip> {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  @Override
  protected Class<JobEntryUnZip> getJobEntryClass() {
    return JobEntryUnZip.class;
  }

  @Override
  protected List<String> listCommonAttributes() {
    return Arrays.asList(
        "zipfilename",
        "wildcard",
        "wildcardexclude",
        "targetdirectory",
        "movetodirectory",
        "addfiletoresult",
        "isfromprevious",
        "adddate",
        "addtime",
        "addOriginalTimestamp",
        "SpecifyFormat",
        "date_time_format",
        "rootzip",
        "createfolder",
        "nr_limit",
        "wildcardSource",
        "success_condition",
        "create_move_to_directory",
        "setOriginalModificationDate" );
  }

  @Override
  protected Map<String, String> createGettersMap() {
    return toMap(
        "zipfilename", "getZipFilename",
        "wildcard", "getWildcard",
        "wildcardexclude", "getWildcardExclude",
        "targetdirectory", "getSourceDirectory",
        "movetodirectory", "getMoveToDirectory",
        "addfiletoresult", "isAddFileToResult",
        "isfromprevious", "getDatafromprevious",
        "adddate", "isDateInFilename",
        "addtime", "isTimeInFilename",
        "addOriginalTimestamp", "isOriginalTimestamp",
        "SpecifyFormat", "isSpecifyFormat",
        "date_time_format", "getDateTimeFormat",
        "rootzip", "isCreateRootFolder",
        "createfolder",  "isCreateFolder",
        "nr_limit", "getLimit",
        "wildcardSource", "getWildcardSource",
        "success_condition", "getSuccessCondition",
        "create_move_to_directory", "isCreateMoveToDirectory",
        "setOriginalModificationDate", "isOriginalModificationDate" );
  }

  @Override
  protected Map<String, String> createSettersMap() {
    return toMap(
        "zipfilename", "setZipFilename",
        "wildcard", "setWildcard",
        "wildcardexclude", "setWildcardExclude",
        "targetdirectory", "setSourceDirectory",
        "movetodirectory", "setMoveToDirectory",
        "addfiletoresult", "setAddFileToResult",
        "isfromprevious", "setDatafromprevious",
        "adddate", "setDateInFilename",
        "addtime", "setTimeInFilename",
        "addOriginalTimestamp", "setAddOriginalTimestamp",
        "SpecifyFormat", "setSpecifyFormat",
        "date_time_format", "setDateTimeFormat",
        "rootzip", "setCreateRootFolder",
        "createfolder",  "setCreateFolder",
        "nr_limit", "setLimit",
        "wildcardSource", "setWildcardSource",
        "success_condition", "setSuccessCondition",
        "create_move_to_directory", "setCreateMoveToDirectory",
        "setOriginalModificationDate", "setOriginalModificationDate" );
  }


  @Test
  public void unzipPostProcessingTest() throws Exception {

    JobEntryUnZip jobEntryUnZip = new JobEntryUnZip();
    JobMeta mockJobMeta = mock( JobMeta.class );
    when( mockJobMeta .getBowl() ).thenReturn( DefaultBowl.getInstance() );
    jobEntryUnZip.setParentJobMeta( mockJobMeta );

    Method unzipPostprocessingMethod = jobEntryUnZip.getClass().getDeclaredMethod( "doUnzipPostProcessing", FileObject.class, FileObject.class, String.class );
    unzipPostprocessingMethod.setAccessible( true );
    FileObject sourceFileObject = Mockito.mock( FileObject.class );
    Mockito.doReturn( Mockito.mock( FileName.class ) ).when( sourceFileObject ).getName();

    //delete
    jobEntryUnZip.afterunzip = 1;
    unzipPostprocessingMethod.invoke( jobEntryUnZip, sourceFileObject, Mockito.mock( FileObject.class ), "" );
    Mockito.verify( sourceFileObject, Mockito.times( 1 ) ).delete();

    //move
    jobEntryUnZip.afterunzip = 2;
    unzipPostprocessingMethod.invoke( jobEntryUnZip, sourceFileObject, Mockito.mock( FileObject.class ), "" );
    Mockito.verify( sourceFileObject, Mockito.times( 1 ) ).moveTo( Mockito.any() );
  }

}
