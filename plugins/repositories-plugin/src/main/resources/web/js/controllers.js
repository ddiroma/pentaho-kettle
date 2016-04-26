define(
    [
        'angular',
        'angular-sanitize',
        'repositories/models'
    ],

  function(angular) {

    var repoConnectionAppControllers = angular.module('repoConnectionAppControllers', []);

    repoConnectionAppControllers.controller("PentahoRepositoryController", function($scope, $location) {
      $scope.finish = function() {

      };
      $scope.close = function() {
        close();
      };
    });

    repoConnectionAppControllers.controller("CreateNewConnectionController", function($scope, $location, $rootScope, repoModel) {
      $scope.repoModel = repoModel;
      $scope.selectRepository = function(repository) {
        repoModel.selectedRepository = repository;
      }
      $scope.close = function() {
        close();
      };
      $scope.getStarted = function(repository) {
        try {
          $rootScope.next();
          if ( repository.id == "KettleFileRepository" ) {
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
        return true;
      }
      $scope.finish = function() {
        createKettleFileRepository(JSON.stringify(kettleFileRepositoryModel));
      }
    });
  }
);
