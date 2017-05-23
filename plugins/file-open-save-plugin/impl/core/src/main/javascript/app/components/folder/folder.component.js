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
 * The File Open and Save Folder component.
 *
 * This provides the component for the Folders in the directory tree.
 **/
define([
  "text!./folder.html",
  "css!./folder.css"
], function (folderTemplate) {
  "use strict";

  var options = {
    bindings: {
      folders: '<',
      onSelect: '&',
      onOpen: '&',
      showRecents: '<',
      selectedFolder: '<'
    },
    template: folderTemplate,
    controllerAs: "vm",
    controller: folderController
  };

  function folderController() {
    var vm = this;
    vm.$onInit = onInit;
    vm.$onChanges = onChanges;
    vm.selectFolder = selectFolder;
    vm.openFolder = openFolder;
    vm.selectAndOpenFolder = selectAndOpenFolder;

    function onInit() {
    }

    function onChanges(changes) {
      if (changes.selectedFolder) {
        var selectedFolder = changes.selectedFolder.currentValue;
        if (selectedFolder && selectedFolder.path) {
          if (selectedFolder.path !== "Recents") {
            selectFolderByPath(selectedFolder.path);
          }
        }
      }
    }

    function isChild(folder, child) {
      return child.path.indexOf(folder.path) === 0;
    }

    function selectAndOpenFolder(folder) {
      openFolder(folder);
      selectFolder(folder);
    }

    function openFolder(folder) {
      if (folder.hasChildren) {
        folder.open = folder.open !== true;
      }
      for (var i = 0; i < vm.folders.length; i++) {
        if (folder.open === true && vm.folders[i].depth === folder.depth + 1 && isChild(folder, vm.folders[i])) {
          vm.folders[i].visible = true;
        } else if (folder.open === false && vm.folders[i].depth > folder.depth && isChild(folder, vm.folders[i])) {
          vm.folders[i].visible = false;
          vm.folders[i].open = false;
        }
      }
    }

    function selectFolder(folder) {
      if (folder === "recents") {
        vm.showRecents = true;
        vm.selectedFolder = null;
      } else {
        vm.selectedFolder = folder;
        vm.showRecents = false;
        vm.folder = folder;
      }
      vm.onSelect({selectedFolder: folder});
    }

    function selectFolderByPath(path) {
      for (var i = 0; i < vm.folders.length; i++) {
        if (vm.folders[i].path === path) {
          selectFolder(vm.folders[i]);
          openParentFolder(vm.folders[i].parent);
        }
      }
    }

    function openParentFolder(path) {
      if (path) {
        for (var i = 0; i < vm.folders.length; i++) {
          if (path.indexOf(vm.folders[i].path) === 0) {
            openParentFolders(vm.folders[i]);
          }
        }
      }
    }

    function openParentFolders(folder) {
      if (folder.hasChildren) {
        folder.open = true;
      }
      for (var i = 0; i < vm.folders.length; i++) {
        if (vm.folders[i].parent === folder.path) {
          vm.folders[i].visible = true;
        }
      }
    }
  }

  return {
    name: "folder",
    options: options
  };
});
