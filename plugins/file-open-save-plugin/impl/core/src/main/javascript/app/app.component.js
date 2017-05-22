/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2017 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

/**
 * The File Open and Save Main component.
 *
 * This provides the main component for supporting the file open and save functionality.
 **/
define([
  "./services/data.service",
  "text!./app.html",
  "css!./app.css"
], function(dataService, template) {
  "use strict";

  var options = {
    bindings: {},
    template: template,
    controllerAs: "vm",
    controller: appController
  };

  appController.$inject = [dataService.name];

  function appController(dt) {
    var vm = this;
    vm.$onInit = onInit;
    vm.selectFolder = selectFolder;
    vm.selectFile = selectFile;
    vm.selectFolderByPath = selectFolderByPath;
    vm.doSearch = doSearch;
    vm.search = "";

    function onInit() {
      vm.wrapperClass = "open";
      vm.headerTitle = "Save";//i18n.get("file-open-save-plugin.app.header.save.title");
      vm.searchPlaceholder = "Search";//i18n.get("file-open-save-plugin.app.header.search.placeholder");
      vm.selectedFolder = "Recents";//i18n.get("file-open-save-plugin.app.header.save.title");
      vm.confirmButton = "Save";//i18n.get("file-open-save-plugin.app.save.button");
      vm.cancelButton = "Cancel";//i18n.get("file-open-save-plugin.app.cancel.button");
      vm.saveFileNameLabel = "File name";//i18n.get("file-open-save-plugin.app.save.file-name.label");
      vm.showRecents = true;
      vm.folder = {name: "Recents", path: "Recents"};
      dt.getDirectoryTree().then(populateTree);
      dt.getRecentFiles().then(populateRecentFiles);

      function populateTree(response) {
        vm.folders = response.data;
        for (var i = 0; i < vm.folders.length; i++) {
          if (vm.folders[i].depth === 0) {
            vm.folders[i].visible = true;
          }
        }
      }

      function populateRecentFiles(response) {
        vm.recentFiles = response.data;
      }

    }

    function selectFolder(folder) {
      if (folder) {
        vm.showRecents = false;
        vm.folder = folder;
        vm.selectedFolder = folder.name;
      } else {
        vm.showRecents = true;
        vm.selectedFolder = "Recents";
        vm.folder = {name: "Recents", path: "Recents"};
      }
    }

    function selectFolderByPath(path) {
      for (var i = 0; i < vm.folders.length; i++) {
        if (vm.folders[i].path===path) {
          selectFolder(vm.folders[i]);
        }
      }
    }

    function selectFile(file) {
      if (file.type === "folder") {
        selectFolder(file);
      } else {
        dt.openFile(file.objectId.id, file.type);
      }
    }

    function doSearch() {
      if (vm.showRecents === true) {
        for (var i = 0; i < vm.recentFiles.length; i++) {
          var name = vm.recentFiles[i].name.toLowerCase();
          vm.recentFiles[i].inResult = name.indexOf(vm.search.toLowerCase()) !== -1;
        }
      } else {
        for (var i = 0; i < vm.folder.children.length; i++) {
          var name = vm.folder.children[i].name.toLowerCase();
          vm.folder.children[i].inResult = name.indexOf(vm.search.toLowerCase()) !== -1;
        }
        for (i = 0; i < vm.folder.files.length; i++) {
          var name = vm.folder.files[i].name.toLowerCase();
          vm.folder.files[i].inResult = name.indexOf(vm.search.toLowerCase()) !== -1;
        }
      }
    }
  }

  return {
    name: "fileOpenSaveApp",
    options: options
  };
});
