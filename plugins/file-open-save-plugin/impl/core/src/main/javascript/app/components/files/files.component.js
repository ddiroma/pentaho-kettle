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
 * The File Open and Save Files component.
 *
 * This provides the component for the Files list on search or files view.
 **/
define([
  "../../services/data.service",
  "text!./files.html",
  "css!./files.css"
], function(dataService, filesTemplate) {
  "use strict";

  var options = {
    bindings: {
      folder: '<',
      search: '<',
      onClick: '&',
      onSelect: '&'
    },
    template: filesTemplate,
    controllerAs: "vm",
    controller: filesController
  };

  filesController.$inject = ["$timeout", dataService.name, "$scope"];

  function filesController($timeout, dt, $scope) {
    var vm = this;
    vm.$onInit = onInit;
    vm.$onChanges = onChanges;
    vm.selectFile = selectFile;
    vm.commitFile = commitFile;
    vm.rename = rename;
    vm.sortFiles = sortFiles;

    function onInit() {
      vm.nameHeader = "Name";
      vm.typeHeader = "Type";
      vm.lastSaveHeader = "Last saved";
      vm.sortState = 0;
    }

    function onChanges(changes) {
      if (changes.folder) {
        vm.selectedFile = null;
        vm.sortState = 0
        if (vm.folder.children) {
          vm.originalChildrenFiles = JSON.parse(JSON.stringify(vm.folder.children))
        }
      }
    }

    function commitFile(file) {
      if (file.editing !== true) {
        vm.onClick({file:file});
      }
    }

    function selectFile(file) {
      vm.selectedFile = file;
      vm.onSelect({selectedFile:file});
    }

    function rename() {
      var path = vm.selectedFile.type === "File folder" ? vm.selectedFile.parent : vm.selectedFile.path;
      dt.rename(vm.selectedFile.objectId.id, vm.selectedFile.name, path, vm.selectedFile.type).then(function(response) {
        vm.selectedFile.objectId = response.data;
      });
    }

    function sortFiles () {
      switch (vm.sortState) {
        case 0:
          vm.sortState = 1
          vm.folder.children.sort(compareDescend)
          return
        case 1:
          vm.sortState = 2
          vm.folder.children.sort(compareAscend)
          return
        case 2:
          vm.sortState = 0
          vm.folder.children = JSON.parse(JSON.stringify(vm.originalChildrenFiles))
          return
        default:
          break
      }
    }

    function compareAscend (file1, file2) {
      if (file1.name.toLowerCase() < file2.name.toLowerCase()) {
        return -1
      }
      if (file1.name.toLowerCase() > file2.name.toLowerCase()) {
        return 1
      }
      return 0
    }

    function compareDescend (file1, file2) {
      if (file1.name.toLowerCase() < file2.name.toLowerCase()) {
        return 1
      }
      if (file1.name.toLowerCase() > file2.name.toLowerCase()) {
        return -1
      }
      return 0
    }
  }

  return {
    name: "files",
    options: options
  };
});
