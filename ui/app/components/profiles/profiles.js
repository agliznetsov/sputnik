'use strict';

angular.module('sputnik').controller('ProfilesController', function ($scope, httpUtils) {

    $scope.refresh = function () {
        httpUtils.get("/dataProfiles").then(function (response) {
            $scope.items = response.data;
        });
    };

    $scope.add = function () {
        $scope.item = {
            name: 'profile',
            graphs: []
        };
    };

    $scope.edit = function (item) {
        $scope.item = angular.copy(item);
    };

    $scope.$on("cancel", function () {
       $scope.item = undefined;
        $scope.refresh();
    });

    $scope.$on("save", function () {
        $scope.item = undefined;
        $scope.refresh();
    });

    $scope.refresh();

});
