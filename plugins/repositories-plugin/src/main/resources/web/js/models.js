define( [
  'repositories'
  ],
  function ( repoConnectionApp ) {

    repoConnectionApp.service("repoModel", function() {
      this.repositories = JSON.parse(getRepos());
      this.selectedRepository = null;
    });

    repoConnectionApp.service("pentahoRepositoryModel",function() {
      this.displayName = "";
      this.url = "http://localhost:8080/pentaho-di";
      this.description = "";
    });

    repoConnectionApp.service("kettleFileRepositoryModel", function() {
      this.displayName = "";
      this.location = "";
      this.doNotModify = false;
      this.showHiddenFolders = false;
      this.description = "";
      this.isDefaultOnStartup = false;
    });

});
