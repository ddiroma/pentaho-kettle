define(
    [
        'angular',
        'angular-sanitize',
        'repositories/models'
    ],

  function(angular) {

    var repoConnectionAppControllers = angular.module('repoConnectionAppControllers', []);

    repoConnectionAppControllers.controller("PentahoRepositoryController", function($scope, $location, $rootScope, pentahoRepositoryModel) {
      $scope.model = pentahoRepositoryModel;

      $scope.canFinish = function() {
        if (pentahoRepositoryModel.displayName == "" || pentahoRepositoryModel.url == "") {
          return false;
        }
        return true;
      }
      $scope.finish = function() {
        try {
        if (createRepository("PentahoEnterpriseRepository", JSON.stringify(pentahoRepositoryModel))) {
          $location.path("/pentaho-repository-creation-success")
        } else {

        }
        $rootScope.next();
        } catch (e) {
          alert(e);
        }
      };
      $scope.close = function() {
        close();
      };
    });

    repoConnectionAppControllers.controller("PentahoRepositoryCreationSuccessController", function($scope, $location, $rootScope, pentahoRepositoryModel) {
       $scope.createNewConnection = function() {
         try {
           $location.path("/pentaho-repository");
           $rootScope.next();
         } catch (e) {
           alert(e);
         }
       }
       $scope.connect = function() {
         $location.path("/pentaho-repository-connect");
         $rootScope.next();
       }
       $scope.close = function() {
         close();
       }
       $scope.successText = "Your connection was created and is ready to use.";
    });

    repoConnectionAppControllers.controller("CreateNewConnectionController", function($scope, $location, $rootScope, repositoryTypesModel) {
      $scope.model = repositoryTypesModel;
      $scope.selectRepositoryType = function(repositoryType) {
        repositoryTypesModel.selectedRepositoryType = repositoryType;
      }
      $scope.close = function() {
        close();
      };
      $scope.getStarted = function(repositoryType) {
        try {
          $rootScope.next();
          if ( repositoryType.id == "KettleFileRepository" ) {
            $location.path("/kettle-file-repository-details");
           }
        } catch ( e ) {
          alert(e);
        }
//        if ( repository.id == "" ) {
//          $location.path("/pentaho-repository-connection-details");
//        }
      }
    })

    repoConnectionAppControllers.controller("KettleDatabaseRepositoryDetailsController", function() {

    });

    repoConnectionAppControllers.controller("KettleFileRepositoryDetailsController", function($scope, $rootScope, $location, kettleFileRepositoryModel) {
      $scope.model = kettleFileRepositoryModel;
      $scope.selectLocation = function() {
        kettleFileRepositoryModel.location = selectLocation();
      }
      $scope.back = function() {
        $rootScope.back();
      }
      $scope.canFinish = function() {
        if (kettleFileRepositoryModel.displayName == "" || kettleFileRepositoryModel.location == "") {
          return false;
        }
        return true;
      }
      $scope.finish = function() {
        if (createRepository("KettleFileRepository", JSON.stringify(kettleFileRepositoryModel))) {
          $location.path("/kettle-file-repository-creation-success")
        } else {

        }
        $rootScope.next();
      }
    });

    repoConnectionAppControllers.controller("KettleFileRepositoryCreationSuccessController", function($scope, $rootScope, $location) {
      $scope.createNewConnection = function() {
        try {
          $location.path("/pentaho-repository");
          $rootScope.next();
        } catch (e) {
          alert(e);
        }
      }
      $scope.connect = function() {
        connectToRepository();
      }
      $scope.close = function() {
        close();
      }
      $scope.successText = "Your Kettle file repository was created and is ready to use.";
    });

    repoConnectionAppControllers.controller("RepositoryManagerController", function($scope, $rootScope, $location, repositoriesModel) {
      $scope.model = repositoriesModel;
      $scope.selectRepository = function(repository) {
        repositoriesModel.selectedRepository = repository;
      }
      $scope.setDefault = function(repository) {
        setDefaultRepository(repository);
      }
      $scope.edit = function(repository) {
        try {
          alert(repository.name);
        } catch (e) {
          alert(e);
        }
      }
      $scope.delete = function(repository) {
        deleteRepository(repository.name);
        for ( i = 0; i < repositoriesModel.repositories.length; i++) {
          if ( repositoriesModel.repositories[i].name == repository.name) {
            alert(i);
          }
        }
      }
      $scope.add = function() {
        $location.path("/pentaho-repository");
        $rootScope.next();
      }
      $scope.close = function() {
        close();
      }
    });
  }
);
